package com.playsoutrcndemo

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class PlaysoutPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        // Register our module
        return listOf(
            PlaysoutUIModule(reactContext) // Add PlaysoutUI module
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        // This module doesn't provide UI components, so return empty list
        return emptyList()
    }
}