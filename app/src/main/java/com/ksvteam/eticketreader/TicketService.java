package com.ksvteam.eticketreader;


import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by forest on 19.03.2017.
 */

public class TicketService {
    private static final String BASE_URL = "http://109.86.35.166:8080/";
    private ReaderActivity readerActivity;
    private OkHttpClient client = new OkHttpClient();

    public TicketService(ReaderActivity readerActivity){
        this.readerActivity = readerActivity;
    }


    public void checkTicket(String ticketID, String routeID) {
        RequestBody formBody = new FormBody.Builder()
                .add("ticketID", ticketID)
                .add("routeID", routeID)
                .build();
        final Request request = new Request.Builder()
                .url(getAbsoluteUrl("checkTicket"))
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final int responseCode = response.code();
                final String responseBody = response.body().string();

                readerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView resultField = readerActivity.getResultField();
                        resultField.setText(responseBody);

                        int color;
                        if(responseCode != 200) {
                            color = Color.RED;
                        } else {
                            color = Color.GREEN;
                        }
                        resultField.setTextColor(color);
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
