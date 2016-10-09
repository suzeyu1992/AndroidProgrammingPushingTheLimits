package com.szysky.note.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SERVICE_NAME = "测试服务";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 发送定时log达到轮询效果
//        setRepeatSend();

        // 设置本地一个wifi服务
//        announceService();

        // 发现本地网络的wifi服务
//        discoverService();



    }


    public void initWifiDirect(){
        IntentFilter intentFilter = new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "接收到wifi-direct的变化" );
            }
        };

        registerReceiver(myReceiver, intentFilter);


    }
    /**
     *  在设备进行发布服务, 并初始化 Wi-Fi Direct渠道
     */
    private void announceWiFiDirectService(){
        WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        final Looper mWFDLooper = handlerThread.getLooper();
        WifiP2pManager.Channel mChannel = wifiP2pManager.initialize(getApplicationContext(), mWFDLooper, new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.e(TAG, "通道回调断开");
                mWFDLooper.quit();
            }
        });

        HashMap<String, String> txtRecords = new HashMap<>();
        WifiP2pDnsSdServiceInfo mServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_NAME, "_http._tcp", txtRecords);

        wifiP2pManager.addLocalService(mChannel, mServiceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Service announcing !");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Service announcing failed : "+reason);
            }
        });


    }



    /**
     * 设置定时轮序检测功能
     */
    public void setRepeatSend(){

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
//        long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long interval = 1000 * 3;
        long start = System.currentTimeMillis() ;

        Intent pollIntent = new Intent("com.heiheihei");
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, pollIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, start, interval, pendingIntent);

    }

    /**
     *  声明设备中标准Wi-Fi的服务
     */
    private void announceService(){
        NsdManager nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
        nsdServiceInfo.setPort(8081);
        nsdServiceInfo.setServiceName("wifi服务哦");
        nsdServiceInfo.setServiceType("_http._tcp.");

        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "onRegistrationFailed: " );
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "onUnregistrationFailed: ");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "onServiceRegistered: " );
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "onServiceUnregistered: " );
            }
        });
    }


    /**
     *  发现一个服务
     */
    private void discoverService(){
        NsdManager nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "onStartDiscoveryFailed: ");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "onStopDiscoveryFailed: ");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "onDiscoveryStarted: ");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "onDiscoveryStopped: ");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "onServiceFound");
                NsdManager nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
                nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        Log.w(TAG, "主机: "+serviceInfo.getHost() +"      端口:"+serviceInfo.getPort() );
                    }
                });

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "onServiceLost: ");
            }
        });
    }

}
