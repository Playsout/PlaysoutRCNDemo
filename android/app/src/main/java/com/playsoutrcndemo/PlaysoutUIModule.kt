package com.playsoutrcndemo

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import android.util.Log

class PlaysoutUIModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val TAG = "PlaysoutUIModule_aaaaa"
    
    // Module name, accessed in JavaScript via NativeModules.PlaysoutUIModule
    override fun getName() = "PlaysoutUIModule"
    
    // Expose method to JavaScript using @ReactMethod annotation
    @ReactMethod
    fun showFlutter() {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity == null) {
            Log.e(TAG, "Current activity is null")
            return
        }
        
        if (currentActivity is MainActivity) {
            // Execute on UI thread
            currentActivity.runOnUiThread {
                currentActivity.showFlutter()
            }
        } else {
            Log.e(TAG, "Current activity is not MainActivity")
        }
    }
    
    @ReactMethod
    fun hideFlutter() {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity == null) {
            Log.e(TAG, "Current activity is null")
            return
        }
        
        if (currentActivity is MainActivity) {
            // 在UI线程中执行
            currentActivity.runOnUiThread {
                currentActivity.hideFlutter()
            }
        } else {
            Log.e(TAG, "Current activity is not MainActivity")
        }
    }
}