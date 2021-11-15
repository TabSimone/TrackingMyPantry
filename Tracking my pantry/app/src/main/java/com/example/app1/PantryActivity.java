package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PantryActivity extends AppCompatActivity {
    //In questa activity vengono semplicemente dichiarati i bottoni per le tre funzioni relative all'utente
    //Visualizzazione prodotti del pantry, creazione data di scadenza e creazione di location

    String user;

    Button pantryListButton;
    Button expireButton;
    Button locationButton;

    @Override
    protected void onResume() {
        super.onResume();
        pantryListButton.setClickable(true);
        expireButton.setClickable(true);
        locationButton.setClickable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        Bundle b = getIntent().getExtras();
        user = b.getString("user");

        expireButton = findViewById(R.id.expireButton);
        pantryListButton = findViewById(R.id.pantryListButton);
        locationButton = findViewById(R.id.locationButton);

        pantryListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pantryListButton.setClickable(false);

                Intent myIntent = new Intent(PantryActivity.this, PantryListActivity.class);
                myIntent.putExtra("user", user); //Optional parameters
                PantryActivity.this.startActivity(myIntent);

            }
        });

        expireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expireButton.setClickable(false);

                Intent myIntent = new Intent(PantryActivity.this, ExpireActivity.class);
                myIntent.putExtra("user", user); //Optional parameters
                PantryActivity.this.startActivity(myIntent);

            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationButton.setClickable(false);

                Intent myIntent = new Intent(PantryActivity.this, LocationActivity.class);
                myIntent.putExtra("user", user); //Optional parameters
                PantryActivity.this.startActivity(myIntent);

            }
        });


    }
}