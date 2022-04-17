package com.farsitel.bazaar.storage.connection

import android.os.Bundle
import com.farsitel.bazaar.ErrorResponse

object StorageResponseHandler {

    private const val KEY_IAL_STATUS: String = "ialStatus"
    private const val KEY_IAL_DATA_STATUS: String = "ialdStatus" // this is added for bazaar versions < 19.2.0
    private const val KEY_IAL_ERROR_MESSAGE: String = "ialErrorMessage"
    private const val ERROR_MESSAGE_UNKNOWN = "unknown error"

    private const val KEY_SAVED_DATA: String = "payload"

    private const val IAL_STATUS_SUCCESS: Int = 0
    private const val IAL_STATUS_FAILED: Int = -1
    private const val IAL_STATUS_DEVELOPER_ERROR: Int = -2
    private const val IAL_STATUS_ACCOUNT_NOT_EXISTS: Int = -3
    private const val IAL_STATUS_EXTRA_IS_NULL: Int = -4

    private const val ACCOUNT_NOT_EXITS = "Account not exists for packageName"
    private const val ERROR_EXTRA_IS_NULL = "extra data is null"

    fun isSuccessful(extras: Bundle?): Boolean {
        return extras?.let {
            it.containsKeyWithSuccessValue(KEY_IAL_STATUS) || it.containsKeyWithSuccessValue(KEY_IAL_DATA_STATUS)
        } ?: false
    }

    fun getErrorResponse(extras: Bundle?): ErrorResponse? {
        val errorCode = extras?.getInt(KEY_IAL_STATUS) ?: IAL_STATUS_EXTRA_IS_NULL
        val message = when (errorCode) {
            IAL_STATUS_FAILED,
            IAL_STATUS_DEVELOPER_ERROR -> extras?.getString(KEY_IAL_ERROR_MESSAGE)
            IAL_STATUS_ACCOUNT_NOT_EXISTS -> ACCOUNT_NOT_EXITS
            IAL_STATUS_EXTRA_IS_NULL -> ERROR_EXTRA_IS_NULL
            else -> ERROR_MESSAGE_UNKNOWN
        } ?: ERROR_MESSAGE_UNKNOWN

        return ErrorResponse(message, errorCode)
    }

    fun getSavedData(extras: Bundle?): String? {
        return extras?.getString(KEY_SAVED_DATA)
    }

    private fun Bundle.containsKeyWithSuccessValue(key: String): Boolean {
        return containsKey(key).and(getInt(key) == IAL_STATUS_SUCCESS)
    }
}