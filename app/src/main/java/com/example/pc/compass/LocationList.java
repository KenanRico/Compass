package com.example.pc.compass;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.widget.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.widget.Toast;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class LocationList extends AppCompatActivity {

    private Database db = new Database(this,null);
    private ArrayList<String> locations = new ArrayList<String>();
    private String action;
    private String box_argument_location;
    private String dialogbox_content;
    private String location_name;
    private String Toast_Message;

    private TextView home_text;
    private TextView dest_text;
    private EditText addLocation;
    private EditText addAddress;

    private Thread t = new Thread();
    private boolean stop_thread = false;

    //private My_handler handler = new My_handler(null,"");

    Handler h1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            home_text.setText("");
        }
    };
    Handler h2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dest_text.setText("");
        }
    };
    Handler h3 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dest_text.setText(db.get_current_home());
        }
    };
    Handler h4 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dest_text.setText(db.get_current_dest());
        }
    };
    Handler h5 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(LocationList.this,Toast_Message,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        home_text = (TextView)findViewById(R.id.home_loc);
        dest_text = (TextView)findViewById(R.id.dest_loc);
        home_text.setText(db.get_current_home());
        dest_text.setText(db.get_current_dest());
        addLocation = (EditText)findViewById(R.id.addLocation);
        addAddress = (EditText)findViewById(R.id.addAddress);

        action = "none";
        dialogbox_content = "";

        //Bundle previous_activity = getIntent().getExtras();
        //final String previous = previous_activity.getString("previous");

        WaitForHomeDestInput(t);

        locations = db.get_locations();
        ListAdapter locAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,locations);
        final ListView location_list = (ListView)findViewById(R.id.locations);
        location_list.setAdapter(locAdapter);
        final Intent edit_location_page = new Intent(this,EditLocationItem.class);
        location_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        location_name = String.valueOf(adapterView.getItemAtPosition(i));
                            edit_location_page.putExtra("loc_name",location_name);
                            edit_location_page.putExtra("loc_address",db.get_address(location_name));
                            startActivity(edit_location_page);

                    }
                }
        );
    }

    public void WaitForHomeDestInput(Thread t){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(true){
                    while(action.equals("none")){
                        if(stop_thread==true)
                        break;
                    }
                    if(stop_thread==true)
                        break;
                    if(action.equals("reset")){
                        db.update_list_status(db.get_current_home(),"home", "remove");
                        db.update_list_status(db.get_current_dest(),"dest", "remove");
                        /*handler.setBoth(home_text,"none");
                        handler.getHandler();*/
                        h1.sendEmptyMessage(0);
                        /*handler.setBoth(dest_text,"none");
                        handler.getHandler();*/
                        h2.sendEmptyMessage(0);
                        Toast_Message = "Successfully removed all pin on the map!";
                        //Toast.makeText(LocationList.this,Toast_Message,Toast.LENGTH_SHORT).show();
                        h5.sendEmptyMessage(0);
                    }
                    action = "none";
                    location_name = "";
                }
            }
        };
        t = new Thread(r);
        t.start();
    }

    public DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i){
                case DialogInterface.BUTTON_POSITIVE: {
                    action = dialogbox_content;
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:{
                    break;
                }
            }
        }
    };

    public void reset(View v){
        dialogbox_content = "reset";
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("Are you sure you want to reset all pins on the map?").setPositiveButton("Yes",dialogBox).setNegativeButton("No",dialogBox).show();
    }
    public void map(View v){
        stop_thread = true;
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }
    public void add_location(View v){
        stop_thread = true;
        String location = addLocation.getText().toString();
        String address = addAddress.getText().toString();

        boolean getLocation_successful;
        double latitude=0.0;
        double longitude=0.0;
        try {
            latitude = getLocationfromAddress(this, address).latitude;
            longitude = getLocationfromAddress(this, address).longitude;
            getLocation_successful = true;
        }catch(Exception e){
            getLocation_successful = false;
            Toast_Message = ("Error translating address to location. Make sure your device is connected to the internet or try to rephrase your address.");
            Toast.makeText(LocationList.this, Toast_Message, Toast.LENGTH_LONG).show();
        }

        if(getLocation_successful) {
            Location new_loc = new Location(location, address, latitude, longitude);
            db.insert(new_loc);
            Toast_Message = ("Added " + location + " to the list!");
            Toast.makeText(LocationList.this, Toast_Message, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, LocationList.class);
            startActivity(i);
        }
    }

    public LatLng getLocationfromAddress(Context context, String strAddress){
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng point = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            android.location.Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            point = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return point;
    }

}
