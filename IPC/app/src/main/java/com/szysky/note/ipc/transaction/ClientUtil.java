package com.szysky.note.ipc.transaction;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Author :  suzeyu
 * Time   :  2016-09-28  上午11:03
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 演示客户端使用IBinder引用向Service执行自定义的Binder事务.
 */

public class ClientUtil {

    public String performCustomBinderTransaction(IBinder binder, String arg0, int arg1) throws RemoteException {

        Parcel request = Parcel.obtain();
        Parcel response = Parcel.obtain();

        // 组装请求数据, 要发送到服务端的
        request.writeString(arg0);
        request.writeInt(arg1);

        // 执行事务
        binder.transact(IBinder.FIRST_CALL_TRANSACTION, request, response, 0);

        // 从响应中读取结果
        String result = response.readString();

        // 释放资源, 以便循环利用
        request.recycle();
        response.recycle();

        return result;
    }
}
