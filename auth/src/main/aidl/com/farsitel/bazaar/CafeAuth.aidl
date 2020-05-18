// CafeAuth.aidl
package com.farsitel.bazaar;

// Declare any non-default types here with import statements

interface CafeAuth {
    Bundle getAccountInfoByScopePermission(String packageName, in int[] scopes);
    Bundle getLastAccountInfo(String packageName, in int[] scopes);
}
