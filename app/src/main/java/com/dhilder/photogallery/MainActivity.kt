package com.dhilder.photogallery

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
class MainActivity : AppCompatActivity(), PhotoAdapter.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private var adapter = PhotoAdapter(emptyList(), this)
    private val viewModel: PhotoViewModel by viewModels()
    private var lastClickedPhoto: PhotoMetadata? = null
    private var tagMode = TagMode.OR
    private var searchMode = SearchMode.TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTagMode(binding.toggleTagType.isChecked)
        setSearchMode(binding.toggleSearchType.isChecked)

        setTagModeVisibility()
        setTagModeListener()
        setSearchInputListener()
        setUpRecyclerView()
        setPhotoListObserver()
        setPhotoInfoObserver()
        setUserIdObserver()
        checkLaunchBehaviour()
    }

    private fun setTagModeVisibility() {
        binding.toggleSearchType.setOnCheckedChangeListener { _, isSearchByUser ->
            if (isSearchByUser) {
                binding.tagTypePrompt.visibility = View.INVISIBLE
                binding.toggleTagType.visibility = View.INVISIBLE
            } else {
                binding.tagTypePrompt.visibility = View.VISIBLE
                binding.toggleTagType.visibility = View.VISIBLE
            }
            setSearchMode(isSearchByUser)
            refreshSearch()
        }
    }

    private fun setSearchMode(isSearchByUser: Boolean) {
        searchMode = if (isSearchByUser) SearchMode.USER_ID else SearchMode.TAG
    }

    private fun setTagModeListener() {
        binding.toggleTagType.setOnCheckedChangeListener { _, isExclusive ->
            setTagMode(isExclusive)
            refreshSearch()
        }
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
                getPhotos(newText)
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

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.photoRecycler.layoutManager = layoutManager
    }

    private fun setPhotoListObserver() {
        viewModel.list.observe(this) { getPhotoResponse ->
            if (getPhotoResponse.photos != null) {
                adapter = PhotoAdapter(getPhotoResponse.photos.photo, this)
                binding.photoRecycler.adapter = adapter
                showNoResultsPrompt(adapter.itemCount == 0)
            }
        }
    }

    private fun showNoResultsPrompt(shouldShow: Boolean) {
        if (shouldShow) {
            binding.noResultsPrompt.visibility = View.VISIBLE
        } else {
            binding.noResultsPrompt.visibility = View.GONE
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
            lifecycleScope.launch {
                viewModel.getPhotosByInteresting()
            }
        }
    }

    override fun onImageClick(metadata: PhotoMetadata) {
        lifecycleScope.launch {
            lastClickedPhoto = metadata
            viewModel.getPhotoInfo(metadata.id)
        }
    }

    override fun onUserClick(owner: String) {
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
        binding.defaultTitle.visibility = View.GONE
        binding.defaultPrompt.visibility = View.GONE
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