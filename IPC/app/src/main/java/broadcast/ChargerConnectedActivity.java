package broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.szysky.note.ipc.R;

/**
 * Author :  suzeyu
 * Time   :  2016-09-29  下午2:19
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 * ClassDescription :
 */

public class ChargerConnectedActivity extends Activity {

    private ChargerConnectedReceiver chargerConnectedReceiver;

    /**
     * 用于发送本地广播标识的action
     */
    public static final String LOCAL_BROADCAST_ACTION = "localBroadcast";
    private BroadcastReceiver mLocalReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_connected);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager instance = LocalBroadcastManager.getInstance(getApplicationContext());
                // 上面定义的通电状态action
                Intent intent = new Intent(LOCAL_BROADCAST_ACTION);
                instance.sendBroadcast(intent);
            }
        });

        // ***************************************************************
        // 1.第一遍打开下一行的注释运行程序,进入到广播解释界面, 发送一个粘性广播
//        sendStickyBroadcast();

        // 2.打开下面的注释, 把上一行注释掉. 注册一个粘性广播发送的接收者, 运行, 会发现上一次的广播
//        myRegisterStickyBroadcast();
        // ***************************************************************


        // 注册电池广播监听
//        myRegisterBattery();

        setComponentEnable(false, ChargerConnectedReceiver.class );

    }

    /**
     * 注册一个粘性广播
     */
    private void myRegisterStickyBroadcast(){
        IntentFilter intent = new IntentFilter("com.szysky.test.sticky");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {
                    // 这是一个粘性广播
                    Log.e("sususu", "这是一个粘性广播");
                } else {
                    Log.e("sususu", "这是不是粘性广播");

                }
            }
        };
        registerReceiver(broadcastReceiver, intent);
    }

    /**
     * 发送一个粘性广播
     */
    private void sendStickyBroadcast(){
        Intent intent = new Intent("com.szysky.test.sticky");
        sendStickyBroadcast(intent);
        Log.e("sususu", "粘性广播注册成功");
    }


    /**
     *  监听电池变化的粘性广播接收者
     */
    private void myRegisterBattery(){
        // 构建广播接收者要接收的action
        IntentFilter intent = new IntentFilter();
        intent.addAction(Intent.ACTION_BATTERY_CHANGED);
        intent.addAction(Intent.ACTION_BATTERY_OKAY);
        intent.addAction(Intent.ACTION_BATTERY_LOW);

        // 创建监听
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {
                    Log.e("sususu", "这是一个粘性广播");
                } else {
                    Log.e("sususu", "这是不是粘性广播");
                }
            }
        };
        // 注册接收者
        registerReceiver(broadcastReceiver, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocalBroadcast();

    }

    private void initLocalBroadcast() {
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter(LOCAL_BROADCAST_ACTION);

        mLocalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "本地广播接收到", Toast.LENGTH_SHORT).show();
            }
        };

        instance.registerReceiver(mLocalReceiver, intentFilter);
    }


    /**
     * 设置组件
     * @param setClass 要设置改变的组件
     * @param isEnable true为启用, false为禁用
     */
    public void setComponentEnable(boolean isEnable, Class setClass){
        PackageManager pm = getPackageManager();
        // 构建要改变组件的Component
        ComponentName componentName = new ComponentName(getApplicationContext(), setClass);
        pm.setComponentEnabledSetting(componentName,
                isEnable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    /**
     *  注册全局广播
     */
    private void initGlobalBroadcast() {
        // 生成对于广播的 intent过滤条件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        chargerConnectedReceiver = new ChargerConnectedReceiver();
        registerReceiver(chargerConnectedReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //注册本地广播
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLocalReceiver);

        //注册全局广播
        //unregisterReceiver(chargerConnectedReceiver);
    }
}
