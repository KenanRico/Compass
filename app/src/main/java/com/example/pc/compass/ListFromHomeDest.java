package com.example.pc.compass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListFromHomeDest extends AppCompatActivity {

    private Database db = new Database(this,null);
    private ArrayList<String> locations = new ArrayList<String>();
    private String action;
    private String location_name;
    private String Toast_Message;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(ListFromHomeDest.this,Toast_Message,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_from_homedest);


        action = "none";

        locations = db.get_locations();
        ListAdapter locAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,locations);
        final ListView location_list = (ListView)findViewById(R.id.locations);
        location_list.setAdapter(locAdapter);

        Thread t = new Thread();
        WaitForHomeDestInput(t);

        location_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        location_name = String.valueOf(adapterView.getItemAtPosition(i));

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage("Do you want to set this location as home or destination?").setPositiveButton("Home", dialogBox).setNegativeButton("Destination", dialogBox).setNeutralButton("Cancel",dialogBox).show();

                    }
                }
        );
    }

    public DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i){
                case DialogInterface.BUTTON_POSITIVE: {
                    action = "home";
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:{
                    action = "dest";
                    break;
                }
                case DialogInterface.BUTTON_NEUTRAL:{
                    break;
                }
            }
        }
    };

    public void WaitForHomeDestInput(Thread t){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while(true){
                    while(action.equals("none")){;}
                        if(action.equals("home")) {
                            db.update_list_status(db.get_current_home(), "home", "remove");
                            db.update_list_status(location_name, "home", "add");
                            Toast_Message = "Successfully set " + location_name + " as home!";
                            handler.sendEmptyMessage(0);
                        }
                    else if (action.equals("dest")){
                            db.update_list_status(db.get_current_dest(), "dest", "remove");
                            db.update_list_status(location_name, "dest", "add");
                            Toast_Message = "Successfully set " + location_name + " as destination!";
                            handler.sendEmptyMessage(0);
                        }

                    action = "none";
                    location_name = "";
                }
            }
        };
        t = new Thread(r);
        t.start();
    }

    public void map(View v){
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }
}
