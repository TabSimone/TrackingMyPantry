package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import java.util.HashMap;
import java.util.Map;

//In questa acitivity viene performata l'opzione di registratsi da parte dell'utente
public class RegisterActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    EditText username;
    Button registerButton;

    @Override
    protected void onResume()
    {
        super.onResume();
        registerButton.setClickable(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emailRegister);
        password = findViewById(R.id.passwordRegister);
        username = findViewById(R.id.usernameRegister);
        registerButton = findViewById(R.id.activeRegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registerButton.setClickable(false);
                //Elenco di controlli, per campi vuoti, mail senza chioccola e password troppo corta
                if(TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(username.getText())){
                    Toast.makeText(getApplicationContext(), "Non hai inserito campi", Toast.LENGTH_SHORT).show();
                    registerButton.setClickable(true);
                }else if (!email.getText().toString().contains("@")) {
                    Toast.makeText(getApplicationContext(), "Non hai inserito mail", Toast.LENGTH_SHORT).show();
                    registerButton.setClickable(true);
                } else if(password.getText().toString().length()<8) {
                    Toast.makeText(getApplicationContext(), "Password troppo corta", Toast.LENGTH_SHORT).show();
                    registerButton.setClickable(true);
                }else {
                    //Viene chiamato il metodo che esegue chiamata Volley, vengono passati i valori inseriti nei campi
                    REGISTER(username.getText().toString(), email.getText().toString(), password.getText().toString());
                }
            }

            public void REGISTER(final String username, final String email, final String password){
                RequestQueue MyRequestQueue = Volley.newRequestQueue(RegisterActivity.this);
                String url = "https://lam21.iot-prism-lab.cs.unibo.it/users";
                StringRequest MyStringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        //Il codice viene eseguito nel caso la regitrazione sia avvenuta con successo
                        //La stringa response, contiene la risposta del server
                        Toast.makeText(getApplicationContext(), "Registrazione avvenuta con successo", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Renidirizzazione..", Toast.LENGTH_SHORT).show();

                        //Qui si viene reindirizzati alla MainActivity, ho usato thread.sleep per permettere di mostrare i due toast sopra
                        final Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2500);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();

                    }
                }, new Response.ErrorListener() {

                    public void onErrorResponse(VolleyError error) {
                        //Codice eseguito nel caso di errore
                        registerButton.setClickable(true);
                        Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("username", username);
                        MyData.put("password", password);
                        MyData.put("email", password);
                        return MyData;
                    }
                };

                MyRequestQueue.add(MyStringRequest);
            }

        });
    }

}