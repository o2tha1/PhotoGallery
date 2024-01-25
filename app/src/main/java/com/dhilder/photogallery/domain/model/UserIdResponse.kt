package com.dhilder.photogallery.domain.model

data class UserIdResponse(
    val user: User?
)

data class User(
    val nsid: String,
)