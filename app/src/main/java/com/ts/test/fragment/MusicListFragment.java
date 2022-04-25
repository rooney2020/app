package com.ts.test.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ts.test.R;
import com.ts.test.adapter.MusicListAdapter;
import com.ts.test.bean.Music;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private Callback callback;

    private MusicListAdapter adapter;

    private List<Music> musicList = new ArrayList<>();

    private ListView listView;

    public MusicListFragment(Callback callback, List<Music> musicList) {
        this.callback = callback;
        this.musicList = musicList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MusicListFragmentReceiver myReceiver = new MusicListFragmentReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.thundersoft.test.musiclistfragment");
        getActivity().registerReceiver(myReceiver, filter);
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.music_list);
        adapter = new MusicListAdapter(getContext(), R.layout.item_music, musicList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onClickMusic(position);
            }
        });
    }

    public interface Callback {
        void onClickMusic(int position);

        List<Music> getMusicList();
    }

    class MusicListFragmentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicList = callback.getMusicList();
            Log.e("TAG", "onReceive:  OK");
            adapter.setMusicList(musicList);
            adapter.notifyDataSetChanged();
//            listView.setAdapter(new MusicListAdapter(getContext(), R.layout.item_music, musicList));
        }
    }
}