package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

//In questa activity si va a creare un nuovo prodotto
public class
NewProductActivity extends AppCompatActivity{

    Button newButton;
    EditText nameText;
    EditText descriptionText;
    EditText imageText;

    String auth;
    String token;
    String barcode;
    String name;
    String description;
    String image;

    @Override
    protected void onResume()
    {
        super.onResume();
        newButton.setClickable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        Toast.makeText(getApplicationContext(), "Se si lascia il campo immagine vuoto, non verrà inserita un'immagine", Toast.LENGTH_SHORT).show();

        Bundle b = getIntent().getExtras();
        token = b.getString("token");
        barcode = b.getString("barcode");
        auth = b.getString("auth");

        newButton = findViewById(R.id.newProductButton);
        nameText = findViewById(R.id.nameText);
        descriptionText = findViewById(R.id.descriptionText);
        imageText = findViewById(R.id.imageText);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newButton.setClickable(false);
                name = nameText.getText().toString();
                description = descriptionText.getText().toString();
                image = imageText.getText().toString();

                if(TextUtils.isEmpty(nameText.getText()) || TextUtils.isEmpty(descriptionText.getText())){
                    Toast.makeText(getApplicationContext(), "Non hai inserito campi", Toast.LENGTH_SHORT).show();
                    newButton.setClickable(true);
                }else{
                    POST_PRODUCTS_DETAILS( token,  barcode,  name,  description, auth, image);
                }
            }
        });
    }

    //Questo è il metoodo con cui si va a inserire il prodotto nel database remoto
    //È stato utilizzato un JSONObject, in quanto era necessario sia aggiungere parametri sia dichiarare il token per la autorizzazione
    public void POST_PRODUCTS_DETAILS(String token, String barcode, String newName, String newDescription, String auth, String image) {

        RequestQueue MyRequestQueue = Volley.newRequestQueue(NewProductActivity.this);

        String url = "https://lam21.iot-prism-lab.cs.unibo.it/products";

        JSONObject object = new JSONObject();
        try {
            object.put("test", false);
            object.put("token", token);
            object.put("name", newName);
            object.put("description", newDescription);
            object.put("barcode", barcode);
            if(TextUtils.isEmpty(imageText.getText())){
                object.put("img", null);
            }else{
                object.put("img", image);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplicationContext(), "Inserito nuovo record! ", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth);
                return params;
            }
        };

        MyRequestQueue.add(request);

    }

}