package com.esri.android.geotrigger.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import com.esri.android.geotrigger.GeotriggerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * A class that packages some convenience methods for starting the Geotrigger Service.
 */
public final class GeotriggerHelper {
    private static final String TAG = "GeotriggerHelper";
    private static boolean sShouldStartGeotriggers;
    private static boolean sSkipPlayServicesInstall;

    private GeotriggerHelper() {}

    private enum AvailableProviders {GPS, NETWORK, BOTH, NEITHER}

    /**
     * Start Geotriggers by first prompting the user to install Google Play Services if not already installed, and
     * enable GPS and Network providers if not already enabled.  A good place to call this would be from the
     * {@link Activity#onStart} method of an {@link Activity} that uses the Geotrigger location services.
     *
     * @param activity The {@link Activity} that is currently displayed.
     * @param requestCode A request that may be returned in {@link Activity#onActivityResult}
     *                    if overridden in the provided {@link Activity}
     * @param clientId Client ID from https://developers.arcgis.com/en/applications
     * @param senderId Project number from https://code.google.com/apis/console
     * @param tags A list of tag names to apply to the device as soon as possible.
     * @param profile The Geotrigger profile (ie: FINE, ADAPTIVE, ROUGH, OFF) to start the service in.
     */
    public static void startGeotriggerService(final Activity activity, int requestCode, String clientId, String senderId,
                                              String[] tags, String profile) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null.");
        }

        // This will not compile without Google Play Services included in the project.
        // Including Google Play Services is *Crucial* to conserving battery power, see:
        // http://developer.android.com/google/play-services/setup.html

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (status != ConnectionResult.SUCCESS && !sSkipPlayServicesInstall) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                // This error can be fixed.
                // Let's delay starting Geotriggers until we know the outcome of the recovery attempt
                sShouldStartGeotriggers = false;

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
                sShouldStartGeotriggers = true;
            }

        } else {
            Log.d(TAG, "Google Play Services is available. Checking on GPS and Network providers.");

            // Check for GPS and Network Provider, and prompt the user to enable them if not available.
            switch (checkForRequiredProviders(activity)) {
                case NEITHER:
                    sShouldStartGeotriggers = false;
                    break;
                case GPS:
                case NETWORK:
                case BOTH:
                    sShouldStartGeotriggers = true;
                    break;
            }
        }

        if (sShouldStartGeotriggers) {
            // Start the GeotriggerService, in the provided profile
            GeotriggerService.init(activity, clientId, senderId, tags, profile);
        } else {
            Log.d(TAG, "Delaying the start of Geotriggers, as we are installing Google Play Services," +
                    " or awaiting the availability of at least one provider.");
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

        String dialogMessage;
        if (!gotGps && !gotNetwork) {
            dialogMessage = context.getString(R.string.both_gps_and_network_disabled);
            returnVal = AvailableProviders.NEITHER;
        } else if (!gotGps) {
            dialogMessage = context.getString(R.string.gps_provider_disabled);
            returnVal = AvailableProviders.NETWORK;
        } else if (!gotNetwork) {
            dialogMessage = context.getString(R.string.network_provider_disabled);
            returnVal = AvailableProviders.GPS;
        } else {
            Log.d(TAG, "Both GPS and Network providers are available.");
            return AvailableProviders.BOTH;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(dialogMessage);
        dialogBuilder.setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        dialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        dialogBuilder.show();

        return returnVal;
    }
}
