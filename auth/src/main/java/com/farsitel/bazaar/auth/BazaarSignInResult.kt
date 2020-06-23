package com.farsitel.bazaar.auth

import android.content.Intent
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import org.json.JSONObject

object BazaarSignInResult {

    private const val KEY_RESPONSE = "response"
    private const val KEY_ACCOUNT_ID_JSON = "accountId"
    private const val KEY_NICKNAME_JSON = "nickname"

    fun getAccountFromIntent(intent: Intent?): BazaarSignInAccount? {
        if (intent == null) {
            return null
        }

        val responseJson = intent.extras?.getString(KEY_RESPONSE) ?: return null
        val accountJson = JSONObject(responseJson)
        val accountId = accountJson.getString(KEY_ACCOUNT_ID_JSON)
        // in first version we don't this
        // val nickname = accountJson.getString(NICKNAME_JSON_KEY)

        return BazaarSignInAccount(accountId)
    }
}