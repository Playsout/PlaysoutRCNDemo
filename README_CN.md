# Playsout SDK 集成与使用指南

## 项目概述

PlaysoutRCNDemo 是一个 React Native 项目，展示了如何在 Android 和 iOS 平台上集成使用 Flutter 开发的 Playsout SDK。该项目通过原生桥接方式实现了 React Native 与 Flutter 的无缝通信和交互。

## 技术栈

- React Native: 跨平台移动应用开发框架
- Flutter: 用于构建 Playsout SDK 的 UI 框架
- Kotlin: Android 原生开发语言
- Swift/Objective-C: iOS 原生开发语言

## 环境要求

- **React Native**: 0.82.1
- **React**: 19.1.1
- **Node.js**: >= 20
- **Flutter**: 推荐使用与Playsout SDK兼容的稳定版本，建议使用Flutter 3.10+
- **Android**:
  - 最低SDK版本: 24 (Android 7.0 Nougat)
  - 目标SDK版本: 36
  - 构建工具版本: 36.0.0
  - Kotlin版本: 2.1.20
- **iOS**:
  - 支持与React Native 0.82.1兼容的iOS版本
  - 需要安装Flutter.framework

## SDK 接入环境配置

在集成 Playsout SDK 之前，需要先配置 Flutter 到原生环境的集成环境。以下是 Flutter 官方提供的配置文档：

- **Android 平台配置文档**: <mcurl name="Add Flutter to existing app - Android" url="https://docs.flutter.dev/add-to-app/android/project-setup"></mcurl>
- **iOS 平台配置文档**: <mcurl name="Add Flutter to existing app - iOS" url="https://docs.flutter.dev/add-to-app/ios/project-setup"></mcurl>

请按照官方文档完成基础环境配置后，再进行以下 Playsout SDK 特定的集成步骤。

## Android 平台 SDK 接入方式

### 1. 初始化 Flutter Engine

在 MainApplication.kt 中，应用启动时初始化 Flutter Engine：

```kotlin
private fun initFlutterEngine() {
    // 创建FlutterEngine实例
    val flutterEngine = FlutterEngine(this)
    
    // 设置初始路由参数，包含通道和SDK密钥
    // 注意：sdkkey需要向Playsout SDK提供方申请获取
    val initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzY0MTI0NDc3fS5zaWc"
    flutterEngine.navigationChannel.setInitialRoute(initialRoute)
    
    // 执行Flutter入口点
    flutterEngine.dartExecutor.executeDartEntrypoint(
        DartExecutor.DartEntrypoint.createDefault()
    )
    
    // 缓存FlutterEngine供后续使用
    FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)
}
```

### 2. 注册 React Native 原生模块

在 MainApplication.kt 中注册 PlaysoutPackage：

```kotlin
override val reactHost: ReactHost by lazy {
    getDefaultReactHost(
      context = applicationContext,
      packageList =
        PackageList(this).packages.apply {
          add(PlaysoutPackage()) // 添加PlaysoutPackage
        },
    )
  }
```

### 3. 实现原生模块接口

在 PlaysoutPackage.kt 中注册 PlaysoutUIModule：

```kotlin
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
```

### 4. 实现 Flutter 界面控制模块

在 PlaysoutUIModule.kt 中提供 JavaScript 调用方法：

```kotlin
class PlaysoutUIModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    // 模块名称，在JavaScript端通过NativeModules.PlaysoutUIModule访问
    override fun getName() = "PlaysoutUIModule"
    
    // 显示Flutter界面的方法
    @ReactMethod
    fun showFlutter() {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity is MainActivity) {
            currentActivity.runOnUiThread {
                currentActivity.showFlutter()
            }
        }
    }
    
    // 隐藏Flutter界面的方法
    @ReactMethod
    fun hideFlutter() {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity is MainActivity) {
            currentActivity.runOnUiThread {
                currentActivity.hideFlutter()
            }
        }
    }
}
```

### 5. 在 MainActivity 中管理 Flutter Fragment

在 MainActivity.kt 中实现显示和隐藏 Flutter Fragment 的功能：

```kotlin
// 显示Flutter界面
fun showFlutter() {
    // 创建FlutterFragment并使用缓存的引擎
    flutterFragment = FlutterFragment.withCachedEngine(FLUTTER_ENGINE_ID).build()
    
    // 添加Fragment到当前Activity
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(android.R.id.content, flutterFragment!!)
    transaction.addToBackStack(null)
    transaction.commitAllowingStateLoss()
    
    showingFlutter = true
}

// 隐藏Flutter界面
fun hideFlutter() {
    if (showingFlutter && flutterFragment != null) {
        supportFragmentManager
            .beginTransaction()
            .remove(flutterFragment!!)
            .commit()
        
        flutterFragment = null
        showingFlutter = false
    }
}
```

## iOS 平台 SDK 接入方式

### 1. 配置 Flutter 引擎和控制器

在 PlaysoutController.swift 中实现 FlutterViewController 的子类：

