package com.szysky.note.storeortest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.szysky.note.storeortest.secure.ClientSecure;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // 进行加密
            String encryResult = ClientSecure.encryptClearText(ClientSecure.userPassword, ClientSecure.textStr);
            System.out.println("加密后的结果:"+encryResult);

            // 进行解密
            String rawData = ClientSecure.decryptData(ClientSecure.userPassword, encryResult);
            System.out.println("解密后得到的原始数据:"+rawData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
