package com.farsitel.bazaar.storage.connection

import android.os.Bundle

object StorageResponseHandler {

    private const val KEY_IAL_STATUE: String = "ialStatus"
    private const val KEY_IAL_ERROR_MESSAGE: String = "ialErrorMessage"

    private const val KEY_SAVED_DATA: String = "payload"

    private const val IAL_STATUE_SUCCESS: Int = 0
    private const val IAL_STATUE_FAILED: Int = -1
    private const val IAL_STATUE_DEVELOPER_ERROR: Int = -2
    private const val IAL_STATUE_ACCOUNT_NOT_EXISTS: Int = -3

    fun isSuccessful(extras: Bundle): Boolean {
        return extras.getInt(KEY_IAL_STATUE) == IAL_STATUE_SUCCESS
    }

    fun getErrorMessage(extras: Bundle): String? {
        return when (extras.getInt(KEY_IAL_STATUE)) {
            IAL_STATUE_FAILED,
            IAL_STATUE_DEVELOPER_ERROR -> extras.getString(KEY_IAL_ERROR_MESSAGE)
            IAL_STATUE_ACCOUNT_NOT_EXISTS -> "Account not exists for packageName"
            else -> null
        }
    }

    fun getSavedData(extras: Bundle): String? {
        return extras.getString(KEY_SAVED_DATA)
    }
}