```swift
@objc class PlaysoutController: FlutterViewController {
    @objc init(engine: FlutterEngine?, channelName: String = "", method: String = "", arguments:[String: Any]?) {
        // 创建FlutterEngine
        var flutterEngine = FlutterEngine(name: "com.playsout.minigames")
        
        // 设置初始路由，包含通道和SDK密钥
        // 注意：sdkkey需要向Playsout SDK提供方申请获取
        let initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzYyOTQwNzU4fS5zaWc"
        flutterEngine.run(withEntrypoint: "main", initialRoute: initialRoute)
            
        // 注册所有Flutter插件
        GeneratedPluginRegistrant.register(with: flutterEngine)
        
        super.init(engine: flutterEngine, nibName: nil, bundle: nil)
        
        // 设置MethodChannel用于通信
        let messageChannel = FlutterMethodChannel(name: channelName, binaryMessenger: self.binaryMessenger)
        messageChannel.invokeMethod(method, arguments: arguments)
        
        // 设置方法处理器
        messageChannel.setMethodCallHandler{[weak self] (call, result) in
            if call.method == "show" {
                self?.showFlutterUI()
                result(true)
            } else {
                result(FlutterMethodNotImplemented)
            }
        }
    }
    
    // 显示Flutter UI的方法
    private func showFlutterUI() {
        // 确保视图可见
        self.view.isHidden = false
        self.view.alpha = 1.0
        
        // 添加动画效果
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
    }
}
```

### 2. 创建 React Native 桥接模块

在 PlaysoutBridge.m 中实现 React Native 调用 iOS 原生代码的接口：

```objective-c
@implementation PlaysoutBridge

RCT_EXPORT_MODULE();

// 导出方法：打开PlaysoutController视图
RCT_EXPORT_METHOD(openPlaysoutController:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  @try {
    // 确保在主线程上执行UI操作
    dispatch_async(dispatch_get_main_queue(), ^{      
      // 获取当前的根视图控制器
      UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
      
      // 创建PlaysoutController实例
      PlaysoutController *playsoutController = [[PlaysoutController alloc] initWithEngine:nil channelName:@"com.playsout.minigames" method:@"init" arguments:nil];
      playsoutController.modalPresentationStyle = UIModalPresentationFullScreen;
      
      // 配置MethodChannel
      FlutterMethodChannel *channel = [FlutterMethodChannel methodChannelWithName:@"com.playsout.minigames" binaryMessenger:playsoutController.binaryMessenger];
      
      // 定义显示Flutter UI的回调
      void (^showFlutterUICallback)(void) = ^{        
        [channel invokeMethod:@"show" arguments:nil];
      };
      
      // 显示视图控制器
      if ([rootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navController = (UINavigationController *)rootViewController;
        [navController pushViewController:playsoutController animated:YES];
        
        // 延迟执行show方法确保视图已加载
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), showFlutterUICallback);
      } else {
        // 包装成导航控制器
        UINavigationController *navController = [[UINavigationController alloc] initWithRootViewController:playsoutController];
        [rootViewController presentViewController:navController animated:YES completion:showFlutterUICallback];
      }
      
      resolve(@"Successfully opened PlaysoutController");
    });
  } @catch (NSException *exception) {
    reject(@"OPEN_PLAYSOUT_CONTROLLER_FAILED", @"Failed to open PlaysoutController", nil);
  }
}
@end
```

### 3. 配置项目依赖

在 iOS 项目配置中添加 Flutter 相关依赖，配置 framework 搜索路径：

```
FRAMEWORK_SEARCH_PATHS = (
    "$(inherited)",
    "$(PROJECT_DIR)/../PlaysoutiOSSDK/$(CONFIGURATION)",
);
```

## 在 React Native 中使用 Playsout SDK

### Android 平台使用方法

```javascript
// 导入 NativeModules
import { NativeModules, Platform } from 'react-native';

// 获取 PlaysoutUIModule
const { PlaysoutUIModule } = NativeModules;

// 显示 Flutter 界面
const showFlutterUI = () => {
  if (Platform.OS === 'android') {
    PlaysoutUIModule.showFlutter();
  }
};

// 隐藏 Flutter 界面
const hideFlutterUI = () => {
  if (Platform.OS === 'android') {
    PlaysoutUIModule.hideFlutter();
  }
};
```

### iOS 平台使用方法

```javascript
// 导入 NativeModules
import { NativeModules, Platform } from 'react-native';

// 获取 PlaysoutBridge
const { PlaysoutBridge } = NativeModules;

// 打开并显示 Flutter 界面
const handleIOSSDKPress = async () => {
  if (Platform.OS === 'ios') {
    try {
      const result = await PlaysoutBridge.openPlaysoutController();
      console.log('打开PlaysoutController结果:', result);
    } catch (error) {
      console.error('打开PlaysoutController失败:', error);
    }
  }
};
```

## 集成流程总结

### Android 平台

1. 在 Application 启动时初始化 Flutter Engine 并缓存
2. 创建 React Native 原生模块，提供 JavaScript 调用接口
3. 在 Activity 中使用 FlutterFragment 管理 Flutter 视图
4. 通过 NativeModules 在 JavaScript 中调用原生方法显示/隐藏 Flutter 界面

### iOS 平台

1. 创建 FlutterViewController 子类管理 Flutter 界面
2. 设置 Flutter Engine 和初始路由参数
3. 实现 React Native 桥接模块，暴露方法给 JavaScript 调用
4. 通过 NativeModules 在 JavaScript 中打开 Flutter 界面

## 通信机制

两个平台都使用了 MethodChannel 进行 Flutter 和原生代码之间的双向通信，实现了数据交换和方法调用。

## 注意事项

- Android 平台使用了 FlutterFragment 嵌入 Flutter 视图
- iOS 平台使用了 FlutterViewController 管理 Flutter 视图
- **SDK 密钥获取：** sdkkey 需要向 Playsout SDK 提供方申请获取，示例中的密钥仅供演示使用
- iOS 平台需要确保正确配置 framework 搜索路径

## 故障排除

- 确保 Flutter Engine 正确初始化
- 检查 MethodChannel 名称是否匹配
- 验证 SDK 密钥是否有效
- 确保平台特定代码在正确的平台上执行
