package com.farsitel.bazaar.auth.connection

import android.os.Bundle
import com.farsitel.bazaar.auth.model.CafeSignInAccount

object AuthResponseHandler {

    private const val IAL_STATUE_KEY: String = "ialStatus"
    private const val IAL_ERROR_MESSAGE_KEY: String = "ialErrorMessage"
    private const val IAL_ACCOUNT_INFO_AID_KEY: String = "aid"
    private const val IAL_ACCOUNT_INFO_NICKNAME_KEY: String = "nickname"

    private const val IAL_STATUE_SUCCESS: Int = 0
    private const val IAL_STATUE_FAILED: Int = -1
    private const val IAL_STATUE_DEVELOPER_ERROR: Int = -2
    private const val IAL_STATUE_ACCOUNT_NOT_EXISTS: Int = -3

    fun isSuccessful(extras: Bundle): Boolean =
        extras.getInt(IAL_STATUE_KEY) == IAL_STATUE_SUCCESS

    fun getErrorMessage(extras: Bundle): String? =
        when (extras.getInt(IAL_STATUE_KEY)) {
            IAL_STATUE_FAILED,
            IAL_STATUE_DEVELOPER_ERROR -> extras.getString(IAL_ERROR_MESSAGE_KEY)
            IAL_STATUE_ACCOUNT_NOT_EXISTS -> "Account not exists for packageName"
            else -> null
        }

    fun getAccountByBundle(extras: Bundle): CafeSignInAccount? {

        val accountId = extras.getString(IAL_ACCOUNT_INFO_AID_KEY) ?: return null
        val nickname = extras.getString(IAL_ACCOUNT_INFO_NICKNAME_KEY)

        return CafeSignInAccount(accountId, nickname)
    }
}