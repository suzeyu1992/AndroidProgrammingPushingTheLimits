package com.szysky.note.ipc.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Author :  suzeyu
 * Time   :  2016-09-28  下午3:59
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 * ClassDescription :
 */

public class CustomData implements Parcelable {
    public   String name;
    protected CustomData(Parcel in) {
    }

    public static final Creator<CustomData> CREATOR = new Creator<CustomData>() {
        @Override
        public CustomData createFromParcel(Parcel in) {
            CustomData customData = new CustomData(in);
            customData.name = (in.readString());
            return customData;
        }

        @Override
        public CustomData[] newArray(int size) {
            return new CustomData[size];
        }
    };

    public CustomData(Date date) {
        name = date.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
