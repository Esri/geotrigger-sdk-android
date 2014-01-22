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
import com.esri.android.geotrigger.GeotriggerApiClient;
import com.esri.android.geotrigger.GeotriggerApiListener;
import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;
import com.esri.android.geotrigger.GeotriggerService;
import com.esri.android.geotrigger.TriggerBuilder;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import org.json.JSONObject;

public class CreateActivity extends Activity implements GeotriggerBroadcastReceiver.LocationUpdateListener {
    private static final String TAG = "CreateActivity";
    private static final String MAP_URL =
            "http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer";

    private MapView mMapView;
    private GeotriggerBroadcastReceiver mGeotriggerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        mGeotriggerReceiver = new GeotriggerBroadcastReceiver();

        // Get the current location from the GeotriggerService onLocationUpdate will be called when the location is
        // retrieved and will set the map's extent.
        GeotriggerService.requestOnDemandUpdate(this);

        mMapView = (MapView)findViewById(R.id.map);
        mMapView.addLayer(new ArcGISTiledMapServiceLayer(MAP_URL));
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGeotriggerReceiver, GeotriggerBroadcastReceiver.getDefaultIntentFilter());
        mMapView.unpause();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mGeotriggerReceiver);
        mMapView.pause();
    }

    public void onCancelClick(View sender) {
        onBackPressed();
    }

    public void onCreateNoteClick(View v) {
        EditText messageText = (EditText)findViewById(R.id.messageText);
        String message = "";
        if (messageText.getText() != null) {
            message = messageText.getText().toString();
        }

        if (!TextUtils.isEmpty(message)) {
            Point point = (Point)GeometryEngine.project(mMapView.getCenter(),
                    SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR),
                    SpatialReference.create(SpatialReference.WKID_WGS84));

            Point p1 = mMapView.getExtent().getPoint(0);
            Point p2 = mMapView.getExtent().getPoint(1);

            double distance = GeometryEngine.geodesicDistance(p1, p2,
                    SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR),
                    new LinearUnit(LinearUnit.Code.METER));

            double radius = Math.max(50, distance / 2.0);

            JSONObject trigger = new TriggerBuilder()
                    .setTags("sample")
                    .setDirection(TriggerBuilder.DIRECTION_ENTER)
                    .setGeo(point.getY(), point.getX(), radius)
                    .setNotificationText(message)
                    .build();

            GeotriggerApiClient.runRequest(this, "trigger/create", trigger, new GeotriggerApiListener() {
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
}
