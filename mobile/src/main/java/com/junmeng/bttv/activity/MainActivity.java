package com.junmeng.bttv.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.junmeng.bttv.R;
import com.junmeng.bttv.app.Constants;
import com.junmeng.bttv.base.BaseActivity;
import com.junmeng.bttv.util.LocalFileUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * https://blog.csdn.net/gywuhengy/article/details/70214865
 */
public class MainActivity extends BaseActivity {
    BluetoothClient mClient;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothServerSocket mServerSocket;

    private TextView statusView;
    private TextView nameView;
    private VideoView video1View;
    private VideoView video2View;
    private VideoView video3View;
    private VideoView video4View;
    private ImageView image1View;
    private ImageView image2View;
    private ImageView image3View;
    private ImageView image4View;
    private boolean isRunning;
    private List<VideoView> videos = new ArrayList<>();
    private List<ImageView> images = new ArrayList<>();
    private List<File> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initViews();
        mClient = new BluetoothClient(this);
        if (!mClient.isBluetoothOpened()) {
            mClient.openBluetooth();
        }
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
        nameView.setText("本机蓝牙：" + mBluetoothAdapter.getName() + " " + mBluetoothAdapter.getAddress());
        onClickStart(null);

        checkPermission();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    private void checkPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        File file = new File(Constants.VIDEO_DIR);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    } else {
                        showToast("请授予写权限");
                    }
                });

    }

    private void initViews() {
        statusView = findViewById(R.id.tv_status);
        nameView = findViewById(R.id.tv_name);
        video1View = findViewById(R.id.vv_video1);
        video2View = findViewById(R.id.vv_video2);
        video3View = findViewById(R.id.vv_video3);
        video4View = findViewById(R.id.vv_video4);
        image1View = findViewById(R.id.iv_video1);
        image2View = findViewById(R.id.iv_video2);
        image3View = findViewById(R.id.iv_video3);
        image4View = findViewById(R.id.iv_video4);

        videos.add(video1View);
        videos.add(video2View);
        videos.add(video3View);
        videos.add(video4View);
        images.add(image1View);
        images.add(image2View);
        images.add(image3View);
        images.add(image4View);


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


        File file = new File(Constants.VIDEO_DIR);
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (int i = 0; i < Math.min(files.length, 4); i++) {
            this.files.add(files[i]);
            VideoView videoView = videos.get(i);
            videoView.setVideoPath(files[i].getAbsolutePath());
            MediaController mMediaController = new MediaController(this);
            videoView.setMediaController(mMediaController);//显示控制栏
            videoView.seekTo(0);
            //video1View.requestFocus();

            ImageView iv = images.get(i);
            Bitmap bitmap = LocalFileUtil.getVideoThumb(files[i].getAbsolutePath());
            iv.setImageBitmap(bitmap);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public void onClickStart(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建一个蓝牙服务器 参数分别：服务器名称、UUID
                try {
                    mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("123",
                            UUID.fromString("11101101-0000-1000-8000-00805F9B34FB"));
                    updateView("正在接收指令...");
                    //服务端接受
                    isRunning = true;
                    while (isRunning) {
                        final BluetoothSocket mSocket = mServerSocket.accept();
                        showToast("收到连接");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                InputStream is = null;
                                try {
                                    is = mSocket.getInputStream();
                                    while (isRunning) {
                                        int cmd = is.read();
                                        handle(cmd);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showToast("连接断开");
                                }

                            }
                        }).start();
                    }
                       /* BluetoothSocket mSocket = mServerSocket.accept();
                        sleep(100);
                        updateView("连接成功，正在接收指令....");

                        InputStream is = null;
                        is = mSocket.getInputStream();
                        isRunning = true;
                        while (isRunning) {
                            int cmd = is.read();
                            handle(cmd);
                        }*/
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast(e.getMessage());
                }
            }
        }).start();

    }

    private void handle(int cmd) {
        try{
            Intent intent = new Intent(this, PlayActivity.class);
            switch (cmd) {
                case 1:
                    //updateViewVisibility(image1View, View.GONE);
                    //video1View.start();

                    intent.putExtra(Constants.EXTRA_VIDEO_PATH, files.get(0).getAbsolutePath());
                    startActivity(intent);
                    break;
                case 2:
                    //updateViewVisibility(image2View, View.GONE);
                    //video2View.start();
                    intent.putExtra(Constants.EXTRA_VIDEO_PATH, files.get(1).getAbsolutePath());
                    startActivity(intent);
                    break;
                case 3:
                    //updateViewVisibility(image3View, View.GONE);
                    //video3View.start();
                    intent.putExtra(Constants.EXTRA_VIDEO_PATH, files.get(2).getAbsolutePath());
                    startActivity(intent);
                    break;
                case 4:
                    // updateViewVisibility(image4View, View.GONE);
                    // video4View.start();
                    intent.putExtra(Constants.EXTRA_VIDEO_PATH, files.get(3).getAbsolutePath());
                    startActivity(intent);
                    break;
                case 5:
                    showToast("喵喵喵");
                    break;
                case 6:
                    showToast("汪汪汪");
                    break;
                case 7:
                    showToast("嘟嘟嘟");
                    break;
                case 8:
                    showToast("咩咩咩");
                    break;
                case 9:
                    EventBus.getDefault().post(9);
                    //showToast("退出");
                    break;

            }
        }catch(Exception e){
            showToast("没有视频");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Integer event) {
        if (event == 9) {
            onBackPressed();
        }
    }

    private void updateView(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.setText(msg);
            }
        });
    }

    private void updateViewVisibility(final View view, final int visibility) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visibility);
            }
        });
    }

    @Override
    public void onBackPressed() {
        isRunning = false;
        super.onBackPressed();
    }
}
