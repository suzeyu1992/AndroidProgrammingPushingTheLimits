package com.szysky.note.multitouch;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 *  实现一个bindService模式的, 简单演示方法被调用的服务类
 */
public class LocalService extends Service {

    private static final int NOTIFICATION_ID = 1000;
    private Callback mCallback;

    @SuppressLint("NewApi")
    private Notification buildNotification() {
        // Build the notification to be shown
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("本地服务");
        builder.setContentText("go");
        Notification notification = builder.build();
        return notification;
    }

    /**
     *  调用方法会开启一个线程任务
     */
    public void doBackgroundOperation(MyComplexDataObject dataObject){
        new MyAsynctask().execute(dataObject);
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

    /**
     *  给调用服务的组件留出设置监听的方法.
     */
    public void setCallback(Callback callback){
        mCallback = callback;
    }

    /**
     *  定义回调接口
     */
    public interface Callback{
        void onOperationProgress(int progress);
        void onOperationCompleted(MyComplexResult complexResult);
    }

    /**
     *  定义个AsyncTask任务
     */
    private final class MyAsynctask extends AsyncTask<MyComplexDataObject, Integer, MyComplexResult>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 开启前台服务
            startForeground(NOTIFICATION_ID, buildNotification());

        }

        @Override
        protected MyComplexResult doInBackground(MyComplexDataObject... params) {
            MyComplexResult myComplexResult = new MyComplexResult();
            // 没有具体实现, 只为了显示流程 , 并简单输出一条日志

            return myComplexResult;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mCallback != null && values.length > 0){
                for (Integer value : values){
                    mCallback.onOperationProgress(value);
                }
            }
        }

        @Override
        protected void onPostExecute(MyComplexResult complexResult) {
            if (mCallback != null){
                mCallback.onOperationCompleted(complexResult);
            }
            stopForeground(true);
        }

        @Override
        protected void onCancelled(MyComplexResult complexResult) {
            super.onCancelled(complexResult);
            stopForeground(true);
        }
    }

}
