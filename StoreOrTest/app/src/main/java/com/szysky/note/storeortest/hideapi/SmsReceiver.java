package com.szysky.note.storeortest.hideapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.szysky.note.storeortest.R;

public class SmsReceiver extends BroadcastReceiver {
    // Telephony.java 中隐藏的常量
    public static final String SMS_RECEIVED_ACTION
            = "android.provider.Telephony.SMS_RECEIVED";

    public static final String MESSAGE_SERVICE_NUMBER = "+461234567890";
    private static final String MESSAGE_SERVICE_PREFIX = "MYSERVICE";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action)) {
            // 通过 pdus 获取SMS数据的隐藏键
            Object[] messages = android.sms
                    (Object[]) intent.getSerializableExtra("pdus");
            for (Object message : messages) {
                byte[] messageData = (byte[]) message;
                SmsMessage smsMessage =
                        SmsMessage.createFromPdu(messageData);
                Log.e("haha", "收到消息来自: "+smsMessage.getOriginatingAddress()+ "   内容:"+smsMessage.getMessageBody());

                processSms(smsMessage);
            }
        }
    }

    private void processSms(SmsMessage smsMessage) {
        String from = smsMessage.getOriginatingAddress();
        if (MESSAGE_SERVICE_NUMBER.equals(from)) {
            String messageBody = smsMessage.getMessageBody();
            if (messageBody.startsWith(MESSAGE_SERVICE_PREFIX)) {
                // TODO: 数据验证通过开始处理
                Log.e("haha", "processSms: "+messageBody);
            }
        }
    }
}
