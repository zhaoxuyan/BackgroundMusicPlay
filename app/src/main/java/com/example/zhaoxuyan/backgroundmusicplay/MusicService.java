package com.example.zhaoxuyan.backgroundmusicplay;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by zhaoxuyan on 2017/12/5.
 * activity需要和service交互时，采用service的第二种启动方式，即bindService（）
 * service需要创建自己的IBinder类，继承Binder，override这个类的onBind()方法，
 * 在该方法中向clients返回这个IBinder实例
 */

public class MusicService extends Service {

    @Override
    public String getPackageName() {
        return "com.example.zhaoxuyan.backgroundmusicplay";
    }


    private Uri[] musicDir = new Uri[]{
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.say),
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.outlaw),
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.thisthing),
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.litanybedroom),
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.terrorjrdeathwish),
    };

    public int musicIndex = 0;

    public void setMusicIndex(int musicIndex) {
        this.musicIndex = musicIndex;
    }

    public int getMusicIndex() {
        return this.musicIndex;
    }


    public class MyIBinder extends Binder {

        public Service getService() {
            return MusicService.this;
        }
    }

    IBinder musicIBinder = new MyIBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        //当绑定后，返回一个musicBinder
        return musicIBinder;
//        return new IMyAidl();
    }

    public class IMyAidl extends IMyAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void play() throws RemoteException {
            MusicService.this.play();
        }
    }

    //获取到activity的Handler，用来通知更新进度条
    Handler mHandler;

    //播放音乐的媒体类
    MediaPlayer mediaPlayer;

    private String TAG = "MusicService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");

        init();
    }

    //初始化音乐播放
    void init() {
        //进入Idle
        mediaPlayer = new MediaPlayer();
        try {
            //初始化音乐文件
            mediaPlayer = MediaPlayer.create(this, musicDir[musicIndex]);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // prepare 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //返回当前的播放进度，是double类型，即播放的百分比
    public double getProgress() {
        int position = mediaPlayer.getCurrentPosition();

        int time = mediaPlayer.getDuration();

        double progress = (double) position / (double) time;

        return progress;
    }

    //通过activity调节播放进度
    public void setProgress(int max, int dest) {
        int time = mediaPlayer.getDuration();
        mediaPlayer.seekTo(time * dest / max);
    }

    //测试播放音乐
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void recycleTrue(){
        mediaPlayer.setLooping(true);

    }

    public void recycleFalse(){
        mediaPlayer.setLooping(false);
    }

    //暂停音乐
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    //切换上一首音乐
    public void preMusic() {
        if (mediaPlayer != null && musicIndex > -1) {
            mediaPlayer.reset();
            mediaPlayer.stop();
            try {
                if (musicIndex == 0) {
                    mediaPlayer = MediaPlayer.create(this, musicDir[musicDir.length - 1]);
                    musicIndex = musicDir.length - 1;
                } else {
                    mediaPlayer = MediaPlayer.create(this, musicDir[musicIndex - 1]);
                    musicIndex--;
                }
//                mediaPlayer.seekTo(0);
                play();
            } catch (Exception e) {
                Log.d("hint", "can't jump button_pre music");
                e.printStackTrace();
            }
        }
    }

    //切换下一首音乐
    public void nextMusic() {
        if (mediaPlayer != null && musicIndex < musicDir.length) {
            mediaPlayer.reset();
            mediaPlayer.stop();
            try {
                if (musicIndex == musicDir.length - 1) {
                    mediaPlayer = MediaPlayer.create(this, musicDir[0]);
                    musicIndex = 0;
                } else {
                    mediaPlayer = MediaPlayer.create(this, musicDir[musicIndex + 1]);
                    musicIndex++;
                }
                play();
            } catch (Exception e) {
                Log.d("hint", "can't jump button_next music");
                e.printStackTrace();
            }
        }
    }

    public void changeMusic() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(this, musicDir[musicIndex]);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

    }


    //service 销毁时，停止播放音乐，释放资源
    @Override
    public void onDestroy() {
        // 在activity结束的时候回收资源
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}

