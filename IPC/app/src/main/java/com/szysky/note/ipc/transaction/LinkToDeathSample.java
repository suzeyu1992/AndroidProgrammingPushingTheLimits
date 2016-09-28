package com.szysky.note.ipc.transaction;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Author :  suzeyu
 * Time   :  2016-09-28  下午2:40
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 无实际意义, 只为演示如何使用IBinder注册link-to-death
 */

public class LinkToDeathSample extends Service {

    private static final String TAG = LinkToDeathSample.class.getSimpleName();


    /**
     *  对客户端提供注册方法
     */
    private void notifyRemoteServiceDeath(IBinder iBinder ){
        try {
            iBinder.linkToDeath(new MyLinkToDeath(), 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MyLinkToDeath implements IBinder.DeathRecipient{

        @Override
        public void binderDied() {
            // 处理远端binder被杀死的情况
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
