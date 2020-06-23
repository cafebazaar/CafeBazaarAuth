# BazaarAuth

Using this library, you can always access your users without having to implement a local login. Firstly, this reduces uncertainty/lack of security and bot users due to its unified login solution. It also reduces the cost of user authentication by email or text message. Furthermore, you do not lose user’s history and data in your app after uninstalling or changing the device.
In the first step, Bazzar gives you a unique user id for each user/app which remains the same forever. More fields of user’s information such as username, email and etc. will be available in the future after the addition of the user's permission feature.

To start working with BazaaAuth, you need to add it's dependency into your build.gradle file:

Dependency

```
dependencies {
    implementation "com.farsitel.bazaar:auth:[latest_version]"
}
```

# How to use

To login the user, you need to create a CafeSignIn object.

```
val signInOption = CafeSignInOptions.Builder(SignInOption.DEFAULT_SIGN_IN).build()
client = CafeSignIn.getClient(
    context,
    signInOption
)
```

At the moment you can only retrieve the default information including accountID. More features features will be added soon. After creating this object, you can receive the user login intent by recalling the following method and guid the user to Bazaar login page.

```
val intent = client.getSignInIntentWithScope()
startActivityForResult(intent, REQ_CODE)
```

To parse the data from CafeBazaar and receive the account recall the following method in onActivityResult.

```
val account = CafeSignIn.getSignedInAccountFromIntent(intent)
```

In case user has granted the access the account returning value is not null and you can read data from accout model.

In case the user has already granted the login access to get latest data, use the following method.

```
CafeSignIn.getLastSignedInAccount(this, this,CafeSingInCallback { account ->
        updateUI(account)
 })
```

In case the user has already granted the login access,the account has a value otherwise it's null

In order to receive information by sync method , you can use the following method
Caution: You are not able to call this method on the Main thread

```
val account = CafeSignIn.getLastSignedInAccountSync(this, this)
```

To display Bazaar login button  in your application you can use the following View in in your XML

```
<com.farsitel.bazaar.auth.widget.LoginButton
    android:id="@+id/loginButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:size="NORMAL" />
```

The size value in this widget can be `BIG` or` NORMAL` which sets the button view size.


# Security issues

To ensure that the correct version of Bazaar app is available on user’s device ( in order to prevent phishing and information theft ) use the following method.

```
CafeHelper.isBazaarInstalledOnDevice(context)
```

# Bazaar in client device

To ensure that the user’s app version is capable of Bazaar’s login, use the following method. 

```
CafeHelper.isBazaarNeedToUpdate(context)
```

If Bazaar app is not installed, you can use following method.

```
CafeHelper.showInstallBazaarView(context)
```

In case an update for Bazaar app is required, use the following method

```
CafeHelper.showUpdateBazaarView(context)
```
