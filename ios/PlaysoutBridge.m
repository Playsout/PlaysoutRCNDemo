//
//  PlaysoutBridge.m
//  PlaysoutRCNDemo
//
//  Created by wl on 2025/11/3.
//


#import <Foundation/Foundation.h>

#import "PlaysoutBridge.h"
// 导入Flutter相关头文件
#import <Flutter/Flutter.h>

#import "PlaysoutRCNDemo-Swift.h"
@implementation PlaysoutBridge

RCT_EXPORT_MODULE();

// 导出新方法：打开PlaysoutController视图并执行show flutterUI方法
RCT_EXPORT_METHOD(openPlaysoutController:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  @try {
    NSLog(@"Opening PlaysoutController");
    
    // 确保在主线程上执行UI操作
    dispatch_async(dispatch_get_main_queue(), ^{
      // 获取当前的根视图控制器
      UIViewController *rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
      
      if (!rootViewController) {
        reject(@"ROOT_VIEW_CONTROLLER_ERROR", @"Failed to get root view controller", nil);
        return;
      }
      
      // 获取AppDelegate中的FlutterEngine（如果已存在）
      // 现在可以直接传入nil，PlaysoutController内部会处理
      PlaysoutController *playsoutController = [[PlaysoutController alloc] initWithEngine:nil channelName:@"com.playsout.minigames" method:@"init" arguments:nil];
      playsoutController.modalPresentationStyle = UIModalPresentationFullScreen;
      
      // 配置执行show flutterUI的方法调用
      FlutterMethodChannel *channel = [FlutterMethodChannel methodChannelWithName:@"com.playsout.minigames" binaryMessenger:playsoutController.binaryMessenger];
      
      // 定义视图出现后的完成回调
      void (^showFlutterUICallback)(void) = ^{
        // 视图出现后执行show flutterUI方法
        [channel invokeMethod:@"show" arguments:nil];
        NSLog(@"执行了show flutterUI方法");
      };
      
      // 显示视图控制器
      if ([rootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController *navController = (UINavigationController *)rootViewController;
        [navController setNavigationBarHidden:YES animated:NO];
        [navController pushViewController:playsoutController animated:YES];
        
        // 使用延迟确保视图完全加载后再执行show方法
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), showFlutterUICallback);
      } else {
        // 包装成导航控制器
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

