package com.dhilder.photogallery.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhilder.photogallery.domain.model.PhotoInfoResponse
import com.dhilder.photogallery.domain.model.PhotosResponse
import com.dhilder.photogallery.domain.repository.PhotosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(private val photosRepository: PhotosRepository) :
    ViewModel() {
    val list = MutableLiveData<PhotosResponse>()
    var info = MutableLiveData<PhotoInfoResponse>()

    suspend fun getPhotos(tags: String) {
        val response = photosRepository.getPhotos(tags)
        response.enqueue(object : Callback<PhotosResponse> {
            override fun onResponse(
                call: Call<PhotosResponse>,
                response: Response<PhotosResponse>
            ) {
                list.postValue(response.body())
            }

            override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
                Log.w(TAG, t)
            }
        })
    }

    suspend fun getPhotoInfo(id: String) {
        val response = photosRepository.getPhotoInfo(id)
        response.enqueue(object : Callback<PhotoInfoResponse> {
            override fun onResponse(
                call: Call<PhotoInfoResponse>,
                response: Response<PhotoInfoResponse>
            ) {
                info.postValue(response.body())
            }

            override fun onFailure(call: Call<PhotoInfoResponse>, t: Throwable) {
                Log.w(TAG, t)
            }
        })
    }

    companion object {
        private const val TAG = "PhotoViewModel"
    }
}