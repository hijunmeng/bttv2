package com.junmeng.bttv.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.junmeng.bttv.R;
import com.junmeng.bttv.base.BaseActivity;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoActivityAndFinish(MainActivity.class);
            }
        },1200);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
