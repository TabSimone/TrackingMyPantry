package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button loginButton;
    Button registerButton;
    Button exitButton;

    String user;

    //Metodo per redere ricliccabili i pulsanti, quando viene riusata la mainactivity
    @Override
    protected void onResume()
    {
        super.onResume();
        registerButton.setClickable(true);
        loginButton.setClickable(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Una volta entrato nella mainactivity viene creata la serie di notifiche giornaliere
        myAlarm();

        email = findViewById(R.id.emailText);
        password = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        exitButton = findViewById(R.id.exitButton);

        //Button per uscire
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                System.exit(0);
            }
        });

        //Button per aprire activity registrazione, usato seclickable false in quanto se
        //si cliccava due volte, si apriva due volte la pagina di registrazine
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerButton.setClickable(false);
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Tasto per il login
        loginButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               user = email.getText().toString();
               loginButton.setClickable(false);
               LOGIN( email.getText().toString(), password.getText().toString()); //Chiamata volley per il login
           }

        //Metodo con login
        public void LOGIN(final String email, final String password){
            RequestQueue MyRequestQueue = Volley.newRequestQueue(MainActivity.this);
            String url = "https://lam21.iot-prism-lab.cs.unibo.it/auth/login";
            StringRequest MyStringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
                public void onResponse(String response) {
                    //Codice eseguito in caso di successo, stringa response ha autirizzazione passata dal server
                    Toast.makeText(getApplicationContext(), "Benvenuto", Toast.LENGTH_SHORT).show();
                    response = response.substring(16);
                    response = response.substring(0,response.length() - 2);
                    changeclass(response);

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.

                public void onErrorResponse(VolleyError error) {
                    //Codice da eseguire in caso di errore
                    loginButton.setClickable(true);
                    Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("email", password);
                    MyData.put("password", password);
                    return MyData;
                }
            };


            MyRequestQueue.add(MyStringRequest);
        }

            public void changeclass(final String response){
                Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
                myIntent.putExtra("response", response); //Optional parameters
                myIntent.putExtra("user", user); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }


    });
    }

    //Metodo per notifiche, giornaliero e alle ore 9
    public void myAlarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }
    }

}

