package be.nabu.glue.appium;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import be.nabu.glue.annotations.GlueParam;
import be.nabu.glue.impl.methods.ScriptMethods;
import be.nabu.glue.selenium.SeleneseMethodProvider;
import be.nabu.glue.selenium.SeleneseMethodProvider.WrappedDriver;
import be.nabu.libs.evaluator.annotations.MethodProviderClass;

@MethodProviderClass(namespace = "appium")
public class AppiumMethods {
	
	/**
	 * Returns a driver to test the browser on a mobile device
	 */
	public static Object webdriver(@GlueParam(name = "platform") String platform, @GlueParam(name = "browser") String browser, @GlueParam(name = "language") String language, @GlueParam(name = "version") String version, @GlueParam(name = "landscape") Boolean landscape) throws MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		if (platform == null) {
			platform = "Android";
		}
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, platform);
		if (browser == null) {
			if ("android".equalsIgnoreCase(platform)) {
				browser = "Chrome";
			}
			else {
				browser = "Safari";
			}
		}
		// the browser names are "Safari", "Chrome", "Chromium" or "Browser" for android
		capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, browser);
		if (language != null) {
			capabilities.setCapability(MobileCapabilityType.LANGUAGE, language);
		}
		capabilities.setCapability(MobileCapabilityType.ORIENTATION, landscape != null && landscape ? "LANDSCAPE" : "PORTRAIT");
		return getDriver("android".equalsIgnoreCase(platform), capabilities);
	}
	
	/**
	 * Returns a driver to test an application on a mobile device
	 */
	public static Closeable appdriver(@GlueParam(name = "application") String application, @GlueParam(name = "language") String language, @GlueParam(name = "version") String version, @GlueParam(name = "landscape") Boolean landscape) throws MalformedURLException {
		// based on: http://appium.io/slate/en/master/?ruby#appium-server-capabilities
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.APP, application);
		if (application.endsWith(".apk")) {
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
		}
		else if (application.endsWith(".ipa")) {
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone Simulator");
		}
		
		if (version != null) {
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, version);
		}
		if (language != null) {
			capabilities.setCapability(MobileCapabilityType.LANGUAGE, language);
		}
		capabilities.setCapability(MobileCapabilityType.ORIENTATION, landscape != null && landscape ? "LANDSCAPE" : "PORTRAIT");
		capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
//		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
		
		return getDriver(application.endsWith(".apk"), capabilities);
	}

	private static Closeable getDriver(boolean isAndroid, DesiredCapabilities capabilities) throws MalformedURLException {
		WebDriver driver;
		String url = ScriptMethods.environment("selenium.server.url");
		// local execution
		if (url == null) {
			Map<String, String> environment = new HashMap<String, String>();
			// TODO: settings for iOS?
			environment.put("ANDROID_HOME", ScriptMethods.environment("androidHome"));
			final AppiumDriverLocalService service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
				.usingDriverExecutable(new File(ScriptMethods.environment("nodeJs")))
				.withAppiumJS(new File(ScriptMethods.environment("appiumMain")))
				.withEnvironment(environment)
				.withIPAddress("127.0.0.1").usingPort(Integer.parseInt(ScriptMethods.environment("appiumPort", "4723"))));
			
			service.start();
			if (isAndroid) {
				driver = new AndroidDriver<MobileElement>(service.getUrl(), capabilities);
			}
			else {
				driver = new IOSDriver<MobileElement>(service.getUrl(), capabilities);
			}
			return new WrappedDriver(driver, new Closeable() {
				@Override
				public void close() throws IOException {
					service.stop();
				}
			});
		}
		else {
			driver = SeleneseMethodProvider.getRemoteDriver(new URL(url), capabilities);
			return new WrappedDriver(driver);
		}
	}
}
