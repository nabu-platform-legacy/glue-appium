# Appium

## Notes

Initial support for appium-based testing.

A few notes:

- The apk/ipa file can live on a http endpoint, this is likely better for reusability of the tests. It is unclear (but unlikely) that they can be streamed from the glue repository
- Appium allows both native application testing and testing of the browser on the mobile device. To differentiate this package offers an `appium.webdriver()` method which provides you with a selenium instance to test the browser on the mobile device whilst `appium.appdriver()` will give you a selenium driver to run a native app
- Currently the system has only been tested locally (so not via the selenium grid yet) and on an actual android device (no iphone/simulators yet). This has to be expanded upon and may introduce new configuration settings.

## Setup

The machine where the simulator or the actual device will run needs a bit of external software:

- Download [nodejs](https://nodejs.org/en/download/) and extract the zip to a folder
- Use the `npm` binary in the unzipped file above to run: `npm install appium`. Avoid a global install to keep things clean.

### Android

- Download [android sdk tools](http://developer.android.com/sdk/index.html#Other)
- Unzip them and run `/path/to/android-sdk-linux/tools/android`
	- The popup should autoselect the correct things to install, if not, select the latest (or applicable) android version(s)

Current expected configuration in your `.glue` file:

```
# The android tools we downloaded and unzipped
local.androidHome = /path/to/android-sdk-linux

# The nodejs runnable we downloaded
local.nodeJs = /path/to/node-v4.4.0-linux-x64/bin/node

# The appium we installed
local.appiumMain = /path/to/node_modules/appium/build/lib/main.js
```

It is not entirely clear yet how these configuration parameters will be affected with remote selenium grid runs.
