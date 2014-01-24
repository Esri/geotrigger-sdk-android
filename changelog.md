# Change Log

## v0.1.2 1/23/2014:

* The SDK will now through a RuntimeException if Google Play Services is not included as a dependency, unless `setLegacyTrackingModeFallbackEnabled()` is called with `true` as the second parameter value.
* The intents used for receiving events form the SDK have had their respective actions slightly renamed. If you were registering your own subclass of `GeotriggerBroadcastReceiver` in your `AndroidManiest.xml` to receive intents sent by the SDK, the action names within your `<intent-filter>` will need to be adjusted to match what is shown [here][handling-events-doc]. (Actions that started with `com.esri.android.action` now start with `com.esri.android.geotrigger.action`).
* Reduced the number of network requests necessary when oauth tokens expires.
* HTTP requests made through the `GeotriggerApiClient` class methods are now sequential.
* The static method `GeotriggerService.hardStop()` has been renamed to `GeotriggerService.stop()`.

## v0.1.1 1/14/2014:

* Clarified log messages.
* Clarified some javadoc around resource paths for sounds and icons.
* Changed log tag to include version info.
* Added function `GeotriggerService.getTrackingProfile()` to return the tracking profile that was most recently used.

[handling-events-doc]: https://developers.arcgis.com/en/geotrigger-service/guide/android-handling-events/