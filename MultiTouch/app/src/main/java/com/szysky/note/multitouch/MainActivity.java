package com.szysky.note.multitouch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     *  多点触控跳转
     */
    public void onButtonMultiTouch(View view){
        Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MultiTouchActivity.class));
    }
}
