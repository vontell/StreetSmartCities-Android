package org.vontech.monet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eegeo.mapapi.EegeoApi;
import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.MapView;
import com.eegeo.mapapi.buildings.BuildingHighlight;
import com.eegeo.mapapi.buildings.BuildingHighlightOptions;
import com.eegeo.mapapi.geometry.LatLng;
import com.eegeo.mapapi.geometry.MapFeatureType;
import com.eegeo.mapapi.map.OnMapReadyCallback;
import com.eegeo.mapapi.markers.Marker;
import com.eegeo.mapapi.markers.MarkerOptions;
import com.eegeo.mapapi.markers.OnMarkerClickListener;
import com.eegeo.mapapi.picking.PickResult;
import com.eegeo.mapapi.util.Ready;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private List<Marker> markers;
    private EegeoMap mapGlobal;
    private Handler m_timerHandler = new Handler();
    private GestureDetectorCompat m_detector;

    private OnMarkerClickListener m_markerTappedListener = new MarkerClickListenerImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialise the EegeoApi with your api key - this needs calling either in the application
        // instance, or in the activity that contains the MapView
        EegeoApi.init(this, "28a48f594ebbf550f29bf1705e249b49");

        // The MapView is contained in a layout xml
        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.basic_mapview);
        mapView.onCreate(savedInstanceState);

        m_detector = new GestureDetectorCompat(this, new TouchTapListener());

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                m_detector.onTouchEvent(event);
                return false;
            }
        });

        final Context c = this;
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final EegeoMap map) {

                markers = new LinkedList<>();
                mapGlobal = map;
                map.addMarkerClickListener(m_markerTappedListener);

                new TaskTask().execute(c);

                Snackbar.make(mapView, "Welcome to Atlanta, GA!", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(Marker m : markers) {
            mapGlobal.removeMarker(m);
        }
        mapView.onDestroy();
    }

    public class TaskTask extends AsyncTask<Context, Context, Context> {

        List<TaskActivity.Task> tasks;

        @Override
        protected Context doInBackground(Context... contexts) {

            JSONArray tasksJson = ServerPortal.getAllTasks();
            tasks = new LinkedList<>();

            try {

                Log.e("TASK", tasksJson.toString());
                for (int i = 0; i < tasksJson.length(); i++) {

                    JSONObject task = tasksJson.getJSONObject(i).getJSONObject("fields");
                    Log.e("TASK", task.toString());
                    tasks.add(new TaskActivity.Task(
                            task.getString("title"),
                            task.getString("description"),
                            task.getString("city"),
                            task.getDouble("latitude"),
                            task.getDouble("longitude"),
                            task.getInt("favorites")
                    ));

                }

                Log.e("TASKS", "GAINED some tasks: " + tasks.size());

            } catch (JSONException ignored) {
                Log.e("INFO", "Problem parsing info: " + ignored.toString());
            }

            return contexts[0];
        }

        private double minLat = -84.4070885221;
        private double minLong = 33.7336389505;
        private double maxLat = -84.3700339754;
        private double maxLong = 33.7758460904;
        private double latRange = maxLat - minLat;
        private double longRange = maxLong - minLong;

        @Override
        protected void onPostExecute(Context context) {

            double count = 0.1;
            Random rand = new Random();
            for (TaskActivity.Task t : tasks) {

                Marker m = mapGlobal.addMarker(new MarkerOptions()
                        .position(new LatLng(t.longitude, t.latitude))
                        .userData(t.description + "::::" + t.title)
                        .iconKey("park")
                        .labelText(t.title)
                );
//                double newLat = (rand.nextDouble() * latRange) + minLat;
//                double newLon = (rand.nextDouble() * longRange) + minLong;
//                mapGlobal.addMarker(new MarkerOptions()
//                        .position(new LatLng(newLon, newLat))
//                        .labelText(t.title)
//                );
                markers.add(m);
            }

        }
    }

    private class MarkerClickListenerImpl implements OnMarkerClickListener {
        public void onMarkerClick(Marker marker) {

            String[] content = marker.getUserData().split("::::");
            String title = content[1];
            String desc = content[0];

            Context context = MapActivity.this;

            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(desc)
                    .show();
        }
    }

    private class TouchTapListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            if (mapGlobal == null) {
                return false;
            }
            final Point screenPoint = new Point((int) event.getX(), (int) event.getY());
            mapGlobal.pickFeatureAtScreenPoint(screenPoint)
                    .then(new Ready<PickResult>() {
                        @UiThread
                        @Override
                        public void ready(PickResult pickResult) {
                            Toast.makeText(MapActivity.this, String.format("Picked map feature: %s", pickResult.mapFeatureType.name()), Toast.LENGTH_SHORT).show();

                            if (pickResult.mapFeatureType == MapFeatureType.Building) {

                                final BuildingHighlight highlight = mapGlobal.addBuildingHighlight(new BuildingHighlightOptions()
                                        .highlightBuildingAtScreenPoint(screenPoint)
                                        .color(ColorUtils.setAlphaComponent(Color.YELLOW, 128))
                                );

                                m_timerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mapGlobal.removeBuildingHighlight(highlight);
                                    }
                                }, 3000);
                            }
                        }
                    });

            return false;
        }

    }

}
