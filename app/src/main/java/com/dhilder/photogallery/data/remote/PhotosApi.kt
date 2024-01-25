package com.dhilder.photogallery.data.remote

import com.dhilder.photogallery.domain.model.PhotoInfoResponse
import com.dhilder.photogallery.domain.model.PhotosResponse
import com.dhilder.photogallery.domain.model.UserIdResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApi {
    @GET("rest")
    fun getPhotosByTag(
        @Query("method") method: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String,
        @Query("tags") tags: String,
        @Query("tag_mode") tagMode: String,
        @Query("extras") extras: String,
        @Query("nojsoncallback") noJsonCallback: Int
    ): Call<PhotosResponse>

    @GET("rest")
    fun getPhotosByUserId(
        @Query("method") method: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String,
        @Query("user_id") userId: String,
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

    @GET("rest")
    fun getUserId(
        @Query("method") method: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String,
        @Query("username") username: String,
        @Query("nojsoncallback") noJsonCallback: Int
    ): Call<UserIdResponse>
}