package com.dhilder.photogallery.data.repository

import com.dhilder.photogallery.data.remote.PhotosApi
import com.dhilder.photogallery.domain.repository.PhotosRepository
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(private val photosApi: PhotosApi) :
    PhotosRepository {
    override suspend fun getPhotos(tags: String) = photosApi.getPhotos(
        METHOD_SEARCH,
        API_KEY,
        FORMAT,
        tags,
        EXTRAS_SEARCH,
        NO_JSON_CALLBACK
    )

    override suspend fun getPhotoInfo(photoId: String) = photosApi.getPhotoInfo(
        METHOD_GET_INFO,
        API_KEY,
        FORMAT,
        photoId,
        NO_JSON_CALLBACK
    )

    companion object {
        private const val METHOD_SEARCH = "flickr.photos.search"
        private const val METHOD_GET_INFO = "flickr.photos.getInfo"
        private const val API_KEY = "af33688f970958902ff4ecad2ef77b6b"
        private const val FORMAT = "json"
        private const val EXTRAS_SEARCH = "tags, url_l"
        private const val NO_JSON_CALLBACK = 1
    }
}