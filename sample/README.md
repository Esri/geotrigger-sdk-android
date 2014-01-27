# Android Geotrigger SDK Sample App

This app is a self contained skeleton app made to show a typical use-case for the Geotrigger Service and SDK. This app goes a few steps further than the app in `../debug`, and allows you to create triggers (with a radius equal to the width of the viewable portion of the map) anywhere on the map that will be fired when entering them with the device.  When that happens, you will receive a push notification that displays the message you entered when creating the trigger.

Make sure to see our [Push Notifications Guide][push-notifications-docs] for details on setting up push notifications for Android. The file `res/values/.geotrigger_config.xml` (remove the leading `.`) is provided as a place to put the `Client ID` from the application in your [AGO developer account][ago-applications], as well as the [GCM Sender ID][gcm-sender-id] for pushing messaging.

In order to build this app, you will need to download the [ArcGIS Android SDK][arcgis-sdk-android]. The contents of the `libs/` directory need to be added to your classpath (Eclipse) or add that directory as a module dependency (Intellij). In addition, you will need to set up [Google Play Services][google-play-services] as a dependency of the app as well.


[push-notifications-docs]:https://developers.arcgis.com/en/geotrigger-service/guide/android-push-notifications/
[ago-applications]:https://developers.arcgis.com/en/applications/
[gcm-sender-id]:http://developer.android.com/google/gcm/gs.html#create-proj
[arcgis-sdk-android]:https://developers.arcgis.com/en/android/install.html
[google-play-services]:http://developer.android.com/google/play-services/setup.html
