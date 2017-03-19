package com.ksvteam.eticketreader;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by forest on 19.03.2017.
 */

public class RouteService {
    private static final String BASE_URL = "http://109.86.35.166:8080/";
    private ReaderActivity readerActivity;
    private OkHttpClient client = new OkHttpClient();

    public RouteService(ReaderActivity readerActivity){
        this.readerActivity = readerActivity;
    }

    public void getAllRoute() {
        final Request request = new Request.Builder()
                .url(getAbsoluteUrl("getAllRoute"))
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();

                readerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HashMap<String, String> routes = new HashMap();
                            JSONArray jsonArray = new JSONArray(responseBody);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                routes.put(
                                        jsonObject.get("numberOfRoute").toString(),
                                        jsonObject.get("routeID").toString());
                                readerActivity.setRoutes(routes);
                                Set<String> numbersOfRouteSet = routes.keySet();
                                String[] numbersOfRoute = numbersOfRouteSet.toArray(new String[numbersOfRouteSet.size()]);
                                readerActivity.initSpinner(numbersOfRoute);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private String getAbsoluteUrl(String relativeUrl) {
        String absoluteUrl = BASE_URL.concat(relativeUrl);
        return absoluteUrl;
    }

}
