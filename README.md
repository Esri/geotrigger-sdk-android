ArcGIS Geotrigger SDK for Android
======================

The ArcGIS Geotrigger Service provides real-time location tracking, geofencing,
and messaging tools for developers working on location based apps.

The Geotrigger SDK provides the `GeotriggerService`, a wrapper around 
Android's native location capabilities, which handles uploading device locations
to the Geotrigger API and receiving push notifications.

## Use Cases

* Easily send messages to users when they arrive at certain areas.
* Create a workout tracking app without needing to build your own backend.
* Quickly track whether people are using your app more in your stores or at home.

## Tracking Profiles

Tracking profiles are used by the `GeotriggerService` to optimize battery usage
for different use cases.

* **Rough** uses minimal battery power to get approximate locations for the
    user. Suitable for city to neighborhood level accuracy.
* **Adaptive** adjusts tracking resolution (and battery drain) depending on
    proximity to triggers and other variables.
* **Fine** collects the most accurate location data, at the expense of battery
    life.

## Triggers and Tags

A *trigger* is a geofence that will execute an *action* when a *condition* is
satisfied.  A *condition* is minimally comprised of a geometry and direction
(entering or leaving). An *action* can be used to send a push notification to
a device, or notify a remote server, allowing you to implement custom
behaviors.

*Tags* are a way of matching *triggers* to *devices*. In order for a *device*
to fire a *trigger*, they must share a *tag*. *Tags* can also be used to control
visibility and permissions.
