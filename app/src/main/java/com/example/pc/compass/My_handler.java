package com.example.pc.compass;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.*;

/**
 * Created by PC on 9/13/2016.
 */
public class My_handler{
    private TextView textView;
    private String text;
    private Handler handler;

    public My_handler(TextView tv, String t){
        this.textView = tv;
        this.text = t;
    }
    public void setBoth(TextView tv, String t){
        this.textView = tv;
        this.text = t;
    }
    public void getHandler(){
         this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                textView.setText(text);
            }
        };
        handler.sendEmptyMessage(0);
    }
}
