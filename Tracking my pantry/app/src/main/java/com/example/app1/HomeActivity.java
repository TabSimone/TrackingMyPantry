package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    Button remoteButton;
    Button pantryButton;

    String user;

    @Override
    protected void onResume() {
        super.onResume();
        pantryButton.setClickable(true);
        remoteButton.setClickable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Leggo immediatamente la risposta che mi è stata fornita dal server, per autenticazione
        Bundle b = getIntent().getExtras();
        String auth = b.getString("response");
        user = b.getString("user");

        remoteButton = findViewById(R.id.remoteButton);
        pantryButton = findViewById(R.id.pantryButton);

        remoteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                remoteButton.setClickable(false);
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                intent.putExtra("auth", auth);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        pantryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pantryButton.setClickable(false);
                Intent intent = new Intent(HomeActivity.this, PantryActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


    }

}