package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Activity che gestice le location
public class LocationActivity extends AppCompatActivity {

    //Creo classe con oggetto location
    public class MyLocation implements Serializable {
        public MyLocation(String id, String location, String product) {
            this.id=id;
            this.location = location;
            this.product=product;
        }
        private  String id;
        private  String location;
        private  String product;

        public String getId() {
            return id;
        }
        public String getLocation() {
            return location;
        }
        public String getProduct() {
            return product;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportButton.setClickable(true);
    }

    Button reportButton;

    String user;

    //Per ottenere i vari metodi relativi al database
    DBLocation DB = new DBLocation(LocationActivity.this);

    ListView locationList;
    ArrayList<String> arrayList = new ArrayList<>();
    List<LocationActivity.MyLocation> locationArray = new ArrayList<LocationActivity.MyLocation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        reportButton = findViewById(R.id.reportButton);

        Bundle b = getIntent().getExtras();
        user = b.getString("user");

        //Mostro tutte le location
        Cursor res = DB.getdata(user);
        showData(res);

        reportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                reportButton.setClickable(false);
                final AlertDialog.Builder alert = new AlertDialog.Builder(LocationActivity.this);

                //Qui creo alert con due input, il prodotto e la location, e poi viene creato
                LinearLayout lila1= new LinearLayout(LocationActivity.this);
                lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation
                final EditText product = new EditText(LocationActivity.this);
                product.setHint("Nome prodotto...");//optional
                final EditText location = new EditText(LocationActivity.this);
                location.setHint("Location...");
                lila1.addView(product);
                lila1.addView(location);
                alert.setView(lila1);
                alert.setTitle("Inserimento location");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String productValue = product.getText().toString();
                        String locationValue = location.getText().toString();
                        if(productValue=="" || locationValue ==""){
                            Toast.makeText(getApplicationContext(), "Errori nei campi", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            reportButton.setClickable(true);
                        }else{
                            Boolean checkinsertdata = DB.insertlocationdata(user, locationValue, productValue);

                            //Controllo che inserimento sia andato a buon fine
                            if(checkinsertdata == true){
                                Toast.makeText(getApplicationContext(), "Inserimento avvenuto con successo", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }else{
                                Toast.makeText(getApplicationContext(), "Inserimento non avvenuto", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }                     });
                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                reportButton.setClickable(true);
                            }     });

                alert.create();
                alert.show();

            }
        });
    }

    //Metodo per popolare listview
    private void showData(Cursor res){
        arrayList.clear();
        if(res.getCount()==0){
            Toast.makeText(getApplicationContext(), "Non ci sono location", Toast.LENGTH_SHORT).show();
        }

        while(res.moveToNext()) {
            String id = res.getString(0);
            String location = res.getString(2);
            String product =res.getString(3);
            String union = "Prodotto: "+product+", "+location+".";
            arrayList.add(union);

            LocationActivity.MyLocation mylocation = new LocationActivity.MyLocation(id, location, product);
            locationArray.add(mylocation);
        }

        locationList = findViewById(R.id.locationList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        locationList.setAdapter(arrayAdapter);

        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Le seguenti linee di codice servono per aprire maps
                String str_location = locationArray.get(position).getLocation();

                String map = "http://maps.google.co.in/maps?q=" + str_location;

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
                finish();
            }
        });

        //In caso di press prolungato dell'elemento do la possibilità all'utente di eliminare la location
        locationList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                // TODO Auto-generated method stub
                String clickedLocation=(String) locationList.getItemAtPosition(position);
                String toDelete = locationArray.get(position).getId();

                androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(LocationActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Vuoi davvero eliminare la location?");
                alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DB.deletedata(toDelete);
                                dialog.dismiss();
                                //Next commands are needed to refresh activity when a product is deleted
                                finish();
                                startActivity(getIntent());
                            }
                        });
                alertDialog.show();
                return true;
            }
        });
    }
}