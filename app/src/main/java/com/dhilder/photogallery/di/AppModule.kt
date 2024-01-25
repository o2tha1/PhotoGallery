package com.dhilder.photogallery.di

import com.dhilder.photogallery.data.remote.PhotosApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePhotosApi(): PhotosApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.flickr.com/services/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PhotosApi::class.java)
    }
}