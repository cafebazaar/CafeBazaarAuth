package com.farsitel.bazaar.auth.model

data class CafeSignInAccount(
    val accountId: String,
    val nickname: String? = null,
    val avatarUrl: String? = null,
    val email: String? = null,
    val phone: String? = null
)