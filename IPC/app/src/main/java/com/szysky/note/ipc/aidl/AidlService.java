package com.szysky.note.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 *  aidl服务端实现代码
 */
public class AidlService extends Service {

    private ArrayList<CustomData> mCustomDataCollection;

    public static final String TAG = AidlService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mCustomDataCollection = new ArrayList<>();
        mCallbacks = new HashSet<>();
        // 使用存储的数据填充列表
        // ....
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mBinder;
    }

    /**
     *  假设此方法内部有逻辑处理  对应调用回调的方法是 {@link IMyApiInterfaceV1.Stub#isPrime(long)}
     */
    public static boolean isPrimeImpl(long value){
        // ... 省略具体实现
        Log.e(TAG, "判断数据的回调已经进来" );
        return false;
    }

    /**
     *  假设此方法内部有逻辑处理  对应调用回调的方法是 {@link IMyApiInterfaceV1.Stub#getAllDataSince(long, CustomData[])}
     */
    public void getAllDataSinceImpl(long timestamp, CustomData[] result){
        Log.e(TAG, "获得全部数据回调已经进来" );

    }

    /**
     *  假设此方法内部有逻辑处理  对应调用回调的方法是 {@link IMyApiInterfaceV1.Stub#storeData(CustomData)}
     */
    public void storeDataImpl(CustomData data){
        Log.e(TAG, "存储数据的回调已经进来" );

    }


    private HashSet<IMyAidlCallback> mCallbacks;
    /**
     *  实现AIDL定义的接口文件
     */
    private final IMyApiInterfaceV1.Stub mBinder = new IMyApiInterfaceV1.Stub(){
        @Override
        public boolean isPrime(long value) throws RemoteException {

            return isPrimeImpl(value);
        }

        @Override
        public void getAllDataSince(long timestamp, CustomData[] result) throws RemoteException {
            getAllDataSinceImpl(timestamp, result);
        }

        @Override
        public void storeData(CustomData data) throws RemoteException {
            Iterator<IMyAidlCallback> iterator = mCallbacks.iterator();
            while(iterator.hasNext()){
                iterator.next().onDataUpdated(null);
            }
            storeDataImpl(data);
        }

        @Override
        public void addCallback(final IMyAidlCallback callback) throws RemoteException {
            // 实现回调. 这里同样使用linkToDeath()来接收通知, 以防客户端Binder被杀死
            mCallbacks.add(callback);
            callback.asBinder().linkToDeath(new DeathRecipient() {
                @Override
                public void binderDied() {
                    mCallbacks.remove(callback);
                }
            }, 0);
        }


    };

}
