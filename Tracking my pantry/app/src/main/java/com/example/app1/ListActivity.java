package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import org.json.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Activity per creazione list dei prodotti in database remoto
public class ListActivity extends AppCompatActivity {

    //Costruisco classe con Item, prodotto
    public class Item implements Serializable {
        public Item(String id, String name, String description, String image) {
            this.id = id;
            this.name = name;
            this.description=description;
            this.image = image;
        }
        private  String name;
        private  String description;
        private  String id;
        private String image;

        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }
        public String getId() {
            return id;
        }
        public String getImage() {
            return image;
        }
    }


    Button newProductButton;
    ListView productList;
    String jsonString;
    ArrayList<String> arrayList = new ArrayList<>();

    //Lista oggetti da passare per eventuale creazione di oggetto
    String token;
    String barcode;
    String auth;
    String user;
    List<Item> itemArray = new ArrayList<Item>();

    @Override
    protected void onResume() {
        super.onResume();
        newProductButton.setClickable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        newProductButton = findViewById(R.id.newProductButton);

        Bundle b = getIntent().getExtras();
        jsonString   = b.getString("response");
        barcode   = b.getString("barcode");
        auth   = b.getString("auth");
        user   = b.getString("user");

        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
            JSONArray arr = obj.getJSONArray("products");
            token = obj.getString("token");
            if(arr.length()==0){
            }
            //Inizio a scorrere i prodotti
            for (int i = 0; i < arr.length(); i++)
            {
                String id = arr.getJSONObject(i).getString("id");
                String name = arr.getJSONObject(i).getString("name");
                String description =  arr.getJSONObject(i).getString("description");
                String image;

                //Nel caso in cui il prodootto non abbia una immagine dichiarata, ne viene posta una predefinita
                if( arr.getJSONObject(i).isNull("img")){
                    image = "https://i.pinimg.com/originals/bf/1a/42/bf1a42cbe1f152ba4ce1f4734730605d.png";
                }else{
                    image =  arr.getJSONObject(i).getString("img");
                }

                Item item = new Item(id, name, description, image);

                itemArray.add(item);
                String union = name+": "+description;
                arrayList.add(union);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SearchView simpleSearchView = (SearchView) findViewById(R.id.simpleSearchView);
        productList = findViewById(R.id.productList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        productList.setAdapter(arrayAdapter);

        //Questa parte di codice serve per implementare la searchBar
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                arrayAdapter.getFilter().filter(query);

                if(arrayAdapter.getCount() == 0){
                    Toast.makeText(getApplicationContext(), "Non ci sono prodotti!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //Qui vado a creare onItemClickListener, serve per salvarmi le caratteristiche del prodotto e aprire l'activity col prodotto singolo
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Item toSend = new Item(itemArray.get(position).getId(),itemArray.get(position).getName(),itemArray.get(position).getDescription(),itemArray.get(position).getImage());
                Intent myIntent = new Intent(ListActivity.this, SingleProductActivity.class);
                myIntent.putExtra("token", token);
                myIntent.putExtra("auth", auth);
                myIntent.putExtra("id", toSend.getId());
                myIntent.putExtra("name", toSend.getName());
                myIntent.putExtra("description", toSend.getDescription());
                myIntent.putExtra("image", toSend.getImage());
                myIntent.putExtra("user", user);
                ListActivity.this.startActivity(myIntent);
                finish();
            }
        });

        newProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ListActivity.this, NewProductActivity.class);
                myIntent.putExtra("token", token); //Optional parameters
                myIntent.putExtra("barcode", barcode);
                myIntent.putExtra("auth", auth);
                ListActivity.this.startActivity(myIntent);
                finish();
            }
        });


    }
}