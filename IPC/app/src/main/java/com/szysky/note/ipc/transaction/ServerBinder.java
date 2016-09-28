package com.szysky.note.ipc.transaction;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Author :  suzeyu
 * Time   :  2016-09-28  上午11:15
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 实现一个Binder类
 */

public class ServerBinder extends Binder {

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
//        return super.onTransact(code, data, reply, flags);

        // 开始读取客户端发送的请求数据
        String arg0 = data.readString();
        int arg1 = data.readInt();

        // 处理接收结果, 并生成返回的数据
        String result = buildResult(arg0, arg1);

        // 把结果写入想用Parcel
        reply.writeString(result);

        // 成功后返回true
        return true;


    }

    private String buildResult(String arg0, int arg1){
        String result = null;

        // ...这里省略业务逻辑, 以后可自行添加

        return result;
    }
}
