package org.vontech.monet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;

import static android.view.View.GONE;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new TaskTask().execute(this);

    }

    public static class Task {

        String title;
        String description;
        String city;
        double latitude;
        double longitude;
        int favorites;

        public Task(String title, String description, String city, double latitude, double longitude, int favorites) {
            this.title = title;
            this.description = description;
            this.city = city;
            this.latitude = latitude;
            this.longitude = longitude;
            this.favorites = favorites;
        }

    }

    public class TaskTask extends AsyncTask<Context, Context, Context> {

        List<Task> tasks;

        @Override
        protected Context doInBackground(Context... contexts) {

            JSONArray tasksJson = ServerPortal.getAllTasks();
            tasks = new LinkedList<>();

            try {

                Log.e("TASK", tasksJson.toString());
                for (int i = 0; i < tasksJson.length(); i++) {

                    JSONObject task = tasksJson.getJSONObject(i).getJSONObject("fields");
                    Log.e("TASK", task.toString());
                    tasks.add(new Task(
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

        @Override
        protected void onPostExecute(final Context context) {

            LinearLayout taskList = (LinearLayout) findViewById(R.id.task_list);
            taskList.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE );

            for (int i = 0; i < 100; i++) {

                final Task t = tasks.get(i);
                final View card = inflater.inflate(R.layout.task_card, null);
                ((TextView) card.findViewById(R.id.task_title)).setText(t.title);
                ((TextView) card.findViewById(R.id.task_description)).setText(t.description);
                ((TextView) card.findViewById(R.id.fav_view)).setText("" + t.favorites + " faves");
                final CardView likesAndStuff = card.findViewById(R.id.awesome_card);
                likesAndStuff.findViewById(R.id.twitter_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                .text(t.title + " - " + t.description + "@Cityofatlanta @stsmartcity");
                        builder.show();

                    }
                });

                // Initially hide the content view.

                taskList.addView(card);

                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        crossfade(likesAndStuff, likesAndStuff.getAlpha() == 0);
                    }
                });

            }

        }
    }

    private void crossfade(final View mContentView, boolean forward) {

        // Retrieve and cache the system's default "short" animation time.
        int mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        if(forward) {
            mContentView.setAlpha(0f);
            mContentView.setVisibility(View.VISIBLE);
            mContentView.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
        } else {
            mContentView.setAlpha(1f);
            mContentView.setVisibility(View.VISIBLE);
            mContentView.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mContentView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
        }



    }

}
