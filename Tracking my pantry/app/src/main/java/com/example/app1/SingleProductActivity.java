package com.example.app1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SingleProductActivity extends AppCompatActivity {

    String auth;
    String token;
    String id;
    String name;
    String description;
    String user;
    String image;
    String type;

    TextView nameView;
    TextView descriptionView;
    ImageView productImage;
    Button deleteButton;
    Button voteButton;
    Button insertButton;

    @Override
    protected void onResume() {
        super.onResume();
        deleteButton.setClickable(true);
        voteButton.setClickable(true);
        insertButton.setClickable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);

        //Ricevo informazioni riguardanti il prodotto cliccato dall'utente
        Bundle b = getIntent().getExtras();
        token = b.getString("token");
        auth = b.getString("auth");
        id = b.getString("id");
        name = b.getString("name");
        description = b.getString("description");
        image = b.getString("image");
        user = b.getString("user");

        nameView = findViewById(R.id.nameView);
        descriptionView = findViewById(R.id.descriptionView);
        deleteButton = findViewById(R.id.deleteButton);
        voteButton = findViewById(R.id.voteButton);
        insertButton = findViewById(R.id.insertButton);
        productImage = findViewById(R.id.productImage);

        //Glide serve per inserire l'immagine, error() mi serve per caricare immagine alternativa in caso di errori
        //I metodi servono per creare Toast per comunicare l'errore nel caricamento immagine
        Glide.with(SingleProductActivity.this).load(image)
                .error(Glide.with(SingleProductActivity.this).load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Nuvola_apps_error.svg/1024px-Nuvola_apps_error.svg.png"))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(getApplicationContext(), "Errore caricamento immagine", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(productImage);


        nameView.setText(name);
        descriptionView.setText(description);

        //Tasto per cancellazione prodotto, compare prima un alert
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteButton.setClickable(false);
                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(SingleProductActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Vuoi davvero eliminare il prodotto?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DELETE_PRODUCT( id, auth);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                deleteButton.setClickable(true);
            }
        });

        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteButton.setClickable(false);
                POST_PRODUCT_PREFERENCE(token, id, auth);
            }
        });

        //Tassto per inserimento del prodotto nel proprio pantry, apre un alert con alll'interno uno spinner
        //Lo spinner serve per selezionare la categoria del prodotto
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertButton.setClickable(false);

                String[] s = { "Carboidrati", "Proteine", "Lipidi", "Bevande" };

                final ArrayAdapter<String> adp = new ArrayAdapter<String>(SingleProductActivity.this,
                        android.R.layout.simple_spinner_item, s);

                final Spinner sp = new Spinner(SingleProductActivity.this);
                sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                sp.setAdapter(adp);

                AlertDialog.Builder builder = new AlertDialog.Builder(SingleProductActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Seleziona tipo di prodotto");
                builder.setView(sp);

                builder.setPositiveButton("Inserisci", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        type = sp.getSelectedItem().toString();
                        if(type!= ""){
                            insertProduct();
                        } else{
                            voteButton.setClickable(true);
                            }
                    }
                });
                builder.create();
                builder.show();
            }
        });
    }

    public void insertProduct() {
            DBHelper DB = new DBHelper(SingleProductActivity.this);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String insertDate = df.format(new Date());

            Boolean checkinsertdata = DB.insertuserdata(name, description, insertDate, user, type);

            if(checkinsertdata == true){
                Toast.makeText(getApplicationContext(), "Inserimento avvenuto con successo", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Inserimento non avvenuto", Toast.LENGTH_SHORT).show();
            }
        }


    public void DELETE_PRODUCT(String id, String auth) {

        RequestQueue MyRequestQueue = Volley.newRequestQueue(SingleProductActivity.this);

        String url = "https://lam21.iot-prism-lab.cs.unibo.it/products/"+id;

        StringRequest MyStringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        //Codice eseguito in caso di successo
                        Toast.makeText(getApplicationContext(), "Prodotto eliminato", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(SingleProductActivity.this, ProductActivity.class);
                        myIntent.putExtra("auth", auth); //Optional parameters
                        SingleProductActivity.this.startActivity(myIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() { //Listener per gestire gli errori
                    public void onErrorResponse(VolleyError error) {
                        deleteButton.setClickable(true);
                        System.out.println(error.toString());
                        Toast.makeText(getApplicationContext(), "Errore, sicuro di aver creato il prodotto?", Toast.LENGTH_SHORT).show();
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

    //La preferenza ha rating uguale a 1, ovvero voto nel caso in cui il prodotto sia conforme alle richieste dell'user
    public void POST_PRODUCT_PREFERENCE(String token, String id, String auth) {

        RequestQueue MyRequestQueue = Volley.newRequestQueue(SingleProductActivity.this);

        String url = "https://lam21.iot-prism-lab.cs.unibo.it/votes";

        int rating = 1;

        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("rating", rating);
            object.put("productId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Inserita valutazione! ", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Errore, sicuro di non aver già votato?", Toast.LENGTH_SHORT).show();
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