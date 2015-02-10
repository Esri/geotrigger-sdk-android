package com.esri.android.geotrigger.sample;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.esri.android.geotrigger.GeotriggerApiClient;
import com.esri.android.geotrigger.GeotriggerApiListener;
import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;
import com.esri.android.geotrigger.GeotriggerService;
import com.esri.android.geotrigger.TriggerBuilder;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements
        GeotriggerBroadcastReceiver.LocationUpdateListener,
        GeotriggerBroadcastReceiver.ReadyListener {
    public static final String TAG = "MainActivity";

    // Create a new application at https://developers.arcgis.com/en/applications
    private static final String AGO_CLIENT_ID = "";

    // The project number from https://cloud.google.com/console
    private static final String GCM_SENDER_ID = "";

    // A list of initial tags to apply to the device.
    // Triggers created on the server for this application, with at least one of these same tags,
    // will be active for the device.
    private static final String[] TAGS = new String[] {"my_tag"};

    // The GeotriggerBroadcastReceiver receives intents from the
    // GeotriggerService, calling any listeners implemented in your class.
    private GeotriggerBroadcastReceiver mGeotriggerBroadcastReceiver;

    private boolean mShouldCreateTrigger = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the GeotriggerBroadcastReceiver so that the listeners get called.
        // Remember to register the receiver in onResume() and unregister it in onPause().
        mGeotriggerBroadcastReceiver = new GeotriggerBroadcastReceiver();

        GeotriggerHelper.startGeotriggerService(this, AGO_CLIENT_ID, GCM_SENDER_ID, TAGS,
                GeotriggerService.TRACKING_PROFILE_ADAPTIVE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the receiver. Activity will no longer respond to
        // GeotriggerService intents. Tracking and push notification handling
        // will continue in the background.
        unregisterReceiver(mGeotriggerBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver. The default intent filter listens for all
        // intents that the receiver can handle. If you need to handle events
        // while the app is in the background, you must register the receiver
        // in the manifest.
        registerReceiver(mGeotriggerBroadcastReceiver,
                GeotriggerBroadcastReceiver.getDefaultIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_stop) {
            GeotriggerService.stop(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationUpdate(Location location, boolean isOnDemand) {
        // Called with the GeotriggerService obtains a new location update from
        // Android's native location services. The isOnDemand parameter lets you
        // determine if this location update was a result of calling
        // GeotriggerService.requestOnDemandUpdate()
        Toast.makeText(this, "Location Update Received!",
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, String.format("Location update received: (%f, %f)",
                location.getLatitude(), location.getLongitude()));

        // Create the trigger if we haven't done so already.
        if (mShouldCreateTrigger) {
            // Set create trigger flag here so that we don't create multiple
            // triggers if we get a few initial updates in rapid succession.
            mShouldCreateTrigger = false;

            // The TriggerBuilder helps build JSON parameters for use with the
            // 'trigger/create' API route.
            JSONObject params = new TriggerBuilder()
                    .setTriggerId(GeotriggerService.getDeviceId(this)) // avoid creating duplicate triggers by making the trigger ID the same as the device ID
                    .setTags(TAGS[0]) // make sure to add at least one of the tags we have on the device to this trigger
                    .setGeo(location, 100)
                    .setDirection(TriggerBuilder.DIRECTION_LEAVE)
                    .setNotificationText("You left the trigger!")
                    .build();

            // Send the request to the Geotrigger API.
            GeotriggerApiClient.runRequest(this, "trigger/create", params,
                    new GeotriggerApiListener() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            Toast.makeText(MainActivity.this, "Trigger created!",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Trigger Created");
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            Log.d(TAG, "Error creating trigger!", e);
                            // It didn't work, so we need to try again
                            mShouldCreateTrigger = true;
                        }
                    });
        }
    }

    @Override
    public void onReady() {
        // Called when the device has registered with ArcGIS Online and is ready
        // to make requests to the Geotrigger Service API.
        Toast.makeText(this, "GeotriggerService ready!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "GeotriggerService ready!");
    }
}
