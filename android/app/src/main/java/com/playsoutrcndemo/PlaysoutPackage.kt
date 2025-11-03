package com.playsoutrcndemo

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class PlaysoutPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        // 注册我们的模块
        return listOf(
            PlaysoutUIModule(reactContext) // 添加PlaysoutUI模块
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        // 这个模块不提供UI组件，所以返回空列表
        return emptyList()
    }
}