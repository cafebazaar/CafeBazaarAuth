# BazaarAuth

Using this library, you can always access your users without having to implement a local login. Firstly, this reduces uncertainty, lack of security, and bot users due to its unified login solution. It also reduces the cost of user authentication by email or text messages. Furthermore, you do not lose the user history and data in your app if they uninstall or change their device.

In the first step, Bazaar gives you a unique user id for each user/app which remains the same forever. More information about the user such as username, email, etc. will be supported in future releases after adding the user permission feature.

To start working with `BazaarAuth`, you'll need to add its dependency to your `build.gradle` file:

```
dependencies {
    implementation 'com.github.cafebazaar.CafeBazaarAuth:auth:[latest_version]"
}
```

You need to add jitPack as your maven dependency to download this package. Add following line into repositories section in gradle file:

```
maven { url 'https://jitpack.io' }
```

# How to use

## In-app login

To login the user, you'll need to create a `BazaarSignIn` object:

```
val signInOption = BazaarSignInOptions.Builder(SignInOption.DEFAULT_SIGN_IN).build()

client = BazaarSignIn.getClient(
    context,
    signInOption
)
```

At the moment, you can only retrieve the default information which includes `accountID`. More features will be added soon. After creating this object, you can receive the user login intent by recalling the following method and guide the user to the Bazaar login page:

```
val intent = client.getSignInIntent()
startActivityForResult(intent, REQ_CODE)
```

To parse the data from Bazaar and receive the account, recall the following method in `onActivityResult`:

```
val account = BazaarSignIn.getSignedInAccountFromIntent(intent)
```

In case the user has granted the login access, the returned `account` is not null and you can read data from the `account` model.

In case the user has already granted the access, use the following method to get the latest data:

```
BazaarSignIn.getLastSignedInAccount(this, this,BazaarSingInCallback { response ->
        val account = response?.data
        updateUI(account)
 })
```

In case the user has not granted the login access, the `account` value is null.

To receive the information by sync method, you can use the following method:
```
val accountResponse = BazaarSignIn.getLastSignedInAccountSync(this, this)
```
**Caution:** You are not able to call the above method on the Main thread.

To display Bazaar login button in your application, you can use the following View in your XML:

```
<com.farsitel.bazaar.auth.widget.LoginButton
    android:id="@+id/loginButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:size="NORMAL" />
```

The `size` value in this widget can be `BIG` or ` NORMAL` which sets the button view size.

## In-app storage

To save the user data, you'll need to call the following method:

```
 BazaarStorage.saveData(
                context = this@MainActivity,
                owner = this@MainActivity,
                data = "saved data".toByteArray(),
                callback = BazaarStorageCallback { savedResponse ->
                    dataTV.text = savedResponse?.data?.toReadableString()
                }
            )
``` 

To convert ByteArray response into string you can use `toReadableString` extension function.

To get saved data,  you'll need to call the following method:

```
BazaarStorage.getSavedData(
                context = this@MainActivity,
                owner = this@MainActivity,
                callback = BazaarStorageCallback { response ->
                    dataTV.text = response?.data?.toReadableString()
                }
            )
```

# Security notes

In order to prevent phishing and information theft, use the following method to ensure that the correct version of Bazaar is available on the user device:

```
BazaarClientProxy.isBazaarInstalledOnDevice(context)
```

# Bazaar in client device

To ensure that the Bazaar app version on the user device supports Bazaar login and in-app storage, use the following method:

```
BazaarClientProxy.isNeededToUpdateBazaar(context)
```

If the Bazaar app is not installed, you can use following method:

```
BazaarClientProxy.showInstallBazaarView(context)
```

In case an update for the Bazaar app is required, use the following method:

```
BazaarClientProxy.showUpdateBazaarView(context)
```
