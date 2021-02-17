package com.farsitel.bazaar.auth.connection

import android.os.Bundle
import com.farsitel.bazaar.ErrorResponse
import com.farsitel.bazaar.auth.model.BazaarSignInAccount

object AuthResponseHandler {

    private const val KEY_IAL_STATUS: String = "ialStatus"
    private const val KEY_IAL_ERROR_MESSAGE: String = "ialErrorMessage"
    private const val KEY_IAL_ACCOUNT_INFO_AID: String = "aid"
    private const val KEY_IAL_ACCOUNT_INFO_NICKNAME: String = "nickname"

    private const val ERROR_MESSAGE_UNKNOWN = "unknown error"
    private const val ERROR_ACCOUNT_NOT_EXISTS = "Account not exists for packageName"
    private const val ERROR_EXTRA_IS_NULL = "extra data is null"

    private const val IAL_STATUS_SUCCESS: Int = 0
    private const val IAL_STATUS_FAILED: Int = -1
    private const val IAL_STATUS_DEVELOPER_ERROR: Int = -2
    private const val IAL_STATUS_ACCOUNT_NOT_EXISTS: Int = -3
    private const val IAL_STATUS_EXTRA_IS_NULL: Int = -4

    fun isSuccessful(extras: Bundle?): Boolean {
        return extras?.getInt(KEY_IAL_STATUS) == IAL_STATUS_SUCCESS
    }

    fun getErrorResponse(extras: Bundle?): ErrorResponse {
        val errorCode = extras?.getInt(KEY_IAL_STATUS) ?: IAL_STATUS_EXTRA_IS_NULL
        val message = when (errorCode) {
            IAL_STATUS_FAILED,
            IAL_STATUS_DEVELOPER_ERROR -> extras?.getString(KEY_IAL_ERROR_MESSAGE)
            IAL_STATUS_ACCOUNT_NOT_EXISTS -> ERROR_ACCOUNT_NOT_EXISTS
            IAL_STATUS_EXTRA_IS_NULL -> ERROR_EXTRA_IS_NULL
            else -> ERROR_MESSAGE_UNKNOWN
        } ?: ERROR_MESSAGE_UNKNOWN

        return ErrorResponse(message, errorCode)
    }

    fun getAccountByBundle(extras: Bundle): BazaarSignInAccount? {

        val accountId = extras.getString(KEY_IAL_ACCOUNT_INFO_AID) ?: return null

        // in first version we don't this
        // val nickname = extras.getString(IAL_ACCOUNT_INFO_NICKNAME_KEY)

        return BazaarSignInAccount(accountId)
    }
}