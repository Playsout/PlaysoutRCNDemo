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
import android.util.Log

class MainApplication : Application(), ReactApplication {

  // Flutter引擎ID，用于缓存和重用
    private val FLUTTER_ENGINE_ID = "my_flutter_engine"

    private val TAG = "MainApplication_aaaaa"

  override val reactHost: ReactHost by lazy {
    getDefaultReactHost(
      context = applicationContext,
      packageList =
        PackageList(this).packages.apply {
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // add(MyReactNativePackage())
          add(PlaysoutPackage()) // 添加PlaysoutPackage
        },
    )
  }

  override fun onCreate() {
    super.onCreate()
    loadReactNative(this)

    // 在Application启动时初始化FlutterEngine
        initFlutterEngine()
  }

    /**
     * 初始化FlutterEngine并设置初始路由
     */
    private fun initFlutterEngine() {
    Log.d(TAG, "Initializing FlutterEngine")
    try {
        // 创建FlutterEngine实例
        val flutterEngine = FlutterEngine(this)
        
        // 统一初始路由参数
        val initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzY0MTI0NDc3fS5zaWc"
        Log.d(TAG, "Set initial route: $initialRoute")
        flutterEngine.navigationChannel.setInitialRoute(initialRoute)
        
        // 执行Flutter入口点
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        
        // 将FlutterEngine缓存起来，供后续使用
        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)
        Log.d(TAG, "FlutterEngine cached with ID: $FLUTTER_ENGINE_ID")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize FlutterEngine: ${e.message}", e)
    }
}

    /**
     * 获取缓存的FlutterEngine
     * 可以在其他组件中通过MainApplication.getInstance().getCachedFlutterEngine()获取
     */
    fun getCachedFlutterEngine(): FlutterEngine? {
        return FlutterEngineCache.getInstance().get(FLUTTER_ENGINE_ID)
    }

    // 单例模式获取Application实例（可选）
    companion object {
        private var instance: MainApplication? = null

        fun getInstance(): MainApplication? {
            return instance
        }
    }
}
