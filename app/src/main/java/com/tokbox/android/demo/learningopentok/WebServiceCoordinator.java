package com.tokbox.android.demo.learningopentok;

import android.content.Context;
import android.util.Log;
import android.widget.NumberPicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceCoordinator {

    private static final String CHAT_SERVER_URL = "https://teleportinopentok.herokuapp.com";
    private static final String SESSION_INFO_ENDPOINT = "http://teleportinserver.azurewebsites.net/api/session?channel=";

    private static final String LOG_TAG = WebServiceCoordinator.class.getSimpleName();

    private final Context context;
    private Listener delegate;

    public WebServiceCoordinator(Context context, Listener delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    public void fetchSessionConnectionData(int channel) {
        RequestQueue reqQueue = Volley.newRequestQueue(context);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET, SESSION_INFO_ENDPOINT + channel, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String apiKey = String.valueOf(response.getInt("apikey"));
                    String sessionId = response.getString("sessionId");
                    String token = response.getString("token");

                    Log.i(LOG_TAG, apiKey);
                    Log.i(LOG_TAG, sessionId);
                    Log.i(LOG_TAG, token);

                    delegate.onSessionConnectionDataReady(apiKey, sessionId, token);

                } catch (JSONException e) {
                    delegate.onWebServiceCoordinatorError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                delegate.onWebServiceCoordinatorError(error);
            }
        }));
    }

    public static interface Listener {
        void onSessionConnectionDataReady(String apiKey, String sessionId, String token);
        void onWebServiceCoordinatorError(Exception error);
    }
}

