package com.szysky.note.ipc.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.szysky.note.ipc.R;

/**
 * Author :  suzeyu
 * Time   :  2016-09-29  上午10:07
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription :  使用Messenger通信的客户端
 */

public class MessengerClientActivity extends Activity implements ServiceConnection{

    private static final String TAG = "sususu";

    /**
     *  服务端的 Messenger
     */
    private Messenger mRemoteMessenger;

    /**
     *  本地构建的 Messenger
     */
    private Handler mReplyHandler;
    private Messenger mReplyMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messenger_client);

        HandlerThread handlerThread = new HandlerThread("MessengerClientActivity");
        handlerThread.start();
        mReplyHandler = new Handler(handlerThread.getLooper(), new ReplyHandlerCallback());

        mReplyMessenger = new Messenger(mReplyHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getApplicationContext(), MessengerService.class), this, MessengerService.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReplyHandler.getLooper().quit();

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // 利用服务端返回的 Messenger的IBinder 构建出一个Messenger
        mRemoteMessenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mRemoteMessenger = null;
    }

    /**
     * 按钮发送
     */
    public void onSendTextButton(View view){
        String sendStr = "嘿嘿";

        Message obtain = Message.obtain();
        obtain.what = MessageAPI.SEND_TEXT_MSG;
        obtain.obj = sendStr;
        obtain.replyTo = mReplyMessenger;

        try {
            mRemoteMessenger.send(obtain);
        } catch (RemoteException e) {
            e.printStackTrace();
            // 远程已被销毁
        }
    }

    /**
     *  接收服务端的回应
     */
    private class ReplyHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MessageAPI.MESSAGE_DELIVERED_MSG:

                    // 省略逻辑部分
                    Log.e(TAG, "客户端收到回应" );

                    break;
            }
            return true;
        }
    }
}
