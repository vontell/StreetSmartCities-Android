package org.vontech.monet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TwitterLoginButton loginButton;

    private TextView usernameView;
    private TextView cityScoreView;
    private TextView userScoreView;
    private ImageView profileImage;
    private Context globalBoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First, let's initialize everything, including Twitter
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("7qHVExDminMOiyn92m8FD7bqb", "CoRnGnbX7ty1230PeSa3OQsHmOGdTwmMZiPM3K0L6vreN5nifr"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        this.globalBoi = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginButton = (TwitterLoginButton) findViewById(R.id.fab);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                loginButton.setVisibility(View.GONE);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tweet_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query("@stsmartcity")
                .maxItemsPerRequest(50)
                .build();

        final TweetTimelineRecyclerViewAdapter adapter =
                new TweetTimelineRecyclerViewAdapter.Builder(this)
                        .setTimeline(searchTimeline)
                        .setViewStyle(R.style.tw__TweetDarkWithActionsStyle)
                        .build();

        recyclerView.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //profileImage = navigationView.findViewById(R.id.profileImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Here is where we instantiate a lot of the UI
        usernameView = (TextView) findViewById(R.id.username);
        cityScoreView = (TextView) findViewById(R.id.city_score);
        userScoreView = (TextView) findViewById(R.id.user_score);

        cityScoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCityScore();
            }
        });

        userScoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserScore();
            }
        });

        new InformationTask().execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loginButton.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_map:
                Intent mapActivity = new Intent(this, MapActivity.class);
                startActivity(mapActivity);
                break;
            case R.id.nav_data:
                Intent dataActivity = new Intent(this, DataActivity.class);
                startActivity(dataActivity);
                break;
            case R.id.nav_tasks:
                Intent taskActivity = new Intent(this, TaskActivity.class);
                startActivity(taskActivity);
                break;
            case R.id.nav_chat:
                Intent chatActivity = new Intent(this, ChatActivity.class);
                startActivity(chatActivity);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCityScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("City SmartScore");
        builder.setMessage("The City SmartScore is a custom metric used to determine how well a given city uses technology for it\'s people." +
                " Factors that affect it include:\n\t - Number of IoT devices\n\t - Accuracy of devices\n\t - Number of participating citizens");
        builder.setPositiveButton("Thanks!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void showUserScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your SmartScore");
        builder.setMessage("Your SmartScore is a custom metric used to determine how well you contribute to your city." +
                " Factors that affect it include:\n\t - Number of logged tasks\n\t - Social media presence\n\t - In-app impressions" +
                "\nMake sure to raise your score by working as a team through the task system!");
        builder.setPositiveButton("Thanks!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    class InformationTask extends AsyncTask<Void, Void, Void> {

        String username = "vontell";
        String name = "Aaron Vontell";
        String cityName = "Atlanta, GA";
        int cityScore = 34;
        int userScore = 12;
        String userImage = null;

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject city = ServerPortal.getCityInfo("Atlanta,GA");
            JSONObject user = ServerPortal.getUserInfo("vontell");
            Log.e("INFO", city.toString());
            Log.e("INFO", user.toString());

            try {
                username = user.getString("username");
                name = user.getString("name");
                cityName = city.getString("city").replace(",", ", ");
                cityScore = city.getInt("score");
                userScore = user.getInt("score");
                userImage = user.getString("image");
            } catch (JSONException ignored) {
                Log.e("INFO", "Problem parsing info: " + ignored.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            userScoreView.setText("" + userScore);
            cityScoreView.setText("" + cityScore);
            usernameView.setText("Welcome, " + name + "!");
            //Picasso.with(globalBoi).load(userImage).into(profileImage);


        }
    }

}
