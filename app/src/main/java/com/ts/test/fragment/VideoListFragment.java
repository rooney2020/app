package com.ts.test.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ts.test.R;
import com.ts.test.bean.Music;
import com.ts.test.util.MusicFileUtil;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        GridView gridView = view.findViewById(R.id.video_grid);
        List<Music> videoList = new ArrayList<>();
        gridView.setAdapter(new ArrayAdapter<Music>(getActivity(), -1, videoList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.item_video, parent, false);
                }
                TextView videoTime = convertView.findViewById(R.id.video_time);
                TextView videoName = convertView.findViewById(R.id.video_name);
                ImageView cover = convertView.findViewById(R.id.video_cover);
                videoName.setText(getItem(position).getTitle());
                videoTime.setText("01:43:52");

                File file = new File("/storage/emulated/0/Music/修炼爱情.mp3");
                try {
                    cover.setImageBitmap(MusicFileUtil.getPicFromBytes(
                            new Mp3File(file.getAbsolutePath()).getId3v2Tag().getAlbumImage(), new BitmapFactory.Options()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnsupportedTagException e) {
                    e.printStackTrace();
                } catch (InvalidDataException e) {
                    e.printStackTrace();
                }
                return convertView;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Click " + videoList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), FoodActivity.class);
//                intent.putExtra("position", position);
//                intent.putExtra("type", 0);
//                startActivity(intent);
            }
        });
    }
}