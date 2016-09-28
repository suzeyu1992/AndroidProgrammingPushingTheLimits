// IMyApiInterfaceV1.aidl
package com.szysky.note.ipc.aidl;

// Declare any non-default types here with import statements
import com.szysky.note.ipc.aidl.CustomData;

interface IMyApiInterfaceV1 {

   // 检查是否为素数的远程方法
   boolean isPrime(long value);

   // 检索timestamp以后的所有CustomData对象, 至多获取result.length对象
   void getAllDataSince(long timestamp, out CustomData[] result);

   // 存储CustomData对象
   void storeData(in CustomData data);
}
