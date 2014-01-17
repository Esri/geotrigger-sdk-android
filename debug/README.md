# Android Geotrigger SDK Sample App

This app is a self contained skeleton app made to show the bare minimum of what an app would need to do to get up and running with the Geotrigger Service and SDK. It aims to be a reference point and test environment to verify that you have all of the non-code pieces set up correctly (i.e. Push Notifications, ArcGIS Online, etc).

The app creates a trigger at its current location with a 100m radius which will fire when the device leaves that location. The trigger is configured to send a Push Notification when it fires, but you'll need to make sure you're set up to send/receive push notifications first. See our [Push Notifications Guide][push-notifications-docs] for details on that.


[push-notifications-docs]:https://developers.arcgis.com/geotrigger-service/android-push-notifications
