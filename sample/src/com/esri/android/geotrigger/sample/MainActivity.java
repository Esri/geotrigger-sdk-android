package com.esri.android.geotrigger.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;
import com.esri.android.geotrigger.GeotriggerService;

public class MainActivity extends Activity implements GeotriggerBroadcastReceiver.DeviceReadyListener {
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_REQUEST_CODE = 1;

    // A list of initial tags to apply to the device.
    // Triggers created on the server for this application, with at least one of these same tags,
    // will be active for the device.
    private static final String[] TAGS = new String[] {"sample"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String client_id = getString(R.string.client_id);
        String gcm_sender_id = getString(R.string.gcm_sender_id);
        GeotriggerService.setLoggingLevel(Log.VERBOSE);
        GeotriggerHelper.startGeotriggerService(this, PLAY_SERVICES_REQUEST_CODE,
                client_id, gcm_sender_id, TAGS, GeotriggerService.TRACKING_PROFILE_ADAPTIVE);
    }

    @Override
    public void onDeviceReady() {
        Log.d(TAG, "Device Ready!");
    }

    public void onCreateNoteClick(View sender) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }
}