package com.playsoutrcndemo

import android.app.Application
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint.createDefault
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import android.util.Log

class MainApplication : Application(), ReactApplication {

  // Flutter engine ID for caching and reuse
    private val FLUTTER_ENGINE_ID = "my_flutter_engine"

    private val TAG = "MainApplication"

    private val CHANNEL = "com.playsout.minigames"

  override val reactHost: ReactHost by lazy {
    getDefaultReactHost(
      context = applicationContext,
      packageList =
        PackageList(this).packages.apply {
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // add(MyReactNativePackage())
          add(PlaysoutPackage()) // Add PlaysoutPackage
        },
    )
  }

  override fun onCreate() {
    super.onCreate()
    loadReactNative(this)

    // Initialize FlutterEngine when Application starts
        initFlutterEngine()
  }

    /**
     * Initialize FlutterEngine and set initial route
     */
    private fun initFlutterEngine() {
    Log.d(TAG, "Initializing FlutterEngine")
    try {
        // Create FlutterEngine instance
        val flutterEngine = FlutterEngine(this)
        
        // Unified initial route parameters
        val initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzY0MTI0NDc3fS5zaWc"
        Log.d(TAG, "Set initial route: $initialRoute")
        flutterEngine.navigationChannel.setInitialRoute(initialRoute)
        
        // Execute Flutter entry point
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        val channel = MethodChannel(flutterEngine.dartExecutor, CHANNEL)
        val arguments = hashMapOf(
            "appAdId" to "ca-app-pub-3940256099942544/1712485313", // change them by your admob ad Id
            "gameAdId" to "ca-app-pub-3940256099942544/1712485313"
        )
        channel.invokeMethod("init", arguments)
        
        // Cache FlutterEngine for later use
        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)
        Log.d(TAG, "FlutterEngine cached with ID: $FLUTTER_ENGINE_ID")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize FlutterEngine: ${e.message}", e)
    }
}

    /**
     * Get cached FlutterEngine
     * Can be obtained in other components through MainApplication.getInstance().getCachedFlutterEngine()
     */
    fun getCachedFlutterEngine(): FlutterEngine? {
        return FlutterEngineCache.getInstance().get(FLUTTER_ENGINE_ID)
    }

    // Get Application instance with singleton pattern (optional)
    companion object {
        private var instance: MainApplication? = null

        fun getInstance(): MainApplication? {
            return instance
        }
    }
}
