package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *  判断网络 3G或者LTE网络状态
 */

public class WhenOn3GorLTE extends BroadcastReceiver {
    private static final String TAG = WhenOn3GorLTE.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            
            if (noConnectivity){
                Log.e(TAG, "没有连接" );
            }else{
                int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, ConnectivityManager.TYPE_DUMMY);
                
                if (networkType == ConnectivityManager.TYPE_MOBILE){
                    checkfor3GorLte(context);
                }else{
                    Log.i(TAG, "不是移动连接");
                }
            }

        }
    }


    /**
     *  当前如果移动数据开启, 那么显示出移动数据的连接类型
     */
    private void checkfor3GorLte(Context context){

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()){
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                Log.d(TAG, "连接类型: NETWORK_TYPE_HSDPA");
                break;

            case TelephonyManager.NETWORK_TYPE_HSPA:
                Log.d(TAG, "连接类型: NETWORK_TYPE_HSPA");
                break;

            case TelephonyManager.NETWORK_TYPE_HSPAP:
                Log.d(TAG, "连接类型: NETWORK_TYPE_HSPAP");
                break;

            case TelephonyManager.NETWORK_TYPE_HSUPA:
                Log.d(TAG, "连接类型: NETWORK_TYPE_HSUPA");
                break;

            case TelephonyManager.NETWORK_TYPE_LTE:
                Log.d(TAG, "连接类型: NETWORK_TYPE_LTE");
                break;

            default:
                Log.d(TAG, "连接类型: 未知类型, 可能传输速度会慢");
                break;

        }

    }
}
