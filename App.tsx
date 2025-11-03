/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import { Alert, Platform, StatusBar, StyleSheet, Text, TouchableOpacity, useColorScheme, View, NativeModules } from 'react-native';
import {
  SafeAreaProvider,
  useSafeAreaInsets,
} from 'react-native-safe-area-context';

const { PlaysoutBridge, PlaysoutUIModule} = NativeModules;
function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <AppContent />
    </SafeAreaProvider>
  );
}

function AppContent() {
  const safeAreaInsets = useSafeAreaInsets();

  const handleTestButtonPress = () => {
    Alert.alert('测试提示', '这是一个测试提示信息');
  };

  const handleAndroidSDKPress = () => {
    if (Platform.OS === 'android') {
      // 这里应该是打开安卓平台SDK页面的逻辑
      // Alert.alert('安卓SDK', '正在打开安卓平台SDK页面');
      try {
        // 检查PlaysoutUIModule是否存在
        if (PlaysoutUIModule) {
          // 调用原生方法打开Flutter页面
          PlaysoutUIModule.showFlutter();
        } else {
          Alert.alert('错误', 'PlaysoutUIModule未找到，请检查原生模块是否正确注册');
          console.error('PlaysoutUIModule未找到');
        }
      } catch (error) {
        Alert.alert('错误', '打开Flutter页面失败: ' + (error as Error).message);
        console.error('打开Flutter页面失败:', error);
      }
    } else {
      Alert.alert('提示', '此功能仅在安卓平台可用');
    }
  };

  const handleIOSSDKPress = async () => {
    if (Platform.OS === 'ios') {
      // 这里应该是打开iOS平台SDK页面的逻辑
      // Alert.alert('iOS SDK', '正在打开iOS平台SDK页面');
      try {
        // 调用原生模块的openPlaysoutController方法
        const result = await PlaysoutBridge.openPlaysoutController();
        console.log('打开PlaysoutController结果:', result);
      } catch (error) {
        Alert.alert('错误', '打开PlaysoutController失败：' + String(error));
        console.error('打开PlaysoutController失败:', error);
      }
    } else {
      Alert.alert('提示', '此功能仅在iOS平台可用');
    }
  };

  const handlePlatformSpecificPress = () => {
    if (Platform.OS === 'android') {
      // Alert.alert('平台信息', '当前运行在安卓平台');
      handleAndroidSDKPress();
    } else if (Platform.OS === 'ios') {
      // Alert.alert('平台信息', '当前运行在iOS平台');
      handleIOSSDKPress();
    } else {
      Alert.alert('平台信息', '当前运行在其他平台: ' + Platform.OS);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.buttonsContainer}>
        <TouchableOpacity style={styles.button} onPress={handleTestButtonPress}>
          <Text style={styles.buttonText}>测试按钮</Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.button} onPress={handleAndroidSDKPress}>
          <Text style={styles.buttonText}>安卓SDK</Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.button} onPress={handleIOSSDKPress}>
          <Text style={styles.buttonText}>iOS SDK</Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.button} onPress={handlePlatformSpecificPress}>
          <Text style={styles.buttonText}>平台特定</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  buttonsContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  button: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 30,
    paddingVertical: 15,
    borderRadius: 8,
    marginVertical: 10,
    minWidth: 200,
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default App;
