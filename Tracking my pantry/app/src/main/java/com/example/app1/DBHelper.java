package com.example.app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//CLasse per gestione database relativo a prodotti
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper( Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Userdetails(id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "description TEXT, " +
                "insertDate TEXT, " +
                "user TEXT, " +
                "type TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Userdetails");
    }

    public Boolean insertuserdata(String name, String description, String insertDate,
     String user, String type){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String id = null;
        contentValues.put("id",id);
        contentValues.put("name",name);
        contentValues.put("description",description);
        contentValues.put("insertDate",insertDate);
        contentValues.put("user",user);
        contentValues.put("type",type);

        long result = DB.insert("Userdetails", null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean deletedata(String id){
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Userdetails where id = ?", new String []{id});

        if(cursor.getCount()>0){
            long result = DB.delete("Userdetails", "id=?", new String[]{id});
            if(result == -1){
                return false;
            }else{
                 return false;
            }
        }else{
            return true;
        }
    }

    public Cursor getdata(String id) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Userdetails where user = ?", new String []{id});

        return cursor;
    }

    //Metodo che filtra a seconda del parametro scelto dall'utente
    public Cursor filterdata(String id, String type) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Userdetails where user = ? and type = ?", new String []{id, type});

        return cursor;
    }
}
