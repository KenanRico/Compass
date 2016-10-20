package com.example.pc.compass;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class PinSelectionPage extends AppCompatActivity {

    private Database db = new Database(this,null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_selection_page);
    }

    public DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i){
                case DialogInterface.BUTTON_POSITIVE: {
                    db.update_list_status(db.get_current_home(),"home","remove");
                    Toast.makeText(PinSelectionPage.this,"Successfully removed current home",Toast.LENGTH_SHORT).show();
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:{
                    break;
                }
            }
        }
    };


    public void location_list(View v){
        Intent i = new Intent(this, ListFromHomeDest.class);
        i.putExtra("previous","home");
        startActivity(i);
    }

    public void back(View v){
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}
