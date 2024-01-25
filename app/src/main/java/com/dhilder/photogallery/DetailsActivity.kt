package com.dhilder.photogallery

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dhilder.photogallery.databinding.ActivityDetailsBinding
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle()
        setUserId()
        setDescription()
        setUploadDate()
        setTakenDate()
        setTags()
        setImage()
        setUserIcon()

        setUserOnClick()
        setShareOnClick()
    }

    private fun setTitle() {
        val title = intent.getStringExtra(MainActivity.TITLE)
        binding.imageTitle.text = title
    }

    private fun setUserId() {
        val ownerName = intent.getStringExtra(MainActivity.OWNER_NAME)
        binding.userId.text = ownerName
    }

    private fun setUserOnClick() {
        val owner = intent.getStringExtra(MainActivity.OWNER)

        binding.userLayout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.SHOULD_USER_SEARCH, true)
            intent.putExtra(MainActivity.OWNER, owner)
            finish()
            startActivity(intent)
        }

    }

    private fun setDescription() {
        val description = intent.getStringExtra(MainActivity.DESCRIPTION)

        if (description.isNullOrEmpty()) {
            binding.imageDescription.visibility = View.GONE
        } else {
            binding.imageDescription.visibility = View.VISIBLE
            binding.imageDescription.text = description
        }
    }

    private fun setUploadDate() {
        val dateUpload = intent.getStringExtra(MainActivity.DATE_UPLOAD)
        binding.imageUpload.text = getString(R.string.uploaded_on, dateUpload)
    }

    private fun setTakenDate() {
        val dateTaken = intent.getStringExtra(MainActivity.DATE_TAKEN)
        binding.imageTaken.text = getString(R.string.taken_on, dateTaken)
    }

    private fun setTags() {
        val tags = intent.getStringExtra(MainActivity.TAGS)

        if (tags.isNullOrEmpty()) {
            binding.imageTags.visibility = View.GONE
        } else {
            binding.imageTags.text = getString(R.string.tags, tags)
            binding.imageTags.visibility = View.VISIBLE
        }
    }

    private fun setImage() {
        val url = intent.getStringExtra(MainActivity.URL_L)
        Picasso.get().load(url).into(binding.imageView)
    }

    private fun setUserIcon() {
        val buddyIcons = intent.getStringExtra(MainActivity.BUDDY_ICONS)
        Picasso.get().load(buddyIcons).into(binding.userIcon)
    }

    private fun setShareOnClick() {
        val url = intent.getStringExtra(MainActivity.URL_L)

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)

            startActivity(Intent.createChooser(shareIntent, null))
        }
    }
}