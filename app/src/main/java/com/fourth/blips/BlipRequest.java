package com.fourth.blips;

import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eliab on 2018-01-23.
 */

/*
    All types of requests to the server are handled here.
 */

public class BlipRequest {

    private ServerInterface server;

    public BlipRequest(ServerInterface server) {
        this.server = server;

    }

    public void requestAttractionList(ServerInterface.VolleyCallback callbackFunc) {

        //request parameters
        Map<String, String> params = new HashMap<>();
        params.put("requestType","dbsync");
        params.put("syncType","getattractions");

        JSONObject syncReq = new JSONObject(params);

        if (syncReq!=null) {
            server.sendRequest(syncReq, callbackFunc);
        }

//        return attractionList;
    }
//
//    public JSONObject getAttractionList(){
//        return attractionList;
//    }
}
