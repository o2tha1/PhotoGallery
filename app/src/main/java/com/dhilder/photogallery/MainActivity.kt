package com.dhilder.photogallery

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.dhilder.photogallery.databinding.ActivityMainBinding
import com.dhilder.photogallery.domain.model.PhotoMetadata
import com.dhilder.photogallery.ui.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private var metadataList: MutableState<List<PhotoMetadata>> = mutableStateOf(emptyList())
    private val viewModel: PhotoViewModel by viewModels()
    private var lastClickedPhoto: PhotoMetadata? = null
    private var tagMode = TagMode.OR
    private var searchMode = SearchMode.TAG
    private var shouldShowNoResultsPrompt = mutableStateOf(false)
    private var shouldShowIntroTitle = mutableStateOf(true)
    private var shouldShowIntroText = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Column {
                    SearchOptions()
                    IntroText()
                    NoResultsText()
                    PhotoList()
                }
            }
        }

        setSearchInputListener()
        setPhotoListObserver()
        setPhotoInfoObserver()
        setUserIdObserver()
        checkLaunchBehaviour()
    }

    @Composable
    private fun SearchOptions() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var isSearchByUser by remember {
                mutableStateOf(false)
            }

            SearchToggleOption(
                prompt = resources.getString(R.string.search_type),
                textOff = resources.getString(R.string.search_option_tag),
                textOn = resources.getString(R.string.search_option_user),
                updateState = { isSearchByUser = it })

            if (!isSearchByUser) {
                TagToggleOption(
                    prompt = resources.getString(R.string.tag_type),
                    textOff = resources.getString(R.string.search_option_or),
                    textOn = resources.getString(R.string.search_option_and)
                )
            }
        }
    }

    @Composable
    private fun SearchToggleOption(
        prompt: String,
        textOff: String,
        textOn: String,
        updateState: (Boolean) -> Unit
    ) {
        ToggleOption(
            prompt = prompt,
            textOff = textOff,
            textOn = textOn,
            listener = { _, isSearchByUser ->
                updateState(isSearchByUser)
                setSearchMode(isSearchByUser)
                refreshSearch()
            })
    }

    @Composable
    private fun TagToggleOption(prompt: String, textOff: String, textOn: String) {
        ToggleOption(
            prompt = prompt,
            textOff = textOff,
            textOn = textOn,
            listener = { _, isExclusive ->
                setTagMode(isExclusive)
                refreshSearch()
            })
    }

    @Composable
    private fun ToggleOption(
        prompt: String,
        textOff: String,
        textOn: String,
        listener: CompoundButton.OnCheckedChangeListener
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = prompt,
                color = Color.White
            )
            AndroidView(factory = { ToggleButton(it) }) { toggleButton ->
                toggleButton.apply {
                    this.textOff = textOff
                    this.textOn = textOn
                    isChecked = isChecked
                    setOnCheckedChangeListener(listener)
                }
            }
        }
    }

    @Composable
    private fun IntroText() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            if (shouldShowIntroTitle.value) {
                Text(
                    text = resources.getString(R.string.default_title),
                    modifier = Modifier.padding(top = 32.dp),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            if (shouldShowIntroText.value) {
                Text(
                    text = resources.getString(R.string.default_prompt),
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }
    }

    @Composable
    private fun NoResultsText() {
        if (shouldShowNoResultsPrompt.value) {
            Text(
                text = resources.getString(R.string.no_results_prompt),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 32.dp, end = 32.dp),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

    @Composable
    private fun PhotoList() {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        ) {
            itemsIndexed(items = metadataList.value) { _, photoMetadata ->
                RoundedPhoto(photoMetadata)
                UserInfo(photoMetadata.getBuddyIcons(), photoMetadata.owner)
                ImageTags(photoMetadata.tags)
            }
        }
    }

    @Composable
    private fun RoundedPhoto(photoMetadata: PhotoMetadata) {
        val url = photoMetadata.url_l
        if (url.isEmpty()) {
            return
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Box {
                AsyncImage(
                    model = url,
                    contentDescription = resources.getString(
                        R.string.content_description_image,
                        photoMetadata.title
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onImageClick(photoMetadata) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    @Composable
    private fun UserInfo(buddyIcons: String, owner: String) {
        Row(
            modifier = Modifier.padding(start = 32.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserIcon(buddyIcons, owner)
            Username(owner)
        }
    }

    @Composable
    private fun UserIcon(buddyIcons: String, owner: String) {
        if (buddyIcons.isEmpty()) {
            return
        }

        Card(shape = RoundedCornerShape(30.dp)) {
            Box {
                AsyncImage(
                    model = buddyIcons,
                    contentDescription = "",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onUserClick(owner) },
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }

    @Composable
    private fun Username(owner: String) {
        if (owner.isEmpty()) {
            return
        }

        Text(
            text = owner,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onUserClick(owner) },
            style = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp
            )
        )
    }

    @Composable
    private fun ImageTags(tags: String) {
        if (tags.isEmpty()) {
            return
        }

        Text(
            text = getString(R.string.tags, tags),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
        )
    }

    private fun setSearchMode(isSearchByUser: Boolean) {
        searchMode = if (isSearchByUser) SearchMode.USER_ID else SearchMode.TAG
    }

    private fun setTagMode(isExclusive: Boolean) {
        tagMode = if (isExclusive) TagMode.AND else TagMode.OR
    }

    private fun refreshSearch() {
        val currentQuery = binding.searchView.query
        binding.searchView.setQuery(currentQuery, true)
    }

    private fun setSearchInputListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getPhotos(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) getPhotos(newText) else getInterestingPhotos()
                return false
            }
        })
    }

    private fun getPhotos(searchText: String) {
        lifecycleScope.launch {
            if (searchMode == SearchMode.TAG) {
                viewModel.getPhotosByTag(searchText, tagMode.value)
            } else {
                viewModel.getUserId(searchText)
            }
        }
        hideDefaultUi()
    }

    private fun setPhotoListObserver() {
        viewModel.list.observe(this) { getPhotoResponse ->
            if (getPhotoResponse.photos != null) {
                metadataList.value = getPhotoResponse.photos.photo
                shouldShowNoResultsPrompt.value = metadataList.value.isEmpty()
            }
        }
    }

    private fun setPhotoInfoObserver() {
        viewModel.info.observe(this) { response ->
            val photoInfo = response.photo
            val photoMetadata = lastClickedPhoto

            // TODO: Improve this section; i.e. pass just the ID and use that to fetch the rest
            val intent = Intent(this, DetailsActivity::class.java)
            if (photoMetadata != null) {
                intent.putExtra(OWNER, photoMetadata.owner)
                intent.putExtra(TITLE, photoMetadata.title)
                intent.putExtra(URL_L, photoMetadata.url_l)
                intent.putExtra(BUDDY_ICONS, photoMetadata.getBuddyIcons())
                intent.putExtra(TAGS, photoMetadata.tags)
            }
            intent.putExtra(OWNER_NAME, photoInfo.owner.username)
            intent.putExtra(DESCRIPTION, photoInfo.description._content)
            intent.putExtra(DATE_UPLOAD, formatDate(photoInfo.dates.posted))
            intent.putExtra(DATE_TAKEN, photoInfo.dates.taken)

            startActivity(intent)
        }
    }

    private fun formatDate(date: String): String {
        val datePosted = Date(TimeUnit.SECONDS.toMillis(date.toLong()))
        val dateTemplate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateTemplate.format(datePosted)
    }

    private fun setUserIdObserver() {
        viewModel.user.observe(this) { response ->
            if (response.user != null) {
                getPhotosByUser(response.user.nsid)
            }
        }
    }

    private fun checkLaunchBehaviour() {
        if (intent.getBooleanExtra(SHOULD_USER_SEARCH, false)) {
            val owner = intent.getStringExtra(OWNER) ?: return
            onUserClick(owner)
        } else {
            getInterestingPhotos()
        }
    }

    private fun getInterestingPhotos() {
        lifecycleScope.launch {
            viewModel.getPhotosByInteresting()
            shouldShowIntroTitle.value = true
            binding.searchView.queryHint = ""
        }
    }

    private fun onImageClick(metadata: PhotoMetadata) {
        lifecycleScope.launch {
            lastClickedPhoto = metadata
            viewModel.getPhotoInfo(metadata.id)
        }
    }

    private fun onUserClick(owner: String) {
        binding.searchView.setQuery("", false)
        binding.searchView.queryHint = owner
        getPhotosByUser(owner)
    }

    private fun getPhotosByUser(user: String) {
        lifecycleScope.launch {
            viewModel.getPhotosByUserId(user)
            hideDefaultUi()
        }
    }

    private fun hideDefaultUi() {
        shouldShowIntroTitle.value = false
        shouldShowIntroText.value = false
    }

    companion object {
        private enum class TagMode(val value: String) {
            AND("all"),
            OR("any")
        }

        private enum class SearchMode {
            TAG,
            USER_ID
        }

        const val TITLE = "title"
        const val URL_L = "url_l"
        const val OWNER = "owner"
        const val OWNER_NAME = "owner_name"
        const val BUDDY_ICONS = "buddy_icons"
        const val DESCRIPTION = "description"
        const val DATE_UPLOAD = "date_upload"
        const val DATE_TAKEN = "date_taken"
        const val TAGS = "tags"
        const val SHOULD_USER_SEARCH = "should_user_search"
    }
}