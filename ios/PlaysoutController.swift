


import UIKit
import Flutter
import FlutterPluginRegistrant

@objc class PlaysoutController: FlutterViewController {
    
    override func viewDidLoad() {
            super.viewDidLoad()
            print("📱 PlaysoutController viewDidLoad()")
            // Do any additional setup after loading the view.
            self.navigationItem.title = "Playsou View"
        }
    override func viewDidDisappear(_ animated: Bool) {
            super.viewDidDisappear(animated)
            print("📱 PlaysoutController viewDidDisappear()")
            
        }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("📱 PlaysoutController viewWillAppear")
    }
    deinit {
            print("exit playsout")
        
        }

        @objc init(engine: FlutterEngine?, channelName: String = "", method: String = "", arguments:[String: Any]?) {

 
            //创建FlutterEngine
            var flutterEngine = FlutterEngine(name: "com.playsout.minigames")
            // 启动FlutterEngine
            let initialRoute = "/home?channel=playsout&sdkkey=eyJ2ZXIiOiJ2MSIsImNoYW5uZWwiOiJwbGF5c291dCIsInBhY2thZ2VuYW1lIjoiIiwiZXhwIjoxNzYyOTQwNzU4fS5zaWc"
            flutterEngine.run(withEntrypoint: "main", initialRoute: initialRoute)
                
            // 注册所有Flutter插件，包括shared_preferences
            GeneratedPluginRegistrant.register(with: flutterEngine)

            
            super.init(engine: flutterEngine, nibName: nil, bundle: nil)
            print("✅ PlaysoutController init with engine: \(engine != nil ? "provided" : "temporary")")
            
            let messageChannel = FlutterMethodChannel(name: channelName, binaryMessenger: self.binaryMessenger)
            messageChannel.invokeMethod(method, arguments: arguments)
            messageChannel.setMethodCallHandler{[weak self] (call, result) in
                guard let strongSelf = self else { return }
    
                print("Playsout method:\(call.method) arguments:\(String(describing: call.arguments))")
    
                // 处理show方法调用，显示Flutter UI
                if call.method == "show" {
                    strongSelf.showFlutterUI()
                    result(true) // 返回成功
                } else {
                    result(FlutterMethodNotImplemented) // 未实现的方法
                }
            }
             
        }
    // 显示Flutter UI的方法
    private func showFlutterUI() {
        print("📱 执行showFlutterUI方法，显示Flutter界面")
        
       
        
        // 创建参数字典
        let arguments: [String: String] = [
            "appAdId": "ca-app-pub-3940256099942544/1712485313",
            "gameAdId": "ca-app-pub-3940256099942544/1712485313"
        ]
        
        // 确保视图在前台并可见
        self.view.isHidden = false
        self.view.alpha = 1.0
        
        // 可以添加一些动画效果
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
        
        // 如果需要，可以发送通知给Flutter端，通知其UI已准备就绪
        // let messageChannel = FlutterMethodChannel(name: "com.playsout.minigames", binaryMessenger: self.binaryMessenger)
//        messageChannel.invokeMethod("flutterUIReady", arguments: arguments)
    }
    
    required init(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }
}
