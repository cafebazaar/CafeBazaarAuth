package com.farsitel.bazaar.sample.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.farsitel.bazaar.auth.BazaarSignIn
import com.farsitel.bazaar.auth.BazaarSignInClient
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.auth.model.BazaarSignInOptions
import com.farsitel.bazaar.auth.model.SignInOption

class MainActivity : AppCompatActivity() {


    private lateinit var loginButton: View
    private lateinit var client: BazaarSignInClient
    private val REQ_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserAlreadySignedIn()

        loginButton = findViewById(R.id.loginButton)
    }

    private fun checkUserAlreadySignedIn() {
        val signInOption = BazaarSignInOptions.Builder(SignInOption.DEFAULT_SIGN_IN).build()
        client = BazaarSignIn.getClient(
            this,
            signInOption
        )

        BazaarSignIn.getLastSignedInAccount(this,
            this,
            BazaarSignInCallback { account ->
                updateUI(account)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            val account = BazaarSignIn.getSignedInAccountFromIntent(
                data
            )
            updateUI(account)
        }
    }

    private fun updateUI(account: BazaarSignInAccount?) {
        if (account == null) {
            loginButton.setOnClickListener {
                val intent = client.getSignInIntentWithScope()
                startActivityForResult(intent, REQ_CODE)
            }
        } else {
            loginButton.setOnClickListener(null)
            Toast.makeText(this, "you are login", Toast.LENGTH_LONG).show()
        }

    }
}
