package com.example.pc.compass;
import android.database.sqlite.*;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by PC on 9/7/2016.
 */

public class Database extends SQLiteOpenHelper{
    private static final int Version = 1;
    private static final String DBname = "location.db";
    private static final String TABLE_LOCATION = "locations";
    private static final String name_column = "names";
    private static final String address_column = "address";
    private static final String OnHomeList_column = "onHomeList";
    private static final String OnDestList_column = "onDestList";
    private static final String TABLE_LATLNG = "latlngs";
    private static final String lat_column = "lat";
    private static final String lng_column = "lng";

    public Database(Context context, SQLiteDatabase.CursorFactory factory){
        super(context, DBname, factory,Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "Create Table "+TABLE_LOCATION+" ("+name_column+" String primary key, "+address_column+" Varchar, "+OnHomeList_column+" Integer, "+OnDestList_column+" Integer);";
        String query2 = "Create Table "+TABLE_LATLNG+" ("+name_column+" String primary key, "+lat_column+" Double, "+lng_column+" Double);";
        db.execSQL(query1);
        db.execSQL(query2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try{
            String query1 = "drop table if exists " + TABLE_LOCATION + ";";
            String query2 = "drop table if exists " + TABLE_LATLNG + ";";
            db.execSQL(query1);
            db.execSQL(query2);
        }catch (Exception e){};
        onCreate(db);
    }

    public void refresh_database(){
        SQLiteDatabase db = getWritableDatabase();
        String query1 = "drop table if exists " + TABLE_LOCATION + ";";
        String query2 = "drop table if exists " + TABLE_LATLNG + ";";
        db.execSQL(query1);
        db.execSQL(query2);
    }

    //Insert, Delete, Update_List_Status, Get_List_Status, Get_Locations
    public void insert(Location location){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues loc_values = new ContentValues();
        loc_values.put(name_column, location.getName());
        loc_values.put(address_column, location.getAddress());
        loc_values.put(OnHomeList_column,false);
        loc_values.put(OnDestList_column,false);
        ContentValues latlng_values = new ContentValues();
        latlng_values.put(name_column,location.getName());
        latlng_values.put(lat_column,location.getLat());
        latlng_values.put(lng_column,location.getLng());
        db.insert(TABLE_LOCATION,null,loc_values);
        db.insert(TABLE_LATLNG,null,latlng_values);
        db.close();
    }
    public void delete(String loc_name){
        SQLiteDatabase db = getWritableDatabase();
        String query = ("delete from "+TABLE_LOCATION+" where "+name_column+" = "+"\""+loc_name+"\";");
        db.execSQL(query);
    }
    public void update(String current_name, Location new_info){
        SQLiteDatabase db = getWritableDatabase();
        String query1 = ("Update "+TABLE_LOCATION+" set "+name_column+" = \'"+new_info.getName()+"\', "+address_column+" = \'"+new_info.getAddress()+"\'"+" where "+name_column+" = \'"+current_name+"\'");
        String query2 = ("Update "+TABLE_LATLNG+" set "+name_column+" = \'"+new_info.getName()+"\', "+lat_column+" = "+new_info.getLat()+", "+lng_column+" = "+new_info.getLng()+" where "+name_column+" = \'"+current_name+"\'");
        db.execSQL(query1);
        db.execSQL(query2);
    }
    public String get_current_home(){
        String name;
        SQLiteDatabase db = getWritableDatabase();
        String query = ("Select "+name_column+" from "+TABLE_LOCATION+" where "+OnHomeList_column+" = 1;");
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        try {
            name = c.getString(c.getColumnIndex(name_column));
        }catch(Exception e){name = "";}
        return name;
    }
    public String get_current_dest(){
        String name;
        SQLiteDatabase db = getWritableDatabase();
        String query = ("Select "+name_column+" from "+TABLE_LOCATION+" where "+OnDestList_column+" = 1;");
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        try {
            name = c.getString(c.getColumnIndex(name_column));
        }catch(Exception e){name = "";}
        return name;
    }
    public LatLng get_latlng(String name){
        SQLiteDatabase db = getWritableDatabase();
        double lat,lng;
        String query1 = "Select "+lat_column+" from "+TABLE_LATLNG+" where "+name_column+" = \'"+name+"\';";
        String query2 = "Select "+lng_column+" from "+TABLE_LATLNG+" where "+name_column+" = \'"+name+"\';";
        Cursor c1 = db.rawQuery(query1,null);
        Cursor c2 = db.rawQuery(query2,null);
        if(c1==null||c1.getCount()==0||c2==null||c2.getCount()==0) {
            return null;
        }
        c1.moveToFirst();
        lat = c1.getDouble(c1.getColumnIndex(lat_column));
        c2.moveToFirst();
        lng = c2.getDouble(c2.getColumnIndex(lng_column));
        LatLng result = new LatLng(lat,lng);
        return result;
    }
    public String get_address(String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "select "+address_column+" from "+TABLE_LOCATION+" where "+name_column+" = \'"+name+"\';";
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex(address_column));
        return result;
    }
    public void update_list_status(String loc_name, String list, String action){
        SQLiteDatabase db = getWritableDatabase();
        String query="";
        if(list.equals("home")) {
            if (action.equals("add")) {
                query = "Update " + TABLE_LOCATION + " set " + OnHomeList_column + " = " + "1 " + "where " + name_column + " = \"" + loc_name + "\";";
            }
            else if (action.equals("remove")) {
                query = "Update " + TABLE_LOCATION + " set " + OnHomeList_column + " = " + "0 " + "where " + name_column + " = \"" + loc_name + "\";";
            }
        }
        else if (list.equals("dest")){
            if (action.equals("add")) {
                query = "Update " + TABLE_LOCATION + " set " + OnDestList_column + " = " + "1 " + "where " + name_column + " = \"" + loc_name + "\";";
            }
            else if (action.equals("remove")) {
                query = "Update " + TABLE_LOCATION + " set " + OnDestList_column + " = " + "0 " + "where " + name_column + " = \"" + loc_name + "\";";
            }
        }
        else{
            //Do nothing
        }
        db.execSQL(query);
    }
    public ArrayList<Integer> get_loc_status(String loc_name){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Integer> List_Status = new ArrayList<Integer>();
        String query1 = "Select "+OnHomeList_column+" from "+TABLE_LOCATION+" where "+name_column+" = \'"+loc_name+"\';";
        String query2 = "Select "+OnDestList_column+" from "+TABLE_LOCATION+" where "+name_column+" = \'"+loc_name+"\';";
        Cursor c1 = db.rawQuery(query1,null);
        c1.moveToFirst();
        List_Status.add(c1.getInt(c1.getColumnIndex(OnHomeList_column)));
        Cursor c2 = db.rawQuery(query2,null);
        c2.moveToFirst();
        List_Status.add(c2.getInt(c2.getColumnIndex(OnDestList_column)));
        return List_Status;
    }
    public ArrayList<String> get_locations(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> location_list = new ArrayList<String>();
        String query = "Select "+name_column+" from "+TABLE_LOCATION;
        boolean exception_occurence = false;
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            try {
                if(c.getString(c.getColumnIndex(name_column))!=null){
                    location_list.add(c.getString(c.getColumnIndex(name_column)));
                }
            } catch (Exception e) {
                exception_occurence = true;
                break;
            }
            c.moveToNext();
        }
        if(exception_occurence){
            location_list.add("There has been an error!");
        }
        db.close();
        return location_list;
    }
}
