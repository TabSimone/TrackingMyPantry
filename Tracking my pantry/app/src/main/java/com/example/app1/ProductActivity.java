package com.example.app1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    EditText barcodeText;
    Button goButton;
    Button scanButton;
    TextView tv1;
    String auth;
    String user;

    @Override
    protected void onResume() {
        super.onResume();
        scanButton.setClickable(true);
        goButton.setClickable(true);
        barcodeText.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Bundle b = getIntent().getExtras();
        auth = b.getString("auth");
        user = b.getString("user");

        barcodeText = findViewById(R.id.barcodeText);
        goButton = findViewById(R.id.goButton);
        scanButton = findViewById(R.id.scanButton);
        tv1 = findViewById(R.id.goButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanButton.setClickable(false);
                IntentIntegrator intentIntegrator = new IntentIntegrator(ProductActivity.this);
                //Testo
                intentIntegrator.setPrompt("Per usare flash usa volume");
                //Setto beep
                intentIntegrator.setBeepEnabled(true);
                //Orientamento bloccato
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                //Initiate scan
                intentIntegrator.initiateScan();
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goButton.setClickable(false);
                String barcode = barcodeText.getText().toString();
                GET_PRODUCTS_BY_BARCODE(barcode, auth);
            }

        });
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode,
        @Nullable @org.jetbrains.annotations.Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            //initialize intent result
            IntentResult intentResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data
            );
            //Check condition
            if (intentResult.getContents() != null) {
                GET_PRODUCTS_BY_BARCODE(intentResult.getContents(), auth);
            } else {
                Toast.makeText(getApplicationContext(), "Ops, non funziona", Toast.LENGTH_SHORT).show();
            }

        }

    public void GET_PRODUCTS_BY_BARCODE(String barcode, String auth){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(ProductActivity.this);
        String url = "https://lam21.iot-prism-lab.cs.unibo.it/products?barcode="+barcode;
        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                public void onResponse(String response) {
                    //Codice eseguito in caso di successo
                    Intent myIntent = new Intent(ProductActivity.this, ListActivity.class);
                    myIntent.putExtra("response", response); //Optional parameters
                    myIntent.putExtra("barcode", barcode);
                    myIntent.putExtra("auth", auth);
                    myIntent.putExtra("user", user);
                    ProductActivity.this.startActivity(myIntent);
                }
            },
            new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                public void onErrorResponse(VolleyError error) {
                    //Codice da eseguire in caso di errore
                    goButton.setClickable(true);
                    Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();
                }
            })
        {
            public Map<String, String> getHeaders() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Authorization", "Bearer " + auth);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

}
