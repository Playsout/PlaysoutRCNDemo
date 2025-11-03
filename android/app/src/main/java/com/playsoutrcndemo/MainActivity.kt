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
  // Flutter引擎ID，用于缓存和重用Flutter引擎
    private val FLUTTER_ENGINE_ID = "my_flutter_engine"
    // FlutterFragment实例
    private var flutterFragment: FlutterFragment? = null
    // 是否显示Flutter视图
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

// 添加Fragment生命周期监听器
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "Activity created, FlutterEngine should be initialized by Application")
    
    // 添加Fragment生命周期监听器
    supportFragmentManager.addOnBackStackChangedListener {
        // 检查回退栈，如果为空，说明FlutterFragment已被移除
        if (supportFragmentManager.backStackEntryCount == 0) {
            Log.d(TAG, "FlutterFragment removed from back stack")
            showingFlutter = false
            flutterFragment = null
        }
    }
}

// 修改showFlutter方法，确保每次都能正确显示FlutterFragment
fun showFlutter() {
    Log.d(TAG, "showFlutter called, showingFlutter: $showingFlutter")
    
    try {
        // 无论showingFlutter状态如何，都创建新的Fragment
        // 先检查是否已存在Fragment，如果存在则移除
        if (flutterFragment != null) {
            val existingTransaction = supportFragmentManager.beginTransaction()
            existingTransaction.remove(flutterFragment!!)
            existingTransaction.commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions() // 立即执行事务
        }
        
        // 创建新的FlutterFragment实例
        flutterFragment = FlutterFragment.withCachedEngine(FLUTTER_ENGINE_ID).build()
        Log.d(TAG, "FlutterFragment created with cached engine")
        
        // 添加新Fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(android.R.id.content, flutterFragment!!)
        transaction.addToBackStack(null) // 添加到回退栈
        transaction.commitAllowingStateLoss()
        
        showingFlutter = true
        Log.d(TAG, "FlutterFragment added to activity")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to show Flutter: ${e.message}", e)
        showingFlutter = false
    }
}

// 也可以添加一个显式的返回按钮处理方法
fun handleBackPress(): Boolean {
    if (showingFlutter) {
        // 如果显示着Flutter页面，先隐藏它
        hideFlutter()
        return true
    }
    return false
}

    /**
     * 隐藏Flutter视图，返回React Native视图
     * 这个方法可以通过React Native的原生模块调用
     */
    fun hideFlutter() {
        if (showingFlutter && flutterFragment != null) {
            // 移除FlutterFragment
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
        
        // 清理缓存的Flutter引擎
        FlutterEngineCache.getInstance().remove(FLUTTER_ENGINE_ID)
    }
}
