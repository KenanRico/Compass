package com.example.pc.compass;

import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by PC on 9/7/2016.
 */
public class Location extends Application {

    private String name;
    private String address;
    private double lat;
    private double lng;

    public Location(String na, String ad, double lat, double lng){
        this.name = na;
        this.address = ad;
        this.lat = lat;
        this.lng = lng;
        //Address to latlng
    }

    public void setName(String na){
        this.name = na;
    }
    public void setAddress (String ad){
        this.address = ad;
    }

    public String getName(){
        return this.name;
    }
    public String getAddress(){
        return this.address;
    }
    public double getLat(){
        return this.lat;
    }
    public double getLng(){
        return this.lng;
    }

}
