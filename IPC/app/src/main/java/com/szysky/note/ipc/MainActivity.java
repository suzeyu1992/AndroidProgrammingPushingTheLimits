package com.szysky.note.ipc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.szysky.note.ipc.aidl.AidlClientActivity;
import com.szysky.note.ipc.messenger.MessengerClientActivity;

import broadcast.ChargerConnectedActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_aidl).setOnClickListener(this);
        findViewById(R.id.btn_msg).setOnClickListener(this);
        findViewById(R.id.btn_broadcast).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_aidl:
                startActivity(new Intent(getApplicationContext(), AidlClientActivity.class));
                break;

            case R.id.btn_msg:
                startActivity(new Intent(getApplicationContext(), MessengerClientActivity.class));
                break;

            case R.id.btn_broadcast:
                startActivity(new Intent(getApplicationContext(), ChargerConnectedActivity.class));
                break;
        }

    }
}
