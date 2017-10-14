package com.tools.security.demo;

import android.app.Activity;
import android.os.Bundle;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/28.
 */

public class DemoGradientActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }
}
