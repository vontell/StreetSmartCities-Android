package org.vontech.monet;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vontell on 10/14/17.
 */
public class ServerPortal {

    private static final String HOST = "http://team-town.herokuapp.com/";
    private static final String CITY = "api/city/";
    private static final String USER = "api/user/";
    private static final String TASK = "api/tasks/";
    private static final String DATA = "api/data/";

    public static JSONObject getCityInfo(String city) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(HOST + CITY + city + "/")
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException exception) {}
          catch (JSONException exception) {}

        return new JSONObject();
    }

    public static JSONObject getUserInfo(String username) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(HOST + USER + username + "/")
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException exception) {}
        catch (JSONException exception) {}

        return new JSONObject();
    }

    public static JSONArray getAllTasks() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(HOST + TASK)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException exception) {}
        catch (JSONException exception) {}

        return new JSONArray();
    }

    public static JSONArray getData() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(HOST + DATA)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException exception) {}
        catch (JSONException exception) {}

        return new JSONArray();
    }

}
