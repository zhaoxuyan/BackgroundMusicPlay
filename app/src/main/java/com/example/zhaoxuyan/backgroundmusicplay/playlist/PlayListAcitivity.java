package com.example.zhaoxuyan.backgroundmusicplay.playlist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhaoxuyan.backgroundmusicplay.MainActivity;
import com.example.zhaoxuyan.backgroundmusicplay.MusicService;
import com.example.zhaoxuyan.backgroundmusicplay.R;

import java.util.ArrayList;

public class PlayListAcitivity extends AppCompatActivity {
    // 初始化mBound
    Boolean mBound = false;

    MusicService mService;
    private String[] musicTitleDir = new String[]{
            "Something about you",
            "Outlaw",
            "This Thing Is Not Break You",
            "Bedroom",
            "Death wish"
    };

    private String[] musicAuthorDir = new String[]{
            "Odesza",
            "it's different",
            "Christa Wells",
            "Litany",
            "Terror Jr"
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // 连接 Successful
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.MyIBinder myIBinder = (MusicService.MyIBinder) binder;

            //获取service实例
            mService = (MusicService) myIBinder.getService();

            //绑定成功
            mBound = true;

        }

        // 连接 Fail
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        //绑定service;
        Intent serviceIntent = new Intent(this, MusicService.class);
        if (!mBound) {
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        ArrayList<Music> musics = new ArrayList<>();
        for (int i = 0; i < musicTitleDir.length; i++) {
            musics.add(new Music(musicTitleDir[i], musicAuthorDir[i]));
        }

        MusicAdapter musicAdapter = new MusicAdapter(this, musics);
        ListView listView = (ListView) findViewById(R.id.playlist);
        listView.setAdapter(musicAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBound && mService != null) {
                    mService.setMusicIndex(position);
                    mService.changeMusic();

                    Intent intent = new Intent(PlayListAcitivity.this, MainActivity.class);
                    int musicIndex = position;
                    intent.putExtra("musicIndex",musicIndex);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });



    }

}
