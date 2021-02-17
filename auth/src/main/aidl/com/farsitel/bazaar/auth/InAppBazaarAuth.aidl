package com.farsitel.bazaar.auth;

/**
* This class used to communicate with the inAppBazaar library to get the account info of the user from Bazaar
*/
interface InAppBazaarAuth {
    /**
    *  @params: packageName of the thirdParty application
    *  @Return: Bundle with the following data:
            ID
            STATUS
    */
    Bundle getLastAccountId(String packageName);
}
