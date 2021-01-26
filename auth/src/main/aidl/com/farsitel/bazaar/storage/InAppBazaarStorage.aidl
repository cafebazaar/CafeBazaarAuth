// BazaarStorage.aidl
package com.farsitel.bazaar.storage;

// Declare any non-default types here with import statements

interface InAppBazaarStorage {
    Bundle getSavedData(String packageName);
    Bundle saveData(String packageName, String data);
}