package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 接收电池手机充电和断开充电的广告
 */
public class ChargerConnectedReceiver extends BroadcastReceiver {
    public ChargerConnectedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_POWER_CONNECTED.equals(action)){
            Toast.makeText(context, "手机充电啦", Toast.LENGTH_SHORT).show();
        }else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)){
            Toast.makeText(context, "手机不充电了", Toast.LENGTH_SHORT).show();
        }
    }
}
