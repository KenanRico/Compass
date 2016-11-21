package com.example.pc.compass;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private TextView error;
    private EditText search_bar;
    private Database db = new Database(this, null);
    private static final int permission_result = 1;
    private LatLng home;
    private LatLng destination;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private String Toast_Message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        error = (TextView) findViewById(R.id.error);
        search_bar = (EditText) findViewById(R.id.search_bar);

    }

       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                                                .addConnectionCallbacks(this)
                                                .addOnConnectionFailedListener(this)
                                                .addApi(LocationServices.API)
                                                .build();*/

    /*@Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case permission_result: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        display_home_dest();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //error.setText("Permission (My Location) not granted. Please go to Menu->Setting->Apps->Compass->Permission to enable this feature.");
            //error.setTextColor(getResources().getColor(R.color.colorAccent));
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, permission_result);
        }
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are here")));
            }
        });

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int counter = 0;
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
            counter++;
        }
        if (counter > 0) {
            LatLngBounds bounds = builder.build();
            int padding = 300; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cu);
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                saveMarker_onClick(point);
            }
        });
    }


    public void display_home_dest() {
        String home_string = db.get_current_home();
        String dest_string = db.get_current_dest();
        String home_pin_text = "Home: " + home_string;
        String dest_pin_text = "Destination: " + dest_string;


        try {
            //home = getLocationfromAddress(context, db.get_address(db.get_current_home()));
            home = db.get_latlng(db.get_current_home());
            //Toast.makeText(MapsActivity.this,home.toString(),Toast.LENGTH_SHORT).show();
            if (home != null)
                markers.add(mMap.addMarker(new MarkerOptions().position(home).title(home_pin_text)));
        } catch (Exception e) {
            String msg = "Error loading pin for home: " + db.get_current_home();
            Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
        try {
            //destination = getLocationfromAddress(context, db.get_address(db.get_current_dest()));
            destination = db.get_latlng(db.get_current_dest());
            //Toast.makeText(MapsActivity.this,db.get_address(db.get_current_dest()),Toast.LENGTH_SHORT).show();
            if (destination != null)
                markers.add(mMap.addMarker(new MarkerOptions().position(destination).title(dest_pin_text)));
        } catch (Exception e) {
            String msg = "Error loading pin for destination: " + db.get_current_dest();
            Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    public void map(View v) {
        //does nothing
    }

    public void compass(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void list(View v) {
        Intent i = new Intent(this, LocationList.class);
        i.putExtra("previous", "map");
        startActivity(i);
    }

    public void search_pin(View v) {
        String searched_loc = search_bar.getText().toString();
        String cap_searched_loc = searched_loc.toUpperCase();
        ArrayList<String> all_locations = db.get_locations();
        ArrayList<String> cap_all_locations = new ArrayList<String>();
        for (String x : all_locations) {
            cap_all_locations.add(x.toUpperCase());
        }
        if (cap_all_locations.contains(cap_searched_loc)) {
            int index = cap_all_locations.indexOf(cap_searched_loc);
            String db_searched_loc = all_locations.get(index);
            LatLng current_latlng = db.get_latlng(db_searched_loc);
            boolean onMap = false;
            for (Marker x : markers) {
                if (x.getTitle().equalsIgnoreCase(db_searched_loc)) {
                    onMap = true;
                }
            }
            if (onMap != true) {
                markers.add(mMap.addMarker(new MarkerOptions().position(current_latlng).title(db_searched_loc)));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_latlng, 13));

        } else {
            Toast_Message = ("Cannot find this location.");
            Toast.makeText(MapsActivity.this, Toast_Message, Toast.LENGTH_LONG).show();
        }
    }

    public void saveMarker_onClick(LatLng latlng){
        markers.add(mMap.addMarker(new MarkerOptions().position(latlng).title("")));

    }











    /* public DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
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
    }*/

}
