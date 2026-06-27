package com.example.bookandpostroom;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

public class activitymap2 extends AppCompatActivity {
    private MapView mapView;
    public static final String latitude = "message";
    public static final String longitude = "passage";
    private static final double MAX_ZOOM_LEVEL = 14.0;
    private PointAnnotationManager pointAnnotationManager;
    private PointAnnotation pointAnnotation;
    private double latitudeOriginal;
    private double longitudeOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activitymap);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Get latitude and longitude from the intent
        String str = getIntent().getStringExtra(latitude);
        String str1 = getIntent().getStringExtra(longitude);
        latitudeOriginal = Double.parseDouble(str);
        longitudeOriginal = Double.parseDouble(str1);

        // Get the MapView from XML (token will be read from manifest)
        mapView = findViewById(R.id.mapView);

        mapView.getMapboxMap().loadStyleUri(
                Style.SATELLITE_STREETS,
                style -> {
                    // Add the custom marker image to the style
                    style.addImage("add", BitmapFactory.decodeResource(getResources(), R.drawable.location3));

                    // Set initial camera position using the original coordinates
                    setCameraPosition(longitudeOriginal, latitudeOriginal);

                    // Initialize PointAnnotationManager
                    AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                    pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

                    // Add a custom image marker to the map
                    addMarker(longitudeOriginal, latitudeOriginal);
                }
        );
    }

    private void setCameraPosition(double longitude, double latitude) {
        mapView.getMapboxMap().setCamera(
                new CameraOptions.Builder()
                        .center(Point.fromLngLat(longitude, latitude))
                        .zoom(MAX_ZOOM_LEVEL)
                        .build()
        );
    }

    private void addMarker(double longitude, double latitude) {
        // Create a PointAnnotationOptions for the marker
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage("add") // Use the marker image from the drawable folder
                .withIconSize(1f); // Adjust the size of the marker

        // Create and add the point annotation to the map
        pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
//package com.example.bookandpostroom;
//
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.mapbox.geojson.Point;
//import com.mapbox.maps.CameraOptions;
//import com.mapbox.maps.MapView;
//import com.mapbox.maps.MapboxMap;
//import com.mapbox.maps.Style;
//import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
//import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
//import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
//
//public class activitymap2 extends AppCompatActivity {
//    private MapView mapView;
//    public static final String latitude = "message";
//    public static final String longitude = "passage";
//    private static final double MAX_ZOOM_LEVEL = 14.0;
//    private PointAnnotationManager pointAnnotationManager;
//    private PointAnnotation pointAnnotation;
//    private double latitudeOriginal;
//    private double longitudeOriginal;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_activitymap);
//        int currentOrientation = getResources().getConfiguration().orientation;
//        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//        } else {
//            // Optional: if user opens the app already in landscape,
//            // you can still force portrait if you want:
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//
//        // Get latitude and longitude from the intent
//        String str = getIntent().getStringExtra(latitude);
//        String str1 = getIntent().getStringExtra(longitude);
//        latitudeOriginal = Double.parseDouble(str);
//        longitudeOriginal = Double.parseDouble(str1);
//
//        mapView = findViewById(R.id.mapView);
//        mapView.getMapboxMap().loadStyleUri(
//                Style.SATELLITE_STREETS,
//                style -> {
//                    // Add the custom marker image to the style
//                    style.addImage("add", BitmapFactory.decodeResource(getResources(), R.drawable.location3)); // Replace 'add' with your image resource name
//
//                    // Set initial camera position using the original coordinates
//                    setCameraPosition(longitudeOriginal, latitudeOriginal);
//
//                    // Initialize PointAnnotationManager
//                    AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
//                    pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
//
//                    // Add a custom image marker to the map
//                    addMarker(longitudeOriginal, latitudeOriginal);
//                }
//        );
//    }
//
//    private void setCameraPosition(double longitude, double latitude) {
//        mapView.getMapboxMap().setCamera(
//                new CameraOptions.Builder()
//                        .center(Point.fromLngLat(longitude, latitude))
//                        .zoom(MAX_ZOOM_LEVEL)
//                        .build()
//        );
//    }
//
//    private void addMarker(double longitude, double latitude) {
//        // Create a PointAnnotationOptions for the marker
//        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(longitude, latitude))
//                .withIconImage("add") // Use the marker image from the drawable folder
//                .withIconSize(1f); // Adjust the size of the marker
//
//        // Create and add the point annotation to the map
//        pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);
//    }
//}
