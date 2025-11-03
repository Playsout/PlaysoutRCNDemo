package com.playsoutrcndemo

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import android.util.Log

class PlaysoutUIModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val TAG = "PlaysoutUIModule_aaaaa"
    
    // 模块名称，在JavaScript端通过NativeModules.PlaysoutUIModule访问
    override fun getName() = "PlaysoutUIModule"
    
    // 使用@ReactMethod注解暴露方法给JavaScript调用
    @ReactMethod
    fun showFlutter() {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity == null) {
            Log.e(TAG, "Current activity is null")
            return
        }
        
        if (currentActivity is MainActivity) {
            // 在UI线程中执行
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