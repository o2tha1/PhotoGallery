package com.dhilder.photogallery.di

import com.dhilder.photogallery.data.repository.PhotosRepositoryImpl
import com.dhilder.photogallery.domain.repository.PhotosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMyRepository(photosRepositoryImpl: PhotosRepositoryImpl): PhotosRepository
}