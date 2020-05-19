package com.farsitel.bazaar.auth.model

class CafeSignInOptions {

    internal var signInOption: SignInOption? = null
        private set

    fun getScopes(): List<PermissionScope> = listOf(PermissionScope.PROFILE)

    class Builder(private val option: SignInOption) {
        fun build() = CafeSignInOptions().apply {
            this.signInOption = option
        }
    }
}

enum class SignInOption {
    DEFAULT_SIGN_IN
}


enum class PermissionScope {
    PROFILE,
    EMAIL,
    PHONE
}