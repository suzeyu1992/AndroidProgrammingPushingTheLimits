package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
            Log.e("sususu", "接收到 BOOT_COMPLETED" );
            Toast.makeText(context, "重启啦", Toast.LENGTH_SHORT).show();

        }else if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)){
            Log.e("sususu", "接收到 MY_PACKAGE_REPLACED" );
        }
    }
}
