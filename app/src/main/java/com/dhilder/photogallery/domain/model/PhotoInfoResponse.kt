package com.dhilder.photogallery.domain.model

data class PhotoInfoResponse(
    val photo: Photo
)

data class Photo(
    val id: String,
    val owner: Owner,
    val title: Title,
    val description: Description,
    val dates: Dates
)

data class Owner(
    val nsid: String,
    val username: String
)

data class Title(
    val _content: String,
)

data class Description(
    val _content: String,
)

data class Dates(
    val posted: String,
    val taken: String
)