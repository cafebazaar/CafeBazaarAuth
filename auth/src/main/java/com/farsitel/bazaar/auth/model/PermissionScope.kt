package com.farsitel.bazaar.auth.model

class BazaarSignInOptions private constructor(
    internal val signInOption: SignInOption,
    private val requestNickName: Boolean
) {

    fun getScopes(): List<PermissionScope> {
        val result = mutableListOf(PermissionScope.ACCOUNT_ID)
        if (requestNickName) {
            result.add(PermissionScope.PROFILE)
        }

        return result
    }

    class Builder(private val option: SignInOption) {
        private var requestNickName: Boolean = false
        fun build() = BazaarSignInOptions(option, requestNickName)

        // You can't set this for now
        private fun requestForNickname() {
            requestNickName = true
        }
    }
}

enum class SignInOption {
    DEFAULT_SIGN_IN
}


enum class PermissionScope {
    ACCOUNT_ID,
    PROFILE,
    EMAIL,
    PHONE
}