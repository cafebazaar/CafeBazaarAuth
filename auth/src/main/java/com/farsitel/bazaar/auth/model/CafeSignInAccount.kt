package com.farsitel.bazaar.auth.model

data class CafeSignInAccount(
    val accountId: String,
    val nickname: String?,
    val avatarUrl: String?,
    val email: String?,
    val phone: String?
)