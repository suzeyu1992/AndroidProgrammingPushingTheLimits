package com.szysky.note.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SERVICE_NAME = "测试服务";
    private WifiP2pManager mServiceWifiP2pManager;
    private WifiP2pManager.Channel mServiceChannel;

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


        // 注册wifi-Direct 服务端的状态变化广播监听
        //initWifiDirect();
        // 设备发布服务 作为服务端
        //announceWiFiDirectService();

        discoverWiFiDirectService();



    }


    /**
     * 进行服务端广播注册, 当有客户端连接时处理回调
     */
    public void initWifiDirect(){
        IntentFilter intentFilter = new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "WIFI-Direct 广播回调", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "接收到wifi-direct的变化" );
                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action) && mServiceWifiP2pManager != null){
                    mServiceWifiP2pManager.requestConnectionInfo(mServiceChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            Log.d(TAG, "Group owner address: "+ info.groupOwnerAddress);
                            Log.d(TAG, "Am I group owner: " + info.isGroupOwner);
                            if (!info.isGroupOwner){
                                // // TODO: 16/10/9 进行连接

                            }
                        }
                    });
                }

            }

        };

        registerReceiver(myReceiver, intentFilter);


    }
    /**
     *  在设备进行发布服务, 并初始化 Wi-Fi Direct渠道
     */
    private void announceWiFiDirectService(){
        mServiceWifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        final Looper mWFDLooper = handlerThread.getLooper();
        mServiceChannel = mServiceWifiP2pManager.initialize(getApplicationContext(), mWFDLooper, new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.e(TAG, "通道回调断开");
                mWFDLooper.quit();
            }
        });

        HashMap<String, String> txtRecords = new HashMap<>();
        WifiP2pDnsSdServiceInfo mServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_NAME, "_http._tcp", txtRecords);

        mServiceWifiP2pManager.addLocalService(mServiceChannel, mServiceInfo, new WifiP2pManager.ActionListener() {
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
     *  客户端的发现服务
     */
    private void discoverWiFiDirectService(){
        final WifiP2pManager mWiFiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        final Looper mWFDLopper = handlerThread.getLooper();

        final WifiP2pManager.Channel mChannel = mWiFiP2pManager.initialize(getApplicationContext(), mWFDLopper, new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.e(TAG, "通道回调断开");
                mWFDLopper.quit();
            }
        });

        WifiP2pDnsSdServiceRequest mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance("_http._tcp");
        mWiFiP2pManager.addServiceRequest(mChannel, mServiceRequest, null);

        // 注册一个找到符合WifiP2pServiceRequest服务后的函数回调.
        mWiFiP2pManager.setServiceResponseListener(mChannel, new WifiP2pManager.ServiceResponseListener() {
            @Override
            public void onServiceAvailable(int protocolType, byte[] responseData, WifiP2pDevice srcDevice) {

                Log.d(TAG, "onServiceAvailable: DNS-SD Service available: "+srcDevice);
                // 取消发现服务的动作
                mWiFiP2pManager.clearServiceRequests(mChannel, null);
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                wifiP2pConfig.deviceAddress = srcDevice.deviceAddress;  // 远程设备的网络MAC地址,  不是ip地址!
                wifiP2pConfig.groupOwnerIntent = 0;                     // 告诉连接的设备它不想成为该组的所有者
                mWiFiP2pManager.connect(mChannel, wifiP2pConfig, null);
            }
        });

        mWiFiP2pManager.setDnsSdResponseListeners(

                mChannel,

                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {

                    }
                },

                new WifiP2pManager.DnsSdTxtRecordListener() {
                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {

                    }
                }

        );


        mWiFiP2pManager.discoverPeers(
                mChannel,

                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "客户端 peer discovery success");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(TAG, "客户端 peer discovery failure");
                    }
                }
        );

        mWiFiP2pManager.discoverServices(
                mChannel,

                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "客户端 service discovery success");

                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(TAG, "客户端 service discovery success");

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
