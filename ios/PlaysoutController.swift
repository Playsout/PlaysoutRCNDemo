


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

 
            // Create FlutterEngine
            var flutterEngine = FlutterEngine(name: "com.playsout.minigames")
            // Start FlutterEngine
            flutterEngine.run()
                
            // Register all Flutter plugins, including shared_preferences
            GeneratedPluginRegistrant.register(with: flutterEngine)

            
            super.init(engine: flutterEngine, nibName: nil, bundle: nil)
            print("✅ PlaysoutController init with engine: \(engine != nil ? "provided" : "temporary")")
            
            let messageChannel = FlutterMethodChannel(name: channelName, binaryMessenger: self.binaryMessenger)
            messageChannel.invokeMethod(method, arguments: arguments)
            messageChannel.setMethodCallHandler{[weak self] (call, result) in
                guard let strongSelf = self else { return }
    
                print("Playsout method:\(call.method) arguments:\(String(describing: call.arguments))")
    
                // Handle show method call to display Flutter UI
                if call.method == "show" {
                    strongSelf.showFlutterUI()
                    result(true) // Return success
                } else {
                    result(FlutterMethodNotImplemented) // Method not implemented
                }
            }
             
        }
    // Method to display Flutter UI
    private func showFlutterUI() {
        print("📱 Executing showFlutterUI method, displaying Flutter interface")
        
       
        
        // Create arguments dictionary
        let arguments: [String: String] = [
            "appAdId": "ca-app-pub-3940256099942544/1712485313",
            "gameAdId": "ca-app-pub-3940256099942544/1712485313"
        ]
        
        // Ensure view is in foreground and visible
        self.view.isHidden = false
        self.view.alpha = 1.0
        
        // Can add some animation effects
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
        
        // If needed, can send notification to Flutter side that UI is ready
        // let messageChannel = FlutterMethodChannel(name: "com.playsout.minigames", binaryMessenger: self.binaryMessenger)
//        messageChannel.invokeMethod("flutterUIReady", arguments: arguments)
    }
    
    required init(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }
}
