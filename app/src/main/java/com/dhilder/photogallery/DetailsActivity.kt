package com.dhilder.photogallery

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

class DetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                ImageTitle()
                ShareableImage()
                UserInfo()
                ImageDescription()
                UploadDate()
                DateTaken()
                ImageTags()
            }
        }
    }

    @Composable
    private fun ImageTitle() {
        val title = intent.getStringExtra(MainActivity.TITLE)
        if (title.isNullOrEmpty()) {
            return
        }

        Text(
            text = title,
            modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp
            )
        )
    }

    @Composable
    private fun ShareableImage() {
        val url = intent.getStringExtra(MainActivity.URL_L)
        if (url.isNullOrEmpty()) {
            return
        }

        val title = intent.getStringExtra(MainActivity.TITLE) ?: ""

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Box {
                AsyncImage(
                    model = url,
                    contentDescription = resources.getString(
                        R.string.content_description_image,
                        title
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = resources.getString(R.string.content_description_share),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { launchSharesheet() }
                    )
                }
            }
        }
    }

    @Composable
    private fun UserInfo() {
        Row(
            modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserIcon()
            Username()
        }
    }

    @Composable
    private fun UserIcon() {
        val buddyIcons = intent.getStringExtra(MainActivity.BUDDY_ICONS)
        if (buddyIcons.isNullOrEmpty()) {
            return
        }

        Card(shape = RoundedCornerShape(30.dp)) {
            Box {
                AsyncImage(
                    model = buddyIcons,
                    contentDescription = "",
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { launchSearchByUser() },
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }

    @Composable
    private fun Username() {
        val ownerName = intent.getStringExtra(MainActivity.OWNER_NAME)
        if (ownerName.isNullOrEmpty()) {
            return
        }

        Text(
            text = ownerName,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable { launchSearchByUser() },
            style = TextStyle(
                color = Color.Blue,
                fontSize = 18.sp
            )
        )
    }

    @Composable
    private fun ImageDescription() {
        val description = intent.getStringExtra(MainActivity.DESCRIPTION)
        if (description.isNullOrEmpty()) {
            return
        }

        Text(
            text = description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 32.dp, end = 32.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            )
        )
    }

    @Composable
    private fun UploadDate() {
        val dateUpload = intent.getStringExtra(MainActivity.DATE_UPLOAD)
        if (dateUpload.isNullOrEmpty()) {
            return
        }

        Text(
            text = getString(R.string.uploaded_on, dateUpload),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 32.dp, end = 32.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            )
        )
    }

    @Composable
    private fun DateTaken() {
        val dateTaken = intent.getStringExtra(MainActivity.DATE_TAKEN)
        if (dateTaken.isNullOrEmpty()) {
            return
        }

        Text(
            text = getString(R.string.taken_on, dateTaken),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
        )
    }

    @Composable
    private fun ImageTags() {
        val tags = intent.getStringExtra(MainActivity.TAGS)
        if (tags.isNullOrEmpty()) {
            return
        }

        Text(
            text = getString(R.string.tags, tags),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 32.dp, end = 32.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
        )
    }

    private fun launchSearchByUser() {
        val owner = intent.getStringExtra(MainActivity.OWNER)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.SHOULD_USER_SEARCH, true)
        intent.putExtra(MainActivity.OWNER, owner)

        finish()
        startActivity(intent)
    }

    private fun launchSharesheet() {
        val url = intent.getStringExtra(MainActivity.URL_L)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)

        startActivity(Intent.createChooser(shareIntent, null))
    }
}