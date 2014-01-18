# Android Geotrigger SDK Sample App

This app is a self contained skeleton app made to show the bare minimum of what an app would need to do to get up and running with the Geotrigger Service and SDK. It aims to be a reference point and test environment to verify that you have all of the non-code pieces set up correctly (i.e. Push Notifications, ArcGIS Online, etc).

The app creates a trigger at its current location with a 100m radius which will fire when the device leaves that location. The trigger is configured to send a Push Notification when it fires, but you'll need to make sure you're set up to send/receive push notifications first. See our [Push Notifications Guide][push-notifications-docs] for details on that.

The file `res/values/.geotrigger_config.xml` (remove the leading `.`) is provided as a place to put the `Client ID` from the application in your [AGO developer account][ago-applications], as well as the [GCM Sender ID][gcm-sender-id] for pushing messaging.

In order to build this app, include the Geotrigger SDK jars at `../sample/libs/` in the classpath (Eclipse), or add those jars as module dependencies (Intellij). In addition, you will need to set up [Google Play Services][google-play-services] as a dependency of the app as well.

[push-notifications-docs]:https://developers.arcgis.com/en/geotrigger-service/guide/android-push-notifications/
[ago-applications]:https://developers.arcgis.com/en/applications/
[gcm-sender-id]:http://developer.android.com/google/gcm/gs.html#create-proj
[google-play-services]:http://developer.android.com/google/play-services/setup.html
