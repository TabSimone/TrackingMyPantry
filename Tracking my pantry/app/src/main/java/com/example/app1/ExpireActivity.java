package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//Activity per gestire le scadenze
public class ExpireActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextView pickedDateText;
    Button addExpireDateButton;
    EditText productInput;

    String user;
    String productDescription;
    String title;
    int insertedYear;
    int insertedMonth;
    int insertedDayOfMonth;

    @Override
    protected void onResume() {
        super.onResume();
        addExpireDateButton.setClickable(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expire);

        Bundle b = getIntent().getExtras();
        user = b.getString("user");

        pickedDateText = findViewById(R.id.pickedDateText);
        addExpireDateButton = findViewById(R.id.addExpireDateButton);
        productInput = findViewById(R.id.productInput);

        //Vado a creare datepickerdialog per leggere le date dell'utente
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();


        addExpireDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpireDateButton.setClickable(false);

                productDescription = productInput.getText().toString();
                title = "Scadenza prodotto per "+user;
                if(TextUtils.isEmpty(productInput.getText())){
                    Toast.makeText(getApplicationContext(), "Non hai inserito prodotto con scadenza", Toast.LENGTH_SHORT).show();
                    addExpireDateButton.setClickable(true);
                }else{
                    insertDate(insertedYear, insertedMonth, insertedDayOfMonth);
                }
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        insertedYear = year;
        insertedMonth = month;
        insertedDayOfMonth = dayOfMonth;

        SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");

        Calendar dateNow = Calendar.getInstance();
        Calendar dateExpire = Calendar.getInstance();

        dateExpire.set(insertedYear, insertedMonth, insertedDayOfMonth);

        if(dateNow.after(dateExpire)){
            Toast.makeText(getApplicationContext(), "Non puoi inserire vecchie date", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }

        pickedDateText.setText(Integer.toString(dayOfMonth)+" "+Integer.toString(month)+" "+Integer.toString(year));
    }

    //Metodo per aprire il calendario con settati i vari parametri
    public void insertDate(int insertedYear, int insertedMonth, int insertedDayOfMonth){
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(insertedYear, insertedMonth, insertedDayOfMonth, 7, 30);

        Calendar endTime = Calendar.getInstance();
        endTime.set(insertedYear, insertedMonth, insertedDayOfMonth, 8, 30);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, productDescription)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(CalendarContract.Events.DURATION,  "PT1H");

        startActivity(intent);
        finish();
    }
}