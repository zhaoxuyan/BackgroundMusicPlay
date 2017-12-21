package com.example.zhaoxuyan.backgroundmusicplay.playlist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zhaoxuyan.backgroundmusicplay.R;

import java.util.ArrayList;

/**
 * Created by zhaoxuyan on 2017/12/20.
 * MusicAdapter
 */

public class MusicAdapter extends ArrayAdapter<Music> {

    public MusicAdapter(Activity context, ArrayList<Music> playLists) {

        //ArrayAdapter(Context context, int resource, List<T> objects)
        super(context, 0, playLists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Music currenMusic = getItem(position);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater
                    .from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView musicTitle = listItemView.findViewById(R.id.playlist_musictitle);
        musicTitle.setText(currenMusic.getmMusicTitle());
        TextView musicAuthor = listItemView.findViewById(R.id.playlist_author);
        musicAuthor.setText(currenMusic.getmMusicAuthor());
        return listItemView;
    }
}
