package com.ts.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ts.test.R;
import com.ts.test.bean.Music;
import com.ts.test.util.Common;

import java.util.ArrayList;
import java.util.List;

import static com.ts.test.util.Common.MUSIC_LIST_DESC_SEPARATOR;

public class MusicListAdapter extends ArrayAdapter {

    private List<Music> musicList = new ArrayList<>();

    private Context mContext;

    public MusicListAdapter(@NonNull Context context, int resource, List<Music> musicList) {
        super(context, resource);
        mContext = context;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_music, parent, false);
        }
        TextView musicName = convertView.findViewById(R.id.music_name);
        TextView musicDesc = convertView.findViewById(R.id.music_desc);
        TextView musicOrder = convertView.findViewById(R.id.music_order);
        musicOrder.setText(String.valueOf(musicList.get(position).getId()));
        if (!Common.isDefault(musicList.get(position).getTitle())) {
            musicName.setText(musicList.get(position).getTitle());
        } else {
            musicName.setText(musicList.get(position).getFileName());
        }

        musicDesc.setText(musicList.get(position).getArtist() + MUSIC_LIST_DESC_SEPARATOR + musicList.get(position).getAlbum());
        return convertView;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }
}
