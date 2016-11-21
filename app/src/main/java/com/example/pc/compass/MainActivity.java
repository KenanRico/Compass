package com.example.pc.compass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public ImageView compass;
    public float direction_degree = 0f;
    public SensorManager sensorManager;

    private Database db = new Database(this,null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (ImageView)findViewById(R.id.compass);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //db
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        String dir = Float.toString(degree)+" degrees";
        RotateAnimation ra = new RotateAnimation(direction_degree,-degree,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(100);
        ra.setFillAfter(true);
        compass.startAnimation(ra);
        direction_degree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not implemented
    }

    public void map(View v){
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }
    public void compass(View v){
        //do nothing
    }
    public void list(View v){
        Intent i = new Intent(this,LocationList.class);
        startActivity(i);
    }
}
