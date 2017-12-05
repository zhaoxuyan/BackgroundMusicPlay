package com.example.zhaoxuyan.backgroundmusicplay;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;


public class MainActivity extends Activity {
    ActionBar mActionBar;
    // 初始化mBound
    Boolean mBound = false;

    MusicService mService;

    SeekBar seekBar;

    //多线程，后台更新UI
    Thread myThread;

    //控制后台线程退出
    boolean playStatus = true;

    /**
     *
     * clients需要实现一个ServiceConnection对象（是一个内部匿名类），
     * clients可以利用这个ServiceConnection中的onServiceConnected（）方法获取这个IBinder实例，
     * 在通过这个IBinder中的方法获取service的实例，从而访问service的public方法。
     *
     * mBound: true 绑定成功; false 绑定失败
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        // 连接 Successful
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.MyBinder myBinder = (MusicService.MyBinder) binder;

            //获取service实例
            mService = (MusicService) myBinder.getService();

            //绑定成功
            mBound = true;

            //开启线程，更新UI
            myThread.start();
        }

        // 连接 Fail
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * 在activity开启多线程，
     * 后台每隔100ms就发送一次message，
     * 在UI线程中利用handler对message进行判定，
     * activity调用service的函数，
     * 得到播放的进度（百分比），
     * 及时更新seekbar的进度条。
     */
    // 处理进度条信息
    // 更新UI
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //从bundle中获取进度，是double类型，播放的百分比
                    double progress = msg.getData().getDouble("progress");

                    //根据播放百分比，计算seekbar的实际位置
                    int max = seekBar.getMax();
                    int position = (int) (max * progress);

                    //设置seekbar的实际位置
                    seekBar.setProgress(position);
                    break;
                default:
                    break;
            }
        }
    };

    // 实现runnable接口，多线程实时更新进度条
    public class MyThread implements Runnable {
        // 通知UI更新的消息

        // 用来向UI线程传递进度的值
        Bundle data = new Bundle();

        // 更新UI间隔时间
        int milliseconds = 100;
        // 进度
        double progress;

        @Override
        public void run() {
            // TODO Auto-generated method stub

            //用来标识是否还在播放状态，用来控制线程退出
            while (playStatus) {

                try {
                    //绑定成功才能开始更新UI
                    if (mBound) {

                        //发送消息，要求更新UI

                        Message msg = new Message();
                        data.clear();

                        progress = mService.getProgress();
                        msg.what = 0;

                        data.putDouble("progress", progress);
                        msg.setData(data);
                        mHandler.sendMessage(msg);
                    }
                    //Thread.currentThread().sleep(milliseconds);
                    //每隔100ms更新一次UI
                    Thread.sleep(milliseconds);


                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        mActionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 定义一个新线程，用来发送消息，通知更新UI
        // 内部类的调用
        myThread = new Thread(new MyThread());

        //绑定service;
        Intent serviceIntent = new Intent(this, MusicService.class);

        //如果未绑定，则进行绑定
        if (!mBound) {
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        //初始化播放按钮
        final ImageView playButton = (ImageView) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (mBound && !(mService.mediaPlayer.isPlaying())) {
                    playButton.setImageResource(R.drawable.pausebutton);
                    mService.play();
                }
                else if (mBound && mService.mediaPlayer.isPlaying()){
                    playButton.setImageResource(R.drawable.playbutton);
                    mService.pause();
                }
            }

        });

//        //初始化暂停按钮
//        ImageView pauseButton = (ImageView) findViewById(R.id.pauseButton);
//        pauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // TODO Auto-generated method stub
//                //首先需要判定绑定情况
//                if (mBound) {
//                    mService.pause();
//                }
//            }
//        });

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手动调节进度
                // TODO Auto-generated method stub
                //seekbar的拖动位置
                int dest = seekBar.getProgress();
                //seekbar的最大值
                int max = seekBar.getMax();
                //调用service调节播放进度
                mService.setProgress(max, dest);
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

        });

    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public void onDestroy() {
        //销毁activity时，要记得销毁线程
        playStatus = false;
        super.onDestroy();
    }

}

