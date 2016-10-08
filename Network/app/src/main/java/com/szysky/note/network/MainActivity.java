package com.szysky.note.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRepeatSend();
    }


    /**
     * 设置定时轮序检测功能
     */
    public void setRepeatSend(){

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
//        long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long interval = 1000 * 3;
        long start = System.currentTimeMillis() ;

        Intent pollIntent = new Intent("com.heiheihei");
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, pollIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, start, interval, pendingIntent);

    }
}
