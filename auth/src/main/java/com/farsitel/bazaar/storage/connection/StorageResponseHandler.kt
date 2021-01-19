package com.farsitel.bazaar.storage.connection

import android.os.Bundle
import com.farsitel.bazaar.ErrorResponse

object StorageResponseHandler {

    private const val KEY_IAL_STATUE: String = "ialStatus"
    private const val KEY_IAL_ERROR_MESSAGE: String = "ialErrorMessage"
    private const val ERROR_MESSAGE_UNKNOWN = "unknown error"

    private const val KEY_SAVED_DATA: String = "payload"

    private const val IAL_STATUE_SUCCESS: Int = 0
    private const val IAL_STATUE_FAILED: Int = -1
    private const val IAL_STATUE_DEVELOPER_ERROR: Int = -2
    private const val IAL_STATUE_ACCOUNT_NOT_EXISTS: Int = -3
    private const val IAL_STATUE_EXTRA_IS_NULL: Int = -4

    private const val ACCOUNT_NOT_EXITS = "Account not exists for packageName"
    private const val ERROR_EXTRA_IS_NULL = "extra data is null"

    fun isSuccessful(extras: Bundle?): Boolean {
        return extras?.getInt(KEY_IAL_STATUE) == IAL_STATUE_SUCCESS
    }

    fun getErrorResponse(extras: Bundle?): ErrorResponse? {
        val errorCode = extras?.getInt(KEY_IAL_STATUE) ?: IAL_STATUE_EXTRA_IS_NULL
        val message = when (errorCode) {
            IAL_STATUE_FAILED,
            IAL_STATUE_DEVELOPER_ERROR -> extras?.getString(KEY_IAL_ERROR_MESSAGE)
            IAL_STATUE_ACCOUNT_NOT_EXISTS -> ACCOUNT_NOT_EXITS
            IAL_STATUE_EXTRA_IS_NULL -> ERROR_EXTRA_IS_NULL
            else -> ERROR_MESSAGE_UNKNOWN
        } ?: ERROR_MESSAGE_UNKNOWN

        return ErrorResponse(message, errorCode)
    }

    fun getSavedData(extras: Bundle?): String? {
        return extras?.getString(KEY_SAVED_DATA)
    }
}