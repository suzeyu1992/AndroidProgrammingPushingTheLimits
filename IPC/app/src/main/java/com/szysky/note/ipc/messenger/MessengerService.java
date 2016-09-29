package com.szysky.note.ipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Author :  suzeyu
 * Time   :  2016-09-29  上午9:43
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 服务端使用Messenger作为通讯
 */

public class MessengerService extends Service{

    private static final String TAG = "sususu";
    private Handler mMessageHandler;
    private Messenger mMessenger;

    @Override
    public void onCreate() {
        super.onCreate();

        // 创建Handler 并构建 Messenger 对象
        HandlerThread handlerThread = new HandlerThread("MessengerService");
        handlerThread.start();
        mMessageHandler = new Handler(handlerThread.getLooper(), new MyHandlerCallback());
        mMessenger = new Messenger(mMessageHandler);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 返回Messenger信使的Binder对象
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出循环
        mMessageHandler.getLooper().quit();
    }

    /**
     *  创建一个HandlerCallback用来接收 Messenger传入的消息
     */
    private class MyHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {

            boolean delivered = false;
            switch (msg.what){
                case MessageAPI.SEND_TEXT_MSG:
                    delivered = sendTextMessage((String)msg.obj);
                    Log.d(TAG, "服务端收到 text消息");
                    break;

                case MessageAPI.SEND_PHOTO_MSG:
                    delivered = sendPhotoMessage((Bitmap)msg.obj);
                    Log.d(TAG, "服务端收到 photo消息");

                    break;
            }

            Message reply = Message.obtain(null, MessageAPI.MESSAGE_DELIVERED_MSG, delivered);

            try {
                // 可以利用 message的replyTo来响应客户端发的请求
                msg.replyTo.send(reply);
                
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "handleMessage: 服务端响应客户端失败", e );
            }

            return true;
        }
    }

    // 分发后返回true
    private boolean sendPhotoMessage(Bitmap obj) {
        // 省略业务步骤
        return true;
    }

    // 分发后返回true
    private boolean sendTextMessage(String obj) {
        // 省略业务步骤
        return true;
    }


}
