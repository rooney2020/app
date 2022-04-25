package com.ts.test.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ts.test.bean.Music;

import java.io.IOException;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private MusicPlayerManager.OnMediaPlayerHelperListener onMediaPlayerHelperListener;

    private Music music;

    private MusicPlayerManager(Context context) {
        mContext = context;
        mediaPlayer = new MediaPlayer();
    }

    /**
     * 单例
     *
     * @param context context
     * @return
     */
    public static MusicPlayerManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicPlayerManager.class) {
                if (instance == null) {
                    instance = new MusicPlayerManager(context);
                }
            }
        }

        return instance;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void setOnMediaPlayerHelperListener(MusicPlayerManager.OnMediaPlayerHelperListener onMediaPlayerHelperListener) {
        this.onMediaPlayerHelperListener = onMediaPlayerHelperListener;
    }

    public boolean getPlayingStatus() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 返回正在播放的音乐路径
     */
    public Music getMusic() {
        return music;
    }

    /**
     * 当前需要播放的音乐
     */
    public void setMusic(Music aim) {

        // 1、音乐正在播放，重置音乐播放状态
        if (mediaPlayer.isPlaying() || music != aim) {
            mediaPlayer.reset();
        }
        music = aim;

        // 2、设置播放音乐路径
        try {
             mediaPlayer.setDataSource(mContext, Uri.parse(aim.getFilePath()));
//            AssetFileDescriptor file = mContext.getAssets().openFd(aim.getFileName());
            // 指定音频文件的路径
//            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3、准备播放
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (onMediaPlayerHelperListener != null) {
                    onMediaPlayerHelperListener.onPrepared(mp);
                }
            }
        });

        // 监听音乐播放完成
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (onMediaPlayerHelperListener != null) {
                    onMediaPlayerHelperListener.onCompletion(mp);
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (onMediaPlayerHelperListener != null) {
                    onMediaPlayerHelperListener.onError(mp);
                }
                return true;
            }
        });
    }

    /**
     * 播放音乐
     */
    public void start() {
        if (mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.start();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mediaPlayer.pause();
    }

    public interface OnMediaPlayerHelperListener {
        void onPrepared(MediaPlayer mp);

        void onCompletion(MediaPlayer mp);

        void onError(MediaPlayer mp);
    }
}
