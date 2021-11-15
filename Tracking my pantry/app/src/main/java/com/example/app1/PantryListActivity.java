package com.example.app1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PantryListActivity<string> extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    //Qui dichiaro la classe con oggetto MyProduct, con relativi metodi e costruttore
    public class MyProduct implements Serializable {
        public MyProduct(String id, String name, String description, String date) {
            this.id=id;
            this.name = name;
            this.description=description;
            this.date=date;
        }
        private  String id;
        private  String name;
        private  String description;
        private  String date;

        public String getId() { return id; }
        public String getName() {
            return name;
        }
        public String getDescription() { return description; }
        public String getDate() { return date; }
    }

    String user;

    DBHelper DB = new DBHelper(PantryListActivity.this);

    Button reportButton;
    Button carbButton;
    Button proteinButton;
    Button fatButton;
    Button beverageButton;
    TextView title;
    ListView productList;

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> oldProductList = new ArrayList<>();
    List<PantryListActivity.MyProduct> productArray = new ArrayList<PantryListActivity.MyProduct>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        reportButton = findViewById(R.id.reportButton);
        title = findViewById(R.id.title);

        fatButton = findViewById(R.id.fatButton);
        proteinButton = findViewById(R.id.proteinButton);
        carbButton = findViewById(R.id.carbButton);
        beverageButton = findViewById(R.id.beverageButton);

        Bundle b = getIntent().getExtras();
        user = b.getString("user");

        //Con la creazione dell'activity vado a leggere i valori contenuti nel database, sfruttando la classe DBHelèer
        Cursor res = DB.getdata(user);
        showData(res);

        reportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                reportButton.setClickable(false);

                if(oldProductList.size()==0){
                    Toast.makeText(getApplicationContext(), "Non hai prodotti vecchi!", Toast.LENGTH_SHORT).show();
                }else{
                    writeToFile();
                }
            }
        });

        //I seguenti quattro metodi servono per filtrare i prodotti nel database a seconda di quanto richiesto dall'utente
        proteinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Cursor res = DB.filterdata(user, "Proteine");
                showData(res);
            }
        });

        beverageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Cursor res = DB.filterdata(user, "Bevande");
                showData(res);
            }
        });

        carbButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Cursor res = DB.filterdata(user, "Carboidrati");
                showData(res);
            }
        });

        fatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Cursor res = DB.filterdata(user, "Lipidi");
                showData(res);
            }
        });

    }

    //Il seguente metodo serve per creare un pdf con all'interno i prodotti più vecchi di 7 giorni
    private void writeToFile() {
        int pageHeight = 1120;
        int pagewidth = 792;

        PdfDocument pdfDocument = new PdfDocument();

        Paint title = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.black));

        canvas.drawText("User: "+user, 209, 80, title);
        canvas.drawText("Report vecchie entry", 209, 100, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(17);

        int axis = 140;

        title.setTextAlign(Paint.Align.LEFT);

        for(int i = 0; i<oldProductList.size(); i++){
            canvas.drawText(i+1 +". "+oldProductList.get(i), 100, axis, title);
            axis=axis+15;
        }

        pdfDocument.finishPage(myPage);

        String titleReport = "Report "+user+".pdf";
        File file = new File(Environment.getExternalStorageDirectory(), titleReport);

        //Check permission viene eseguito per permettere l'applicazione di creare il file
        try {
            if (checkPermission()) {
            } else {
                requestPermission();
            }
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(PantryListActivity.this, "PDF creato.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PantryListActivity.this, "Errore nella creazione pdf.", Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();

    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    //Il seguente metodo serve nel caso i permessi non siano stati concessi
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    //Metood per leggere e mostrare i dati nel database
    private void showData(Cursor res){
        arrayList.clear();
        if(res.getCount()==0){
            Toast.makeText(getApplicationContext(), "Non ci sono prodotti", Toast.LENGTH_SHORT).show();
        }

        //While va a creare un arraylist con la lista dei prodotti
        while(res.moveToNext()) {
            String id = res.getString(0);
            String name = res.getString(1);
            String description =res.getString(2);
            String date = res.getString(3);
            String union = "Prodotto: "+name+", "+description+".\nInserimento in data: "+date;
            arrayList.add(union);

            //Inoltre vengono aggiunti a un altro array i prodotti più vecchi di una settimana, per la creazione del pdf
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date insertDate = inputFormat.parse(date);
                Date todayDate = new Date();

                final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

                int diffInDays = (int) ((todayDate.getTime() - insertDate.getTime())/ DAY_IN_MILLIS );

                if(diffInDays>=7){
                    oldProductList.add("Prodotto: " +name+", "+description);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            PantryListActivity.MyProduct myproduct = new MyProduct(id, name, description, date);
            productArray.add(myproduct);
        }

        productList = findViewById(R.id.productList);

        //Viene quindi popolata la listview
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        productList.setAdapter(arrayAdapter);

        //Viene aggiunto un onitemclicklistener che permette di eliminare i prodotti consumati dall'utente
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String clickedProduct=(String) productList.getItemAtPosition(position);
                String toDelete = productArray.get(position).getId();

                AlertDialog alertDialog = new AlertDialog.Builder(PantryListActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Vuoi davvero eliminare il prodotto?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
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
            }
        });
    }

}