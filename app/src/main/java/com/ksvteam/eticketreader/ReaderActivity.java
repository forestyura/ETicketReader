package com.ksvteam.eticketreader;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.util.HashMap;

public class ReaderActivity extends AppCompatActivity {
    private Button scanButton;
    private Button refreshButton;
    private TextView resultField;
    private Spinner spinner;
    private HashMap<String,String> routes;
    private String routeID = "";
    private ReaderActivity readerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        final Activity activity = this;
        readerActivity = this;

        scanButton = (Button) findViewById(R.id.scan_btn);
        refreshButton = (Button) findViewById(R.id.refreshButton);
        resultField = (TextView) findViewById(R.id.result_field);
        spinner = (Spinner) findViewById(R.id.spinner);
        routes = new HashMap<>();
        new RouteService(this).getAllRoute();


        //Action: QrCode run
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routeID.equals("")) {
                    Toast.makeText(activity, "Please select number of route", Toast.LENGTH_LONG);
                } else {
                    IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    intentIntegrator.setPrompt("Scan ticket");
                    intentIntegrator.setCameraId(0);
                    intentIntegrator.setBeepEnabled(false);
                    intentIntegrator.setBarcodeImageEnabled(false);
                    intentIntegrator.initiateScan();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RouteService(readerActivity).getAllRoute();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                routeID = routes.get(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //Read QrCode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled this scanning", Toast.LENGTH_LONG).show();
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(result.getContents());
                    String ticketID = jsonObject.get("ticketID").toString();

                    TicketService ticketService =  new TicketService(this);
                    ticketService.checkTicket(ticketID, routeID);

                } catch (Exception e) {
                    e.printStackTrace();
                    e.getMessage();
                    Log.e("Error", e.getMessage());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    public void initSpinner(String[] routes){
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, routes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public TextView getResultField() {
        return resultField;
    }

    public void setRoutes(HashMap<String, String> routes) {
        this.routes = routes;
    }

}


