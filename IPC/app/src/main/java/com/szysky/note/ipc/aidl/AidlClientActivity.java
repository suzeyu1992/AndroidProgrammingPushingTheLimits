package com.szysky.note.ipc.aidl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.szysky.note.ipc.R;

import java.util.Date;

/**
 *  aidl 客户端实现代码
 */
public class AidlClientActivity extends AppCompatActivity implements ServiceConnection{

    private static final String TAG = AidlClientActivity.class.getSimpleName();
    /**
     * 绑定的远程服务
     */
    private IMyApiInterfaceV1 mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_client);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getApplicationContext(), AidlService.class), this, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    /**
     *  设置点击事件
     */
    public void backAdd(View view){
        Log.e(TAG, "backAdd: 点击触发" );
        CustomData customData = new CustomData(new Date());
        try {
            mService.storeData(customData);
        } catch (RemoteException e) {
            Log.e(TAG, "backAdd: 调用远程方法add发生了异常" );
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = IMyApiInterfaceV1.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }
}
