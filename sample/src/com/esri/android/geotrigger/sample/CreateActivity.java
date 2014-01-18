package com.esri.android.geotrigger.sample;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.esri.android.geotrigger.*;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.*;
import org.json.JSONObject;

public class CreateActivity extends Activity implements GeotriggerBroadcastReceiver.LocationUpdateListener {
    private static final String TAG = "CreateActivity";

    private MapView mMapView;
    private GeotriggerBroadcastReceiver mGeotriggerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        mGeotriggerReceiver = new GeotriggerBroadcastReceiver();
        initializeMapView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView == null) {
            initializeMapView();
        } else {
            mMapView.unpause();
        }

        if (mGeotriggerReceiver == null) {
            mGeotriggerReceiver = new GeotriggerBroadcastReceiver();
        } else {
            registerReceiver(mGeotriggerReceiver, GeotriggerBroadcastReceiver.getDefaultIntentFilter());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.pause();
        }

        if (mGeotriggerReceiver != null) {
            unregisterReceiver(mGeotriggerReceiver);
        }
    }

    public void onCancelClick(View sender) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCreateNoteClick(View sender) {
        EditText messageText = (EditText)findViewById(R.id.messageText);
        String message = "";
        if (messageText.getText() != null) {
            message = messageText.getText().toString();
        }

        if (!TextUtils.isEmpty(message)) {
            Envelope extent = new Envelope(mMapView.getExtent().getPoint(0).getX(),
                    mMapView.getExtent().getPoint(0).getY(),
                    mMapView.getExtent().getPoint(1).getX(),
                    mMapView.getExtent().getPoint(1).getY());
            double radius = extent.getWidth()/2;
            Point point = (Point)GeometryEngine.project(mMapView.getCenter(),
                    SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR),
                    SpatialReference.create(SpatialReference.WKID_WGS84));

            TriggerBuilder triggerBuilder = new TriggerBuilder();
            triggerBuilder.setTags("sample")
                    .setDirection(TriggerBuilder.DIRECTION_ENTER)
                    .setGeo(point.getY(), point.getX(), Math.max(radius, 50))
                    .setNotificationText(message);

            GeotriggerApiClient.runRequest(this, "trigger/create", triggerBuilder.build(), new GeotriggerApiListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Toast.makeText(CreateActivity.this, "Trigger created!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Toast.makeText(CreateActivity.this, "Error creating trigger.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationUpdate(Location location, boolean isOnDemand) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.d(TAG, String.format("Location update received (%3.6f, %3.6f)", lat, lng));
        if (isOnDemand) {
            SpatialReference mercator = SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR);
            Point point = GeometryEngine.project(lng, lat, mercator);
            Polygon buffer = GeometryEngine.buffer(point, mercator, 100, null);
            mMapView.setExtent(buffer);
        }
    }

    private void initializeMapView() {
        // Get the current location from the GeotriggerService onLocationUpdate will be called when the location is
        // retrieved and will set the map's extent.
        GeotriggerService.requestOnDemandUpdate(this);

        mMapView = (MapView)findViewById(R.id.map);
        mMapView.addLayer(new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer"));
    }
}
