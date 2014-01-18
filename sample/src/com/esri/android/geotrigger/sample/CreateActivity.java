package com.esri.android.geotrigger.sample;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.esri.android.geotrigger.GeotriggerBroadcastReceiver;
import com.esri.android.geotrigger.GeotriggerService;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.geometry.*;

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
        // TODO: Create trigger!
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
