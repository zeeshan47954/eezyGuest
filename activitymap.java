package com.example.bookandpostroom;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation;

public class activitymap extends AppCompatActivity {
    private MapView mapView;
    public static final String latitude="message";
    public static final String longitude="passage";
    private static final double MAX_ZOOM_LEVEL = 14.0;
    private CircleAnnotationManager circleAnnotationManager;
    private CircleAnnotation circleAnnotation;
    private double roundedLat;
    private double roundedLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activitymap);
        String str = getIntent().getStringExtra(latitude);
        String str1 = getIntent().getStringExtra(longitude);
        float number = Float.parseFloat(str);
        float number1=Float.parseFloat(str1);
        mapView = findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(
                Style.SATELLITE_STREETS,
                style -> {
                    // Round the coordinates to reduce precision
                    roundedLat = Math.round(number * 1000.0) / 1000.0;
                    roundedLon = Math.round(number1 * 1000.0) / 1000.0;

                    // Set initial camera position
                    setCameraPosition(roundedLon, roundedLat);

                    // Initialize circle annotation manager
                    AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                    circleAnnotationManager = CircleAnnotationManagerKt.createCircleAnnotationManager(annotationPlugin, mapView);

                    // Add a circle to the map
                    addCircle(roundedLon, roundedLat);

                    // Add camera movement listener
                    mapView.getMapboxMap().addOnCameraChangeListener(cameraChanged -> {
                        double currentZoom = mapView.getMapboxMap().getCameraState().getZoom();
                        updateCircleVisibility(currentZoom);
                    });
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

    private void addCircle(double longitude, double latitude) {
        CircleAnnotationOptions circleAnnotationOptions = new CircleAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withCircleRadius(100f)
                .withCircleColor(Color.RED)
                .withCircleOpacity(0.5f);

        circleAnnotation = circleAnnotationManager.create(circleAnnotationOptions);
    }

    private void updateCircleVisibility(double currentZoom) {
        if (currentZoom > MAX_ZOOM_LEVEL && circleAnnotation != null) {
            circleAnnotationManager.delete(circleAnnotation);
            circleAnnotation = null;
        } else if (currentZoom <= MAX_ZOOM_LEVEL && circleAnnotation == null) {
            addCircle(roundedLon, roundedLat);
        }
    }
}