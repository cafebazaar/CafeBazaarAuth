package com.farsitel.bazaar

class BazaarResponse<T>(
    val isSuccessful: Boolean,
    val errorResponse: ErrorResponse? = null,
    val data: T? = null
)

class ErrorResponse(
    val errorMessage: String,
    val errorCode: Int
)