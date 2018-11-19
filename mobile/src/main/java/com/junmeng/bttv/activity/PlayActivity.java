package com.junmeng.bttv.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.junmeng.bttv.R;
import com.junmeng.bttv.app.Constants;
import com.junmeng.bttv.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 视频播放
 */
public class PlayActivity extends BaseActivity {
    private static final String TAG = "PlayActivity";
    VideoView videoView;
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        videoPath=getIntent().getStringExtra(Constants.EXTRA_VIDEO_PATH);
        Log.i(TAG, "onCreate: videoPath="+videoPath);
        initViews();
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        videoView.setVideoPath(videoPath);
       // MediaController mMediaController = new MediaController(this);
        //  videoView.setMediaController(mMediaController);//显示控制栏
        videoView.seekTo(0);
        videoView.start();
    }

    private void initViews() {
        videoView = findViewById(R.id.vv_video);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Integer event) {
        if (event==9) {
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        videoView.stopPlayback();
        super.onBackPressed();
    }
}
