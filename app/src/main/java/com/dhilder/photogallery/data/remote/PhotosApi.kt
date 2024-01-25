package com.dhilder.photogallery.data.remote

import com.dhilder.photogallery.domain.model.PhotoInfoResponse
import com.dhilder.photogallery.domain.model.PhotosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApi {
    @GET("rest")
    fun getPhotos(
        @Query("method") method: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String,
        @Query("tags") tags: String,
        @Query("extras") extras: String,
        @Query("nojsoncallback") noJsonCallback: Int
    ): Call<PhotosResponse>

    @GET("rest")
    fun getPhotoInfo(
        @Query("method") method: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String,
        @Query("photo_id") photoId: String,
        @Query("nojsoncallback") noJsonCallback: Int
    ): Call<PhotoInfoResponse>
}