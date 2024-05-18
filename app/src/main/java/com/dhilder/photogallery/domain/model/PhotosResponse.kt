package com.dhilder.photogallery.domain.model

data class PhotosResponse(
    val photos: Photos?
)

data class Photos(
    val photo: ArrayList<PhotoMetadata>
)

data class PhotoMetadata(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val tags: String,
    val url_l: String
) {
    fun getBuddyIcons(): String {
        return "http://farm${farm}.staticflickr.com/${server}/buddyicons/${owner}.jpg"
    }
}