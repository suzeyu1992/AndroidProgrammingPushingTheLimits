package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Author : suzeyu
 * Time   : 16/10/2  下午3:04
 * Blog   : http://szysky.com
 * GitHub : https://github.com/suzeyu1992
 * <p>
 * ClassDescription :  用于检测设备是否连接到预先设置的""WiFi的广播
 */

public class CheckForHomeWifi extends BroadcastReceiver {

    public static final String PREFS_HOME_WIFI_SSID = "homeSSID";

    @Override
    public void onReceive(Context context, Intent intent) {

        // 需要判断的路由名字 对应ssid
        String name = "\"ziroom502\"";

        // 1.首先判断wifi是否开启, 并连接
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null && networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {

            // 2.再判断连接的wifi的具体信息
            WifiInfo WifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (WifiInfo != null && name.equals(WifiInfo.getSSID())) {
                Log.d("sususu", "连接到指定wifi");
            } else {
                Log.d("sususu", "连接到其他wifi");
            }
        }
    }
}
