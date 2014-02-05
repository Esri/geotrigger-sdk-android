package com.esri.android.geotrigger.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.esri.android.geotrigger.GeotriggerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

// This will not compile without Google Play Services included in the project.
// Including Google Play Services is *Crucial* to conserving battery power, see:
// http://developer.android.com/google/play-services/setup.html

/**
 * A class that packages some convenience methods for starting the Geotrigger Service.
 */
public final class GeotriggerHelper {
    private static final String TAG = "GeotriggerHelper";
    private static boolean sSkipPlayServicesInstall;

    private GeotriggerHelper() {}

    private enum AvailableProviders {GPS, NETWORK, BOTH, NEITHER}

    /**
     * Start Geotriggers by first prompting the user to (1) install Google Play Services if not already installed,
     * (2) enable GPS and Network providers if not already enabled, and (3) enable background wifi scanning if not
     * already enabled.
     *
     * <p>A good place to call this would be from the{@link Activity#onStart} method of an {@link Activity}
     * that uses the Geotrigger location services.
     *
     * @param activity The {@link Activity} that is currently displayed.
     * @param requestCode A request that may be returned in {@link Activity#onActivityResult}
     *                    if overridden in the provided {@link Activity}
     * @param clientId Client ID from https://developers.arcgis.com/en/applications
     * @param senderId Project number from https://code.google.com/apis/console
     * @param tags A list of tag names to apply to the device as soon as possible.
     * @param profile The Geotrigger profile (ie: FINE, ADAPTIVE, ROUGH, OFF) to start the service in.
     */
    public static void startGeotriggerService(final Activity activity, int requestCode, String clientId,
                                              String senderId, String[] tags, String profile) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null.");
        }

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (status != ConnectionResult.SUCCESS && !sSkipPlayServicesInstall) {
            // We could not detect Google Play Services, and the user hasn't indicated a desire to skip
            // installation of Google Play Services, so let's check if this error can be recovered from.
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                // This error can be fixed, and Google Play Services provides a dialog for doing so:
                Dialog playServicesDialog = GooglePlayServicesUtil.getErrorDialog(status, activity,
                        requestCode, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(activity, activity.getString(R.string.play_services_dialog_cancel_toast),
                                Toast.LENGTH_LONG).show();

                        // The user has decided not to install Google Play Services.
                        // We should just start Geotriggers without Google Play Services.
                        sSkipPlayServicesInstall = true;
                    }
                });

                if (playServicesDialog != null) {
                    playServicesDialog.show();
                }
            } else {
                Log.d(TAG, "Google Play Services not available, and cannot be installed on this device.");

                // Geotriggers can still work, using an older, less battery-efficient mode of operation.
            }

        } else {
            Log.d(TAG, "Google Play Services is available (or user has declined to install it). " +
                    "Checking on GPS and Network providers.");

            // Check for GPS and Network Provider, and prompt the user to enable them if not available.
            if (checkForRequiredProviders(activity) == AvailableProviders.BOTH) {
                // We have both providers enabled, great!
                // Let's also verify that we get make the most efficient use of the Network provider by checking
                // that devices running SDK 18 (Jelly Bean MR2) and above have enabled background scanning.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);

                    // The call to isScanAlwaysAvailable() requires the permission:
                    //      <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
                    if (!wifiManager.isScanAlwaysAvailable()) {
                        showSettingsDialog(activity, WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE,
                                activity.getString(R.string.enable_background_scanning));
                        return;
                    }
                }

                // For devices running an older version of Android, or those that have background scanning
                // enabled, we have enough information to start the GeotriggerService and be reasonably sure
                // that it will perform well.
                GeotriggerService.init(activity, clientId, senderId, tags, profile);
            } else {
                Log.d(TAG, "Delaying the start of Geotriggers, as we are awaiting the availability of " +
                        "at least one provider.");
            }
        }
    }

    /**
     * This method checks to see if we can access GPS and Wi-Fi data, prompting the user to enable them if not.
     *
     * @param context
     * @return
     */
    private static AvailableProviders checkForRequiredProviders(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null.");
        }

        AvailableProviders returnVal;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gotGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean gotNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        String msg;
        if (!gotGps && !gotNetwork) {
            msg = context.getString(R.string.both_gps_and_network_disabled);
            returnVal = AvailableProviders.NEITHER;
        } else if (!gotGps) {
            msg = context.getString(R.string.gps_provider_disabled);
            returnVal = AvailableProviders.NETWORK;
        } else if (!gotNetwork) {
            msg = context.getString(R.string.network_provider_disabled);
            returnVal = AvailableProviders.GPS;
        } else {
            Log.d(TAG, "Both GPS and Network providers are available.");
            return AvailableProviders.BOTH;
        }

        showSettingsDialog(context, android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS, msg);

        return returnVal;
    }

    /**
     * A helper method for putting up a settings dialog.
     *
     * @param context
     * @param action
     * @param msg
     */
    private static void showSettingsDialog(final Context context, final String action, String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(action));
            }
        });

        dialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialogBuilder.show();
    }
}
