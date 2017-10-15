package org.vontech.monet;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataActivity extends AppCompatActivity {

    private TextView cameraCountView;
    private TextView pedCountView;
    private TextView micCountView;
    private TextView weatherCountView;
    private LineChart chart;

    private View title;
    private View firstAn;
    private View secondAn;
    private View lottie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        cameraCountView = (TextView) findViewById(R.id.camera_text);
        pedCountView = (TextView) findViewById(R.id.person_text);
        micCountView = (TextView) findViewById(R.id.mic_text);
        weatherCountView = (TextView) findViewById(R.id.temp_text);
        chart = (LineChart) findViewById(R.id.chart);

        title = findViewById(R.id.title_an);
        firstAn = findViewById(R.id.first_an);
        secondAn = findViewById(R.id.second_an);
        lottie = findViewById(R.id.animation_view);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new DataTask().execute();
    }

    public class DataTask extends AsyncTask<Context, Context, Context> {

        JSONObject obj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            title.setVisibility(View.GONE);
            firstAn.setVisibility(View.GONE);
            secondAn.setVisibility(View.GONE);
            chart.setVisibility(View.GONE);
            lottie.setVisibility(View.VISIBLE);
        }

        @Override
        protected Context doInBackground(Context... contexts) {

            try {
                obj = ServerPortal.getData().getJSONObject(0).getJSONObject("fields");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);

            lottie.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            firstAn.setVisibility(View.VISIBLE);
            secondAn.setVisibility(View.VISIBLE);
            chart.setVisibility(View.VISIBLE);

            try {
                cameraCountView.setText("" + obj.getString("cams") + " cameras");
            } catch (JSONException e) {
                cameraCountView.setText("Cameras unavailable");
            }

            try {
                pedCountView.setText("" + obj.getString("peds") + " traffic sensors");
            } catch (JSONException e) {
                cameraCountView.setText("Traffic unavailable");
            }

            try {
                weatherCountView.setText("" + obj.getString("temps") + " weather sensors");
            } catch (JSONException e) {
                cameraCountView.setText("Temperature unavailable");
            }

            try {
                micCountView.setText("" + obj.getString("mics") + " microphones");
            } catch (JSONException e) {
                cameraCountView.setText("Microphones unavailable");
            }

            // Now parse other data
            Map<Long, Long> mapping = new TreeMap<>();

            try {
                JSONArray arr = Constants.getPeds();
                for(int i = 0; i < arr.length(); i += 20) {
                    JSONObject element = arr.getJSONObject(i);
                    Long count = element.getJSONObject("measures").getLong("pedestrianCount");
                    Long time = element.getLong("timestamp");
                    time = time / 20;
                    Date date = new Date(time);
                    if (mapping.containsKey(time)) {
                        //mapping.put(time, mapping.get(time) + count);
                    } else {
                        mapping.put(time, count);
                    }
                }
            } catch (Exception e) {
                Log.e("FailBoi", e.toString());
            }

            List<Entry> entries = new ArrayList<Entry>();
            for (Long key : mapping.keySet()) {
                Entry ent = new Entry(key/1000, mapping.get(key));
                entries.add(ent);
            }

            Log.e("DATA", "" + entries.size());

            final LineDataSet dataSet = new LineDataSet(entries, "Foot Traffic"); // add entries to dataset
            dataSet.setCircleColor(Color.RED);
            dataSet.setLineWidth(4);
            dataSet.setValueTextColor(android.R.color.white); // styling, ...
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            XAxis xAxis = chart.getXAxis();
            chart.setDescription(null);
            YAxis rightYAxis = chart.getAxisRight();
            rightYAxis.setEnabled(false);
            chart.getAxisLeft().setTextColor(Color.WHITE);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10f);
            xAxis.setTextColor(Color.WHITE);
            chart.getLegend().setTextColor(Color.WHITE);
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    Date date = new Date((int) value);
                    String result = (date.getHours() + 1) + ":" + (date.getMinutes() < 9 ? "0" : "") + (date.getMinutes() + 1);
                    return result;
                }
            });
            chart.invalidate(); // refresh

        }
    }

}
