package com.example.zhaoxuyan.backgroundmusicplay.playlist;

/**
 * Created by zhaoxuyan on 2017/12/20.
 * Playlist
 */

public class Music {
    private String mMusicTitle;
    private String mMusicAuthor;

    // Constructor
    public Music(String musicTitle, String musicAuthor) {
        this.mMusicTitle = musicTitle;
        this.mMusicAuthor = musicAuthor;
    }

    //Getter


    public String getmMusicTitle() {
        return mMusicTitle;
    }

    public String getmMusicAuthor() {
        return mMusicAuthor;
    }
}
