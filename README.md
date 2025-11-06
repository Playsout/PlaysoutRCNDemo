# Playsout SDK Integration and Usage Guide (React Native)

## Project Overview

PlaysoutRCNDemo is a React Native project demonstrating how to integrate and use the Flutter-based Playsout SDK on both Android and iOS platforms. The project achieves seamless communication and interaction between React Native and Flutter through native bridging.

## Technology Stack

- React Native: Cross-platform mobile application development framework
- Flutter: UI framework used to build the Playsout SDK
- Kotlin: Android native development language
- Swift/Objective-C: iOS native development languages

## Environment Requirements

- **React Native**: 0.82.1
- **React**: 19.1.1
- **Node.js**: >= 20
- **Flutter**: Recommended to use a stable version compatible with Playsout SDK, Flutter 3.10+
- **Android**:
  - Minimum SDK version: 24 (Android 7.0 Nougat)
  - Target SDK version: 36
  - Build Tools version: 36.0.0
  - Kotlin version: 2.1.20
- **iOS**:
  - iOS versions compatible with React Native 0.82.1
  - Flutter.framework installation required

## SDK Integration Environment Configuration

Before integrating the Playsout SDK, you need to configure the Flutter to native environment integration. Here are the official Flutter configuration documents:

- **Android Platform Configuration Document**: <mcurl name="Add Flutter to existing app - Android" url="https://docs.flutter.dev/add-to-app/android/project-setup"></mcurl>
- **iOS Platform Configuration Document**: <mcurl name="Add Flutter to existing app - iOS" url="https://docs.flutter.dev/add-to-app/ios/project-setup"></mcurl>

Please complete the basic environment configuration according to the official documentation before proceeding with the following Playsout SDK-specific integration steps.

## Android Platform SDK Integration

### 1. Initialize Flutter Engine

In MainApplication.kt, initialize the Flutter Engine when the application starts:

```kotlin
private fun initFlutterEngine() {
    // Create FlutterEngine instance
    val flutterEngine = FlutterEngine(this)
    
    // Set initial route parameters, including channel and SDK key
    // Note: sdkkey needs to be obtained from Playsout SDK provider
    val initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzY0MTI0NDc3fS5zaWc"
    flutterEngine.navigationChannel.setInitialRoute(initialRoute)
    
    // Execute Flutter entry point
    flutterEngine.dartExecutor.executeDartEntrypoint(
        DartExecutor.DartEntrypoint.createDefault()
    )
    
    // Cache FlutterEngine for later use
    FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine)
}
```

### 2. Register React Native Native Module

Register PlaysoutPackage in MainApplication.kt:

```kotlin
override val reactHost: ReactHost by lazy {
    getDefaultReactHost(
      context = applicationContext,
      packageList =
        PackageList(this).packages.apply {
          add(PlaysoutPackage()) // Add PlaysoutPackage
        },
    )
  }
```

### 3. Implement Native Module Interface

Register PlaysoutUIModule in PlaysoutPackage.kt:

```kotlin
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
```

### 4. Implement Flutter UI Control Module

Provide JavaScript calling methods in PlaysoutUIModule.kt:

```kotlin
class PlaysoutUIModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    // Module name, accessed via NativeModules.PlaysoutUIModule on JavaScript side
    override fun getName() = "PlaysoutUIModule"
    
    // Method to show Flutter UI
    @ReactMethod
    fun showFlutter() {
        val currentActivity = reactApplicationContext.currentActivity
        if (currentActivity is MainActivity) {
            currentActivity.runOnUiThread {
                currentActivity.showFlutter()
            }
        }
    }
    
    // Method to hide Flutter UI
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

### 5. Manage Flutter Fragment in MainActivity

Implement functionality to show and hide Flutter Fragment in MainActivity.kt:

```kotlin
// Show Flutter UI
fun showFlutter() {
    // Create FlutterFragment using cached engine
    flutterFragment = FlutterFragment.withCachedEngine(FLUTTER_ENGINE_ID).build()
    
    // Add Fragment to current Activity
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(android.R.id.content, flutterFragment!!)
    transaction.addToBackStack(null)
    transaction.commitAllowingStateLoss()
    
    showingFlutter = true
}

// Hide Flutter UI
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

## iOS Platform SDK Integration

### 1. Configure Flutter Engine and Controller

Implement a subclass of FlutterViewController in PlaysoutController.swift:

