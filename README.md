# Geotrigger SDK for Android

The Geotrigger Service provides real-time location tracking, geofencing,
and messaging tools for developers working on location based apps.

The Geotrigger SDK provides the `GeotriggerService`, a wrapper around 
Android's native location capabilities, which handles uploading device locations
to the Geotrigger API and receiving push notifications.

See the [Getting Started with the Geotrigger SDK for Android][geotrigger-docs-android-getting-started] guide
for more information about the Geotrigger SDK.

Guides for other topics are available as part of the [Geotrigger Documentation][geotrigger-docs],
including interacting with the `GeotriggerService`, working with the Geotrigger API, and configuring
push notifications. Information about other platforms and the API itself are also available.

## Setup

The Geotrigger Service can be included in a project by modifying the following Gradle files.

Top-level `build.gradle`:

```groovy
allprojects {
    repositories {
        ...
        // Repository for the Esri Geotrigger SDK for Android
        maven {
            url 'http://dl.bintray.com/esri/android'
        }
    }
}
```

App `build.gradle`:

```groovy
dependencies {
    ...
    // Required by the Esri Geotrigger SDK for Android
    compile 'com.google.android.gms:play-services:6.5.87'

    // Esri Geotrigger SDK for Android
    compile 'com.esri.android.geotrigger:geotrigger-sdk:1.1.0'
}
```

[esri-site]: http://www.esri.com
[arcgis-dev-site]: https://developers.arcgis.com/
[geotrigger-docs]: https://developers.arcgis.com/en/geotrigger-service
[geotrigger-docs-android-getting-started]: https://developers.arcgis.com/en/geotrigger-service/guide/android-getting-started
[geotrigger-docs-android-push-notifications]: https://developers.arcgis.com/en/geotrigger-service/guide/android-push-notifications/
[android-docs]: http://developer.android.com/
[android-sdk]: http://developer.android.com/sdk/index.html
[google-play-services-setup]: http://developer.android.com/google/play-services/setup.html
[sample-app-geotrigger-activity]: https://github.com/Esri/geotrigger-sdk-android/blob/master/sample/src/com/esri/android/geotrigger/sample/GeotriggerActivity.java
[sample-app-geotrigger-helper]: https://github.com/Esri/geotrigger-sdk-android/blob/master/sample/src/com/esri/android/geotrigger/sample/GeotriggerHelper.java
[goog-dev-console]: https://cloud.google.com/console
