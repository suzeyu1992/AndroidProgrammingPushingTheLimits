package com.szysky.note.multitouch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 *  实现一个bindService模式的, 简单演示方法被调用的服务类
 */
public class LocalService extends Service {
    public LocalService() {
    }

    public void doBackgroundOperation(){
        Toast.makeText(getApplicationContext(), "定义服务方法被调用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public class LocalBinder extends Binder{
        public LocalService getService(){
            return LocalService.this;
        }

    }
}
