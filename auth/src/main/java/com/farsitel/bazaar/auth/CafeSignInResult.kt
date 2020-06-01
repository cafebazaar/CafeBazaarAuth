package com.farsitel.bazaar.auth

import android.content.Intent
import com.farsitel.bazaar.auth.model.CafeSignInAccount
import org.json.JSONObject

object CafeSignInResult {

    private const val RESPONSE_KEY = "response"
    private const val ACCOUNT_ID_JSON_KEY = "accountId"
    private const val NICKNAME_JSON_KEY = "nickname"

    fun getAccountFromIntent(intent: Intent?): CafeSignInAccount? {
        if (intent == null) {
            return null
        }

        val responseJson = intent.extras?.getString(RESPONSE_KEY) ?: return null
        val accountJson = JSONObject(responseJson)
        val accountId = accountJson.getString(ACCOUNT_ID_JSON_KEY)
        // in first version we don't this
        // val nickname = accountJson.getString(NICKNAME_JSON_KEY)

        return CafeSignInAccount(accountId)
    }
}