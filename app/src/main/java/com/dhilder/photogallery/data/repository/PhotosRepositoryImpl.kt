package com.dhilder.photogallery.data.repository

import com.dhilder.photogallery.data.remote.PhotosApi
import com.dhilder.photogallery.domain.repository.PhotosRepository
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(private val photosApi: PhotosApi) :
    PhotosRepository {
    override suspend fun getPhotos(tags: String) = photosApi.getPhotos(
        METHOD,
        API_KEY,
        FORMAT,
        tags,
        EXTRAS_SEARCH,
        NO_JSON_CALLBACK
    )

    companion object {
        private const val METHOD = "flickr.photos.search"
        private const val API_KEY = "af33688f970958902ff4ecad2ef77b6b"
        private const val FORMAT = "json"
        private const val EXTRAS_SEARCH = "tags, url_l"
        private const val NO_JSON_CALLBACK = 1
    }
}