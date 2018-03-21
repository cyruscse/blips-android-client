package com.fourth.blips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eliab on 2018-01-11.
 */

public class ServerInterface {

    private static final int MAX_CACHE_SIZE = 1024*1024;
    private static final String AWS_SERVER_URL = "http://www.blipsserver-env.us-east-2.elasticbeanstalk.com";

    private static Context appContext;
    private static ServerInterface appServerInstance;
    private RequestQueue appReqQueue;

    private enum ServerInterFaceErrors{
        BAD_JSON_REQUEST_ERR,
        BAD_SERVER_RESPONSE_ERR,
        JSON_PARSE_FAILURE_ERR;
    }

    public ServerInterface (Context context) {
        appContext = context;
        appReqQueue = getRequestQueue();
    }

    public static synchronized ServerInterface getInstance (Context context) {

        if (appServerInstance == null) {
            appServerInstance = new ServerInterface(context);
        }

        return appServerInstance;
    }

    public RequestQueue getRequestQueue () {

        if (appReqQueue == null) {
            appReqQueue = Volley.newRequestQueue(appContext.getApplicationContext());
        }

        return appReqQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void sendRequest(JSONObject jsonReq, final VolleyCallback callback) {

//        // TODO protect with null case handling
//        JSONObject jsonReq = generateReqObj(name, value);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, AWS_SERVER_URL, jsonReq, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    // TODO need proper error handling, implement custom error codes?
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("*******ERROR");
                        error.printStackTrace();
                    }
                });

        // Access the RequestQueue through your singleton class.
        ServerInterface.getInstance(appContext.getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    public JSONObject generateReqObj(String name, String value){
        try {
            return new JSONObject().put(name,value);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Context getAppContext() {
        return appContext;
    }


    public interface VolleyCallback {
        void onSuccess(JSONObject response);
    }
}
