//
//  PlaysoutBridge.m
//  PlaysoutRCNDemo
//
//  Created by wl on 2025/11/3.
//


#import <Foundation/Foundation.h>

#import "PlaysoutBridge.h"
// Import Flutter related headers
#import <Flutter/Flutter.h>

#import "PlaysoutRCNDemo-Swift.h"
@implementation PlaysoutBridge

RCT_EXPORT_MODULE();

// Export new method: Open PlaysoutController view and execute show flutterUI method
RCT_EXPORT_METHOD(openPlaysoutController:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  @try {
    NSLog(@"Opening PlaysoutController");
    
    // Ensure UI operations are executed on the main thread
    dispatch_async(dispatch_get_main_queue(), ^{
      // Get the current root view controller
      UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
      
      if (!rootViewController) {
        reject(@"ROOT_VIEW_CONTROLLER_ERROR", @"Failed to get root view controller", nil);
        return;
      }
      
      // Get the FlutterEngine from AppDelegate (if it exists)
      // Now you can directly pass nil, PlaysoutController will handle it internally
      PlaysoutController *playsoutController = [[PlaysoutController alloc] initWithEngine:nil channelName:@"com.playsout.minigames" method:@"init" arguments:nil];
      playsoutController.modalPresentationStyle = UIModalPresentationFullScreen;
      
      // Configure method call to execute show flutterUI
      FlutterMethodChannel *channel = [FlutterMethodChannel methodChannelWithName:@"com.playsout.minigames" binaryMessenger:playsoutController.binaryMessenger];
      
      // Define completion callback after view appears
      void (^showFlutterUICallback)(void) = ^{
        // Execute show flutterUI method after view appears
        [channel invokeMethod:@"show" arguments:nil];
        NSLog(@"Executed show flutterUI method");
      };
      
      // Show the view controller
      if ([rootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navController = (UINavigationController *)rootViewController;
        [navController setNavigationBarHidden:YES animated:NO];
        [navController pushViewController:playsoutController animated:YES];
        
        // Use delay to ensure show method is executed after view is fully loaded
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), showFlutterUICallback);
      } else {
        // Wrap in a navigation controller
        UINavigationController *navController = [[UINavigationController alloc] initWithRootViewController:playsoutController];
        navController.modalPresentationStyle = UIModalPresentationFullScreen;
        [rootViewController presentViewController:navController animated:YES completion:showFlutterUICallback];
      }
      
      resolve(@"Successfully opened PlaysoutController");
    });
  } @catch (NSException *exception) {
    NSError *error = [NSError errorWithDomain:@"IOSNativeError" code:3 userInfo:@{NSLocalizedDescriptionKey: exception.reason}];
    reject(@"OPEN_PLAYSOUT_CONTROLLER_FAILED", @"Failed to open PlaysoutController", error);
    NSLog(@"Exception: %@", exception);
  }
}
@end

