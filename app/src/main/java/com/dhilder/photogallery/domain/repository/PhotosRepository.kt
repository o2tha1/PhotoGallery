package com.dhilder.photogallery.domain.repository

import com.dhilder.photogallery.domain.model.PhotosResponse
import retrofit2.Call

interface PhotosRepository {
    suspend fun getPhotos(tags: String): Call<PhotosResponse>
}