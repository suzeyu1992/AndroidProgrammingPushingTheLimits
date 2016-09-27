package com.szysky.note.multitouch;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * 绑定的远程服务的实例
     */
    private LocalService mService;
    private ServiceConnection serviceConnection;


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

    public void onButtonRotate(View view){
        startActivity(new Intent(getApplicationContext(), RotateActivity.class));
    }

    public void onButtonBindService(View view){
        if (mService != null){
            mService.doBackgroundOperation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), LocalService.class);
        serviceConnection = new ServiceConnection() {


            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((LocalService.LocalBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;

            }
        };
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mService != null){
            unbindService(serviceConnection);
        }
    }
}
