package com.example.app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//CLasse per gestione database relativo a location
public class DBLocation extends SQLiteOpenHelper {
    public DBLocation( Context context) {
        super(context, "Userlocation.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Userlocation(id INTEGER PRIMARY KEY, " +
                "user TEXT, " +
                "location TEXT, " +
                "product TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Userlocation");
    }

    public Boolean insertlocationdata(String user, String location, String product){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String id = null;
        contentValues.put("id",id);
        contentValues.put("user",user);
        contentValues.put("location",location);
        contentValues.put("product",product);

        long result = DB.insert("Userlocation", null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean deletedata(String id){
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Userlocation where id = ?", new String []{id});

        if(cursor.getCount()>0){
            long result = DB.delete("Userlocation", "id=?", new String[]{id});
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

        Cursor cursor = DB.rawQuery("Select * from Userlocation where user = ?", new String []{id});

        return cursor;
    }
}
