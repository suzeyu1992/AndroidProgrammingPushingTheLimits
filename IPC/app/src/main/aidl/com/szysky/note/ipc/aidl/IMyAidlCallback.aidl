// IMyAidlCallback.aidl
package com.szysky.note.ipc.aidl;

// Declare any non-default types here with import statements
import com.szysky.note.ipc.aidl.CustomData;

// 使用 oneway 关键字, 告诉AIDL编译器该接口只是单向通信. 对调用者的响应不是必须的(本例中是Service)
oneway interface IMyAidlCallback {
  void onDataUpdated(in CustomData[] data);
}
