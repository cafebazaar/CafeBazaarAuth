package com.farsitel.bazaar.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.farsitel.bazaar.auth.CafeSignIn
import com.farsitel.bazaar.auth.CafeSignInClient
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.model.CafeSignInAccount
import com.farsitel.bazaar.auth.model.CafeSignInOptions
import com.farsitel.bazaar.auth.model.SignInOption

class MainActivity : AppCompatActivity() {


    private lateinit var loginButton: Button
    private lateinit var client: CafeSignInClient
    private val REQ_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserAlreadySingIn()

        loginButton = findViewById(R.id.login)
    }

    private fun checkUserAlreadySingIn() {
        val signInOption = CafeSignInOptions.Builder(SignInOption.DEFAULT_SIGN_IN).build()
        client = CafeSignIn.getClient(
            this,
            signInOption
        )

        CafeSignIn.getLastSignedInAccount(this,
            this,
            CafeSingInCallback { account ->
                updateUI(account)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            val account =
                CafeSignIn.getSignedInAccountFromIntent(
                    data
                )
            updateUI(account)
        }
    }

    private fun updateUI(account: CafeSignInAccount?) {
        loginButton.text = if (account == null) {
            loginButton.setOnClickListener {
                val intent = client.getSignInIntentWithScope()
                startActivityForResult(intent, REQ_CODE)
            }
            "login"
        } else {
            loginButton.setOnClickListener(null)
            "welcome"
        }

    }
}
