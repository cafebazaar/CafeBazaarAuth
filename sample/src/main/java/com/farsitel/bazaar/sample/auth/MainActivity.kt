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
import com.farsitel.bazaar.storage.BazaarStorage
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.util.ext.toReadableString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: View
    private lateinit var client: BazaarSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserAlreadySignedIn()
        checkInBackThread()

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

        getData.setOnClickListener {
            BazaarStorage.getSavedData(
                context = this@MainActivity,
                owner = this@MainActivity,
                callback = BazaarStorageCallback {
                    dataTV.text = it?.data?.toReadableString()
                }
            )
        }

        setData.setOnClickListener {
            BazaarStorage.saveData(
                context = this@MainActivity,
                owner = this@MainActivity,
                data = dataET.text.toString().toByteArray(),
                callback = BazaarStorageCallback {
                    dataTV.text = it?.data?.toReadableString()
                }
            )
        }

        accountId.text = "try to get last signedIn account"
        BazaarSignIn.getLastSignedInAccount(
            context = this,
            owner = this,
            callback = BazaarSignInCallback { response ->
                accountId.text = "Account is fetched"
                updateUI(response?.data)
            })

        if (!BazaarClientProxy.isBazaarInstalledOnDevice(this)) {
            BazaarClientProxy.showInstallBazaarView(this)
        } else if (BazaarClientProxy.isNeededToUpdateBazaar(this).needToUpdateForStorage) {
            BazaarClientProxy.showUpdateBazaarView(this)
        }
    }

    private fun checkInBackThread() {

        Thread {
            val response = BazaarSignIn.getLastSignedInAccountSync(
                context = this,
                owner = this
            )

            if (response?.isSuccessful == true) {
                println("Use already logged in ${response.data?.accountId}")
            }
        }.start()
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
