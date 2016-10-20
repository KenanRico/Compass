package com.example.pc.compass;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class EditLocationItem extends AppCompatActivity {

    private Database db = new Database(this,null);
    private EditText name_textfield;
    private EditText address_textfield;
    private TextView toptext_2;
    private String Toast_Message;
    String name,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location_item);

        Bundle b = getIntent().getExtras();
        name = b.getString("loc_name");
        address = b.getString("loc_address");

        name_textfield = (EditText)findViewById(R.id.name_textfield);
        address_textfield = (EditText)findViewById(R.id.address_textfield);
        toptext_2 = (TextView)findViewById(R.id.toptext);
        name_textfield.setText(name);
        address_textfield.setText(address);

        String text = name;
        toptext_2.setText(text);
    }


    public void save(View v){
        String new_name = name_textfield.getText().toString();
        String new_address = address_textfield.getText().toString();
        boolean getLocation_successful;
        double latitude = 0.0;
        double longitude = 0.0;
        try{
            latitude = getLocationfromAddress(this,new_address).latitude;
            longitude = getLocationfromAddress(this,new_address).longitude;
            getLocation_successful = true;
        }catch (Exception e){
            getLocation_successful = false;
            Toast_Message = ("Error translating address to location. Make sure your device is connected to the internet or try to rephrase your address.");
            Toast.makeText(EditLocationItem.this, Toast_Message, Toast.LENGTH_LONG).show();
        }
        if(getLocation_successful) {
            Location location = new Location(new_name, new_address, latitude, longitude);
            db.update(name, location);

            Intent i = new Intent(this, LocationList.class);
            startActivity(i);
        }
    }

    public void cancel(View v){
        Intent i = new Intent(this,LocationList.class);
        startActivity(i);
    }

    public void delete(View v){
        db.delete(name);

        Intent i = new Intent(this,LocationList.class);
        startActivity(i);
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
