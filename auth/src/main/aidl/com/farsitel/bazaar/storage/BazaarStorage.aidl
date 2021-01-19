// BazaarStorage.aidl
package com.farsitel.bazaar.storage;

// Declare any non-default types here with import statements

interface BazaarStorage {
    Bundle getSavedData(String packageName);
    Bundle saveData(String packageName);
}