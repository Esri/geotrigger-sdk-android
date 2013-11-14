# Geotrigger SDK for Android

The Geotrigger Service provides real-time location tracking, geofencing,
and messaging tools for developers working on location based apps.

The Geotrigger SDK provides the `GeotriggerService`, a wrapper around 
Android's native location capabilities, which handles uploading device locations
to the Geotrigger API and receiving push notifications.

For more information, refer to the [Geotrigger Documentation][geotrigger-docs] on
the [ArcGIS for Developers][arcgis-dev-site] site.

> Note: This documentation assumes the reader is familiar with the Android SDK
> at a basic level and knows how to build a simple Android project.
>
> If you find yourself struggling with the concepts outlined below, you might
> benefit from looking over the [Android Developer Docs][android-docs].

## Installation

This app can be compiled and run in the standard fashion using Android
Studio/IntelliJ, Eclipse, or the command-line Android build tools.

If you have not already installed the [Android SDK][android-sdk], please
do so now! It's also a good idea to make sure you're running the latest version.

### Setting up the Google Play Services SDK

Because the Android Geotrigger SDK makes use of the location APIs included in the
Google Play Services SDK, you'll need to include it in your project. Please refer
to the official [Google Play Services Setup Guide][google-play-services-setup].
IntelliJ users, please note that you will need to include the 
`google-play-services.jar` as a dependancy of the `google-play-services_lib`
module in order to properly add the Google Play Services SDK to your project.

As of 11/14/2013, you'll also need to add the following meta-data declaration
within your application block in order to get Google Play Services working. This
appears to be a bug with v13 of the Google Play Services SDK or its documentation.

```xml    
<meta-data
    android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />
```

### Credentials

After checking out the sample code, you'll need to provide values for AGO\_CLIENT\_ID
and GCM\_SENDER\_ID in [GeotriggerActivity.java][sample-app-geotrigger-activity]

```java
// Create a new application at https://developers.arcgis.com/en/applications
private static final String AGO_CLIENT_ID = "...";

// The project number from https://code.google.com/apis/console
private static final String GCM_SENDER_ID = "...";
```

### Javadoc

The Geotrigger SDK Javadoc is bundled with the sample application as a jar file
in the `libs` directory. Please refer to your IDE's documentation for instructions
on adding the documentation jar.

## Existing Projects

If you have an existing project and would like to use the Geotrigger SDK, you can simply
copy the `.jar` files from the sample application's `libs` directory into your project.

### AndroidManifest.xml

In order to make use of Geotrigger functionality in your app, you'll need to modify your
application's `AndroidManifest.xml` file to include the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
<permission
    android:name="{YOUR_PACKAGE_NAME}.permission.C2D_MESSAGE"
    android:protectionLevel="signature" />
<!-- These permissions are required to enable the C2DM features of the SDK. -->
<uses-permission android:name="{YOUR_PACKAGE_NAME}.permission.C2D_MESSAGE" />
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
```

Optionally, you may also want to include the `ACCESS_MOCK_LOCATION` permission if you plan
on doing testing on Android Virtual Devices.

```xml
<uses-permission android:name="android.permission.ACCESS_MOCK_LOCATION" />
```

To enable Geotrigger functionality, you'll need to declare the `GeotriggerService` in your
`AndroidManifest.xml`. You'll also need to declare the `MessageReceiver` in order to enable
handling of Geotrigger notifications.

```xml
<application>
    <service
        android:name="com.esri.android.geotrigger.GeotriggerService"
        android:exported="false" />
    <receiver
        android:name="com.esri.android.geotrigger.MessageReceiver"
        android:permission="com.google.android.c2dm.permission.SEND">
        <intent-filter>
            <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
            <category android:name="{YOUR_PACKAGE_NAME}" />
        </intent-filter>
    </receiver>
</application>
```

### Starting and Stopping the Service

In order to start tracking you must call the static `init` method on the `GeotriggerService`,
passing in your ArcGIS Online `client_id`, Google Cloud Messaging `sender_id`, and a Tracking
Profile. The best way to start the service is by using the
[GeotriggerHelper][sample-app-geotrigger-helper] class included in this project, which will
take care of making sure that GPS and Network Location services are turned on.

In order to stop the `GeotriggerService`, you can switch the profile to `OFF`. Doing so will disable
tracking, but will not kill the service. When destroyed, the `GeotriggerService` will restart itself
and wait for further instructions:

```java
// Turn off tracking, but leave the service around for further instructions
GeotriggerService.setTrackingProfile(this, GeotriggerService.TRACKING_PROFILE_OFF);
```

If you want the `GeotriggerService` to be destroyed without restarting itself, you can alternatively
call the static `hardStop` method. This is similar to setting the profile to `OFF`, except that no
event is fired to indicate a change in tracking profile, and the `GeotriggerService` will stop
tracking and no longer run in the background:

```java
// Stop the service, and do not recover
GeotriggerService.hardStop(this);
```

## Further Reading

Guides on various topics are available as part of the [Geotrigger Documentation][geotrigger-docs],
including interacting with the `GeotriggerService`, working with the Geotrigger API, and configuring
push notifications. Information about other platforms and the API itself are also available.

[esri-site]: http://www.esri.com
[arcgis-dev-site]: https://developers.arcgis.com
[geotrigger-docs]: https://developers.arcgis.com/geotriggers
[geotrigger-docs-android-getting-started]: https://developers.arcgis.com/geotriggers/guide/android-getting-started
[android-docs]: http://developer.android.com/
[android-sdk]: http://developer.android.com/sdk/index.html
[google-play-services-setup]: http://developer.android.com/google/play-services/setup.html
[sample-app-geotrigger-activity]: https://github.com/Esri/geotrigger-sdk-android/blob/master/sample/src/com/esri/android/geotrigger/sample/GeotriggerActivity.java
[sample-app-geotrigger-helper]: https://github.com/Esri/geotrigger-sdk-android/blob/master/sample/src/com/esri/android/geotrigger/sample/GeotriggerHelper.java
