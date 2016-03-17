package be.nabu.glue.appium;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class AppiumTester {

	public static void main(String...args) throws InterruptedException {
		// step 1: download https://nodejs.org/en/download/
		// step 2: run "npm install appium" based on the npm in the above package
		// step 3: download android sdk tools: http://developer.android.com/sdk/index.html#Other
		// step 4: run: /path/to/android-sdk-linux/tools $ ./android
		// 		in the popup it should auto-select the latest android version
	
		Map<String, String> environment = new HashMap<String, String>();
		environment.put("ANDROID_HOME", "/home/alex/apps/android-sdk-linux");
		AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
			.usingDriverExecutable(new File("/home/alex/apps/node-v4.4.0-linux-x64/bin/node"))
//			.withAppiumJS(new File("/home/alex/apps/appium/node_modules/appium/build/lib/appium.js"))
			.withAppiumJS(new File("/home/alex/apps/appium/node_modules/appium/build/lib/main.js"))
//			.withArgument(new ServerArgument() {
//				@Override
//				public String getArgument() {
//					return "ANDROID_HOME";
//				}
//			}, "/home/alex/apps/android-sdk-linux")
			.withEnvironment(environment)
			.withIPAddress("127.0.0.1").usingPort(4723));
		
		service.start();
		
		File file = new File("/home/alex/files/appium/flex-calculator.apk");
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
		capabilities.setCapability(MobileCapabilityType.APP, file.getAbsolutePath());
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 5);
		AndroidDriver<MobileElement> driver = new AndroidDriver<MobileElement>(service.getUrl(), capabilities);
		
		MobileElement element = driver.findElement(By.id("txtCalories"));
		element.sendKeys("100");
		
		element = driver.findElement(By.id("txtFat"));
		element.sendKeys("10");

		element = driver.findElement(By.id("txtFibers"));
		element.sendKeys("5");
		
//		1.83
		
		element = driver.findElement(By.id("lblPoints"));
		String text = element.getText();
		System.out.println("THE RESULT == " + text);
		
		Thread.sleep(1000);
		
		driver.quit();
	}
	
}
