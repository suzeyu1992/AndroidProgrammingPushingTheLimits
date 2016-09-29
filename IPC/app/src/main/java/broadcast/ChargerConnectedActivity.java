package broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
