package com.szysky.note.multitouch;

import android.app.Activity;
import android.os.Bundle;

/**
 * Author :  suzeyu
 * Time   :  2016-09-26  下午4:09
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 展示多点触控的自定义View
 */

public class MultiTouchActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_touch);
    }
}
