package com.esri.android.geotrigger.sample;

import android.app.Activity;
import android.os.Bundle;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;

public class CreateActivity extends Activity {
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView = (MapView)findViewById(R.id.map);
        mMapView.addLayer(new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer"));
    }
}
