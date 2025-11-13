package com.playsoutrcndemo

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

import android.os.Bundle
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.android.FlutterView 
import android.util.Log 

class MainActivity : ReactActivity() {
  // Flutter engine ID for caching and reusing Flutter engine
    private val FLUTTER_ENGINE_ID = "my_flutter_engine"
    // FlutterFragment instance
    private var flutterFragment: FlutterFragment? = null
    // Whether Flutter view is showing
    private var showingFlutter = false

    private val TAG = "MainActivity"

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "PlaysoutRCNDemo"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

// Add Fragment lifecycle listener
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "Activity created, FlutterEngine should be initialized by Application")
    
    // Add Fragment lifecycle listener
    supportFragmentManager.addOnBackStackChangedListener {
        // Check back stack, if empty, FlutterFragment has been removed
        if (supportFragmentManager.backStackEntryCount == 0) {
            Log.d(TAG, "FlutterFragment removed from back stack")
            showingFlutter = false
            flutterFragment = null
        }
    }
}

// Modify showFlutter method to ensure FlutterFragment is displayed correctly each time
fun showFlutter() {
    Log.d(TAG, "showFlutter called, showingFlutter: $showingFlutter")
    
    try {
        // Create new Fragment regardless of showingFlutter state
        // First check if Fragment exists, remove if present
        if (flutterFragment != null) {
            val existingTransaction = supportFragmentManager.beginTransaction()
            existingTransaction.remove(flutterFragment!!)
            existingTransaction.commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions() // Execute pending transactions immediately
        }
        
        // Create a new FlutterFragment instance
        flutterFragment = FlutterFragment.withCachedEngine(FLUTTER_ENGINE_ID).build()
        Log.d(TAG, "FlutterFragment created with cached engine")
        
        // Add new Fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(android.R.id.content, flutterFragment!!)
        transaction.addToBackStack(null) // Add to back stack
        transaction.commitAllowingStateLoss()
        
        showingFlutter = true
        Log.d(TAG, "FlutterFragment added to activity")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to show Flutter: ${e.message}", e)
        showingFlutter = false
    }
}

// Can also add an explicit back button handling method
fun handleBackPress(): Boolean {
    if (showingFlutter) {
        hideFlutter()
        return true
    }
    return false
}

    /**
     * Hide Flutter view and return to React Native view
     * This method can be called from React Native native module
     */
    fun hideFlutter() {
        if (showingFlutter && flutterFragment != null) {
            // Remove FlutterFragment
            supportFragmentManager
                .beginTransaction()
                .remove(flutterFragment!!)
                .commit()
            
            flutterFragment = null
            showingFlutter = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Clean up cached Flutter engine
        FlutterEngineCache.getInstance().remove(FLUTTER_ENGINE_ID)
    }
}
