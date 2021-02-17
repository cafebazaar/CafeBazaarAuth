package com.farsitel.bazaar.storage;

/**
* This class used to communicate with the inAppBazaar library to get/set data of the user.
*/
interface InAppBazaarStorage {

    /**
    * In this method we get saved data from bazaar
    * @params: packageName of the thirdParty application
    * @return: Bundle with the following data:
    *      PAYLOAD
    *      STATUS
    */
    Bundle getSavedData(String packageName);

    /**
    * Save data of user and return the bundle that specified state of it.
    * @params: packageName of the thirdParty application
    *          data that need to save
    * @return: Bundle with the following data:
           STATUS
           PAYLOAD
    */
    Bundle saveData(String packageName, String data);
}