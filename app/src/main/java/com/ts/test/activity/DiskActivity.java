package com.ts.test.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ts.test.R;
import com.ts.test.bean.Music;
import com.ts.test.bean.Video;
import com.ts.test.fragment.MusicListFragment;
import com.ts.test.fragment.VideoListFragment;
import com.ts.test.services.MusicService;
import com.ts.test.util.Common;
import com.ts.test.util.MusicFileUtil;
import com.google.android.material.tabs.TabLayout;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ts.test.util.Common.EMPTY_STRING;
import static com.ts.test.util.Common.MUSIC_TYPES;
import static com.ts.test.util.Common.VIDEO_TYPES;

public class DiskActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DiskActivity_info";

    private TabLayout mTlTab;
    private ViewPager viewPager;

    private Button play, last, next;
    private TextView tvTitle, tvSinger;
    private ProgressBar progressBar;

    private List<Music> musicList = new ArrayList<>();
    private List<Video> videoList = new ArrayList<>();
    private File storageDirectory;
    private Object lock = new Object();
    private Intent musicServiceIntent;
    private MusicService.MusicBind musicBinder;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBind) service;
            Toast.makeText(DiskActivity.this, "hello", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onServiceConnected: binder != null");
            musicBinder.setMusicLis(musicList);
            musicBinder.setPlayView(play, tvTitle, tvSinger, progressBar);
            musicBinder.setPlayViewStatus();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk);

        initView();
    }

    /**
     * 初始化视图
     */
    protected void initView() {
        initNavBar(true, Common.TITLE_DISK, true);
        initDiskSearchBar();
        bindView();

        musicServiceIntent = new Intent(this, MusicService.class);
        this.startService(musicServiceIntent);
        this.bindService(musicServiceIntent, conn, Context.BIND_AUTO_CREATE);

        storageDirectory = Environment.getExternalStorageDirectory();

        new Thread(new Runnable() {
            @Override
            public void run() {
                filterDirectory(storageDirectory);
            }
        }).start();

    }

    /**
     * 绑定视图
     */
    protected void bindView() {
        // 绑定控件
        mTlTab = findViewById(R.id.mTlTab);
        viewPager = findViewById(R.id.vp);
        tvTitle = findViewById(R.id.tv_title);
        tvSinger = findViewById(R.id.tv_singer);
        progressBar = findViewById(R.id.progress_music);
        play = (Button) findViewById(R.id.btn_play);
        last = (Button) findViewById(R.id.btn_last);
        next = (Button) findViewById(R.id.btn_next);

        // 设置默认文本及状态
        tvTitle.setText("暂无播放");
        tvSinger.setText("");
        progressBar.setProgress(0);
        // 按钮添加点击事件
        play.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);

        // 绑定tabLayout和viewPager
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return Common.TAB_TITLES[position];
            }

            @Override
            public int getCount() {
                return Common.TAB_TITLES.length;
            }

            @NonNull
            @NotNull
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position) {
                    case 0:
                        fragment = new MusicListFragment(new MusicListFragment.Callback() {
                            @Override
                            public void onClickMusic(int position) {
                                playMusic(position);
                            }

                            @Override
                            public List<Music> getMusicList() {
                                return musicList;
                            }
                        }, musicList);
                        break;
                    default:
                        fragment = new VideoListFragment();
                }
                return fragment;
            }
        });
        mTlTab.setupWithViewPager(viewPager);
    }

    /**
     * 点击事件
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                trigger();
                break;
            case R.id.btn_last:
                if (musicBinder.getCurrentPosition() != -1) {
                    musicBinder.triggerMusic(true);
                    playMusic();
                } else {
                    Toast.makeText(DiskActivity.this, "暂无播放内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next:
                if (musicBinder.getCurrentPosition() != -1) {
                    musicBinder.triggerMusic(false);
                    playMusic();
                } else {
                    Toast.makeText(DiskActivity.this, "暂无播放内容", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 切换播放状态
     */
    private void trigger() {
        if (musicBinder != null && musicBinder.getPlayingStatus()) {
            stopMusic();
        } else {
            if (musicBinder.getCurrentPosition() != -1) {
                playMusic();
            } else {
                Toast.makeText(DiskActivity.this, "暂无播放内容", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 播放音乐
     */
    public void playMusic() {
        play.setBackgroundResource(R.mipmap.ic_udisk_suspendedbutton);
        musicBinder.playMusic();
    }

    /**
     * 播放音乐
     */
    public void playMusic(int position) {
        play.setBackgroundResource(R.mipmap.ic_udisk_suspendedbutton);
        musicBinder.setMusic(position);
        musicBinder.playMusic();
    }

    /**
     * 停止播放
     */
    public void stopMusic() {
        play.setBackgroundResource(R.mipmap.ic_udisk_playbutton);
        musicBinder.stopMusic();
    }

    /**
     * 扫描文件
     *
     * @param file 文件
     * @return
     */
    public void filterDirectory(File file) {
        if (storageDirectory == file) {
            Log.e(TAG, "filterDirectory: 开始");
        }
        Log.e(TAG, file.getAbsolutePath());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File temp : files) {
                    filterDirectory(temp);
                }
            }
        } else {
            switch (isAudioFile(file.getName())) {
                case 0:
                    Music music = new Music();
                    music.setFilePath(file.getAbsolutePath());
                    music.setFileName(file.getName());
                    music = setID3Info(music);
                    musicList.add(music);
                    Intent intent = new Intent("com.thundersoft.test.musiclistfragment");
//                    Intent intent2 = new Intent("com.thundersoft.test.musiclistservice");
//                    intent2.putExtra("music", music);
                    sendBroadcast(intent);
//                    sendBroadcast(intent2);
                    while (musicBinder == null) {

                    }
                    musicBinder.setMusicLis(musicList);
                    break;
                case 1:
                    Video video = new Video("");
                    video.setFilePath(file.getAbsolutePath());
                    videoList.add(video);
                    break;
                default:
                    break;
            }
        }
    }

    public int isAudioFile(String fileName) {
        String[] strArray = fileName.split("\\.");
        if (EMPTY_STRING.equals(fileName) || !fileName.contains(".")) {
            return -1;
        }
        String extraType = strArray[strArray.length - 1].toLowerCase();
        if (MUSIC_TYPES.contains(extraType)) {
            return 0;
        } else if (VIDEO_TYPES.contains(extraType)) {
            return 1;
        }
        return -1;
    }

    public Music setID3Info(Music music) {
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(music.getFilePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            music.setArtist(id3v1Tag.getArtist());
            music.setTitle(id3v1Tag.getTitle());
            music.setAlbum(id3v1Tag.getAlbum());
            music.setYear(id3v1Tag.getYear());
            music.setAlbumImage(null);
        }
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            music.setArtist(id3v2Tag.getArtist());
            music.setTitle(id3v2Tag.getTitle());
            music.setAlbum(id3v2Tag.getAlbum());
            music.setYear(id3v2Tag.getYear());
            Bitmap bitmap = MusicFileUtil.getPicFromBytes(id3v2Tag.getAlbumImage(), new BitmapFactory.Options());
            music.setAlbumImage(bitmap);
        }
        return music;
    }
}