package com.szysky.note.storeortest.hideapi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Erik Hellman
 */
public class WifiApUtil {
    private static final String TAG = "WifiApUtil";

    public static Method getWifiAPMethod(WifiManager wifiManager) {
        try {
            Class clazz = wifiManager.getClass();
            return clazz.getMethod("isWifiApEnabled");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean invokeIsWifiAPEnabled(WifiManager wifiManager,
                                         Method isWifiApEnabledMethod) {
        try {
            return (Boolean) isWifiApEnabledMethod.invoke(wifiManager);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static WifiConfiguration getWifiApConfig(Context context) {
        WifiConfiguration wifiConfiguration = null;
        try {
            WifiManager wifiManager =
                    (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Class clazz = WifiManager.class;
            Method getWifiApConfigurationMethod =
                    clazz.getMethod("getWifiApConfiguration");
            return (WifiConfiguration)
                    getWifiApConfigurationMethod.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Cannot find method", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Cannot call method", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Cannot call method", e);
        }
        return wifiConfiguration;
    }

}
