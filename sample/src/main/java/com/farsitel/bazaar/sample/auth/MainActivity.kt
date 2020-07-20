package com.farsitel.bazaar.sample.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.farsitel.bazaar.BazaarClientProxy
import com.farsitel.bazaar.auth.BazaarSignIn
import com.farsitel.bazaar.auth.BazaarSignInClient
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.auth.model.BazaarSignInOptions
import com.farsitel.bazaar.auth.model.SignInOption
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: View
    private lateinit var client: BazaarSignInClient

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

        updateBazaar.setOnClickListener {
            BazaarClientProxy.showUpdateBazaarView(context = this)
        }

        installBazaar.setOnClickListener {
            BazaarClientProxy.showInstallBazaarView(context = this)
        }

        accountId.text = "try to get last signedIn account"
        BazaarSignIn.getLastSignedInAccount(
            context = this,
            owner = this,
            callback = BazaarSignInCallback { account ->
                accountId.text = "Account is fetched"
                updateUI(account)
            })

        if (!BazaarClientProxy.isBazaarInstalledOnDevice(this)) {
            BazaarClientProxy.showInstallBazaarView(this)
        } else if (BazaarClientProxy.isNeededToUpdateBazaar(this)) {
            BazaarClientProxy.showUpdateBazaarView(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            val account = BazaarSignIn.getSignedInAccountFromIntent(data)
            updateUI(account)
        }
    }

    private fun updateUI(account: BazaarSignInAccount?) {
        if (account == null) {
            loginButton.setOnClickListener {
                val intent = client.getSignInIntent()
                startActivityForResult(intent, REQ_CODE)
            }
        } else {
            loginButton.setOnClickListener(null)
            accountId.text = account.accountId
            Toast.makeText(this, "you are login", Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val REQ_CODE = 123
    }
}