```swift
@objc class PlaysoutController: FlutterViewController {
    @objc init(engine: FlutterEngine?, channelName: String = "", method: String = "", arguments:[String: Any]?) {
        // Create FlutterEngine
        var flutterEngine = FlutterEngine(name: "com.playsout.minigames")
        
        // Set initial route, including channel and SDK key
        // Note: sdkkey needs to be obtained from Playsout SDK provider
        let initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzYyOTQwNzU4fS5zaWc"
        flutterEngine.run(withEntrypoint: "main", initialRoute: initialRoute)
            
        // Register all Flutter plugins
        GeneratedPluginRegistrant.register(with: flutterEngine)
        
        super.init(engine: flutterEngine, nibName: nil, bundle: nil)
        
        // Set up MethodChannel for communication
        let messageChannel = FlutterMethodChannel(name: channelName, binaryMessenger: self.binaryMessenger)
        messageChannel.invokeMethod(method, arguments: arguments)
        
        // Set method handler
        messageChannel.setMethodCallHandler{[weak self] (call, result) in
            if call.method == "show" {
                self?.showFlutterUI()
                result(true)
            } else {
                result(FlutterMethodNotImplemented)
            }
        }
    }
    
    // Method to show Flutter UI
    private func showFlutterUI() {
        // Ensure view is visible
        self.view.isHidden = false
        self.view.alpha = 1.0
        
        // Add animation effect
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
    }
}
```

### 2. Create React Native Bridge Module

Implement the interface for React Native to call iOS native code in PlaysoutBridge.m:

```objective-c
@implementation PlaysoutBridge

RCT_EXPORT_MODULE();

// Export method: open PlaysoutController view
RCT_EXPORT_METHOD(openPlaysoutController:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  @try {
    // Ensure UI operations are executed on the main thread
    dispatch_async(dispatch_get_main_queue(), ^{      
      // Get the current root view controller
      UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
      
      // Create PlaysoutController instance
      PlaysoutController *playsoutController = [[PlaysoutController alloc] initWithEngine:nil channelName:@"com.playsout.minigames" method:@"init" arguments:nil];
      playsoutController.modalPresentationStyle = UIModalPresentationFullScreen;
      
      // Configure MethodChannel
      FlutterMethodChannel *channel = [FlutterMethodChannel methodChannelWithName:@"com.playsout.minigames" binaryMessenger:playsoutController.binaryMessenger];
      
      // Define callback to show Flutter UI
      void (^showFlutterUICallback)(void) = ^{        
        [channel invokeMethod:@"show" arguments:nil];
      };
      
      // Show view controller
      if ([rootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navController = (UINavigationController *)rootViewController;
        [navController pushViewController:playsoutController animated:YES];
        
        // Delay execution of show method to ensure view is loaded
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), showFlutterUICallback);
      } else {
        // Wrap in navigation controller
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

### 3. Configure Project Dependencies

Add Flutter-related dependencies to iOS project configuration, configure framework search paths:

```
FRAMEWORK_SEARCH_PATHS = (
    "$(inherited)",
    "$(PROJECT_DIR)/../PlaysoutiOSSDK/$(CONFIGURATION)",
);
```

## Using Playsout SDK in React Native

### Android Platform Usage

```javascript
// Import NativeModules
import { NativeModules, Platform } from 'react-native';

// Get PlaysoutUIModule
const { PlaysoutUIModule } = NativeModules;

// Show Flutter UI
const showFlutterUI = () => {
  if (Platform.OS === 'android') {
    PlaysoutUIModule.showFlutter();
  }
};

// Hide Flutter UI
const hideFlutterUI = () => {
  if (Platform.OS === 'android') {
    PlaysoutUIModule.hideFlutter();
  }
};
```

### iOS Platform Usage

```javascript
// Import NativeModules
import { NativeModules, Platform } from 'react-native';

// Get PlaysoutBridge
const { PlaysoutBridge } = NativeModules;

// Open and show Flutter UI
const handleIOSSDKPress = async () => {
  if (Platform.OS === 'ios') {
    try {
      const result = await PlaysoutBridge.openPlaysoutController();
      console.log('Open PlaysoutController result:', result);
    } catch (error) {
      console.error('Failed to open PlaysoutController:', error);
    }
  }
};
```

## Integration Process Summary

### Android Platform

1. Initialize Flutter Engine and cache it when Application starts
2. Create React Native native module to provide JavaScript calling interface
3. Use FlutterFragment in Activity to manage Flutter views
4. Call native methods in JavaScript through NativeModules to show/hide Flutter UI

### iOS Platform

1. Create FlutterViewController subclass to manage Flutter UI
2. Set up Flutter Engine and initial route parameters
3. Implement React Native bridge module to expose methods for JavaScript to call
4. Open Flutter UI in JavaScript through NativeModules

## Communication Mechanism

Both platforms use MethodChannel for bidirectional communication between Flutter and native code, enabling data exchange and method calls.

## Notes

- Android platform uses FlutterFragment to embed Flutter views
- iOS platform uses FlutterViewController to manage Flutter views
- **SDK Key Acquisition:** sdkkey needs to be obtained from Playsout SDK provider, the key in the example is for demonstration only
- iOS platform needs to ensure correct framework search path configuration

## Troubleshooting

- Ensure Flutter Engine is initialized correctly
- Check if MethodChannel names match
- Verify if SDK key is valid
- Ensure platform-specific code is executed on the correct platform

