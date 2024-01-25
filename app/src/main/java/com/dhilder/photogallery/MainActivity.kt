package com.dhilder.photogallery

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhilder.photogallery.databinding.ActivityMainBinding
import com.dhilder.photogallery.ui.viewmodel.PhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PhotoAdapter.OnImageClickListener {
    private lateinit var binding: ActivityMainBinding

    private var adapter = PhotoAdapter(emptyList(), this)
    private val viewModel: PhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSearchInputListener()
        setUpRecyclerView()
        setObserver()
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
            viewModel.getPhotos(searchText)
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.photoRecycler.layoutManager = layoutManager
    }

    private fun setObserver() {
        viewModel.list.observe(this) { getPhotoResponse ->
            if (getPhotoResponse.photos != null) {
                adapter = PhotoAdapter(getPhotoResponse.photos.photo, this)
                binding.photoRecycler.adapter = adapter
            }
        }
    }

    override fun onImageClick(url: String) {
        viewModel.photoUrl.value = url
    }
}