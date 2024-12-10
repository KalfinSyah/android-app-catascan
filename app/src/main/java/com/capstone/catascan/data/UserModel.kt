package com.capstone.catascan.data

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)