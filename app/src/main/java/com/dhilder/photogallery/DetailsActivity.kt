package com.dhilder.photogallery

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
    }

    private fun setTitle() {
        val title = intent.getStringExtra(MainActivity.TITLE)
        binding.imageTitle.text = title
    }

    private fun setUserId() {
        val ownerName = intent.getStringExtra(MainActivity.OWNER_NAME)
        binding.userId.text = ownerName
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
        val urlL = intent.getStringExtra(MainActivity.URL_L)
        Picasso.get().load(urlL).into(binding.imageView)
    }

    private fun setUserIcon() {
        val buddyIcons = intent.getStringExtra(MainActivity.BUDDY_ICONS)
        Picasso.get().load(buddyIcons).into(binding.userIcon)
    }
}