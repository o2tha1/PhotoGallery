package com.dhilder.photogallery.domain.repository

import com.dhilder.photogallery.domain.model.PhotoInfoResponse
import com.dhilder.photogallery.domain.model.PhotosResponse
import com.dhilder.photogallery.domain.model.UserIdResponse
import retrofit2.Call

interface PhotosRepository {
    suspend fun getPhotosByTag(tags: String, tagMode: String): Call<PhotosResponse>

    suspend fun getPhotosByUserId(userId: String): Call<PhotosResponse>

    suspend fun getPhotoInfo(photoId: String): Call<PhotoInfoResponse>

    suspend fun getUserId(username: String): Call<UserIdResponse>
}