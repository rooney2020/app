package com.ts.test.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ts.test.R;
import com.ts.test.activity.DiskActivity;
import com.ts.test.bean.Music;
import com.ts.test.util.MusicPlayerManager;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    // 通知id，不可为 0
    public static final int NOTIFICATION_ID = 1;
    private MusicPlayerManager musicPlayerManager;
    private List<Music> musicList;
    private int currentPosition = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicService.MusicBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayerManager = MusicPlayerManager.getInstance(this);
        musicList = new ArrayList<>();
    }

    /**
     * 设置服务在前台可见
     */
    private void startForeground() {
        // 通知栏点击跳转的intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, DiskActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);

        // 创建Notification
        Notification notification = null;
        // android API 26 以上 NotificationChannel 特性适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = createNotificationChannel();
            notification = new Notification.Builder(this, channel.getId())
                    .setContentTitle(musicList.get(currentPosition).getTitle())
                    .setContentText(musicList.get(currentPosition).getArtist())
                    .setSmallIcon(R.mipmap.ic_udisk_playbutton)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(musicList.get(currentPosition).getTitle())
                    .setContentText(musicList.get(currentPosition).getArtist())
                    .setSmallIcon(R.mipmap.ic_udisk_playbutton)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .build();
        }

        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * 创建通知channel
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChannel() {
        String channelId = "super";
        String channelName = "superMusicService";
        String Description = "superMusic";
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(Description);

        return channel;
    }

    /**
     * Binder
     */
    public class MusicBind extends Binder {

        private Button btnPlay;
        private TextView tvTitle, tvSinger;
        private ProgressBar progressBar;
        //处理进度条更新
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //更新进度
                        int position = musicPlayerManager.getCurrentPosition();
                        int time = musicPlayerManager.getDuration();
                        int max = progressBar.getMax();
                        int progress = 0;
                        if (time != 0) {
                            progress = position * max / time;
                        }
                        progressBar.setProgress(progress);
                        break;
                    default:
                        break;
                }
            }
        };

        public MusicBind() {
            final int milliseconds = 100;
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sleep(milliseconds);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mHandler.sendEmptyMessage(0);
                    }
                }
            }.start();
        }

        public List<Music> getMusicList() {
            return musicList;
        }

        public void setMusicLis(List<Music> list) {
            musicList = list;
        }

        public void setMusic(int position) {
            currentPosition = position;
            startForeground();
        }

        public void setPlayView(Button view, TextView tvTitle, TextView tvSinger, ProgressBar progressBar) {
            btnPlay = view;
            this.tvTitle = tvTitle;
            this.tvSinger = tvSinger;
            this.progressBar = progressBar;
        }

        public void setPlayViewStatus() {
            if (musicPlayerManager.getPlayingStatus()) {
                btnPlay.setBackgroundResource(R.mipmap.ic_udisk_suspendedbutton);
            }
            if (currentPosition != -1) {
                tvTitle.setText(musicList.get(currentPosition).getTitle());
                tvSinger.setText(musicList.get(currentPosition).getArtist());
            }
        }

        public void setPlayViewText() {
            btnPlay.setBackgroundResource(R.mipmap.ic_udisk_suspendedbutton);
            tvTitle.setText(musicList.get(currentPosition).getTitle());
            tvSinger.setText(musicList.get(currentPosition).getArtist());
        }

        /**
         * @param order true上一首，false下一首
         * @return
         */
        public void triggerMusic(boolean order) {
            if (order) {
                if (currentPosition - 1 < 0) {
                    currentPosition = musicList.size() - 1;
                } else {
                    currentPosition--;
                }
            } else {
                if (currentPosition + 1 >= musicList.size()) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
            }
            Log.i("TAG_SERVICE", "last: " + musicList.get(currentPosition).getId());
        }

        /**
         * 播放音乐
         */
        public void playMusic() {
            if (musicPlayerManager.getMusic() != null && musicPlayerManager.getMusic() == musicList.get(currentPosition)) {
                musicPlayerManager.start();
                setPlayViewText();
            } else {
                startForeground();
                musicPlayerManager.setMusic(musicList.get(currentPosition));
                setPlayViewText();
                musicPlayerManager.setOnMediaPlayerHelperListener(new MusicPlayerManager.OnMediaPlayerHelperListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        musicPlayerManager.start();
                    }

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        triggerMusic(false);
                        playMusic();
                    }

                    @Override
                    public void onError(MediaPlayer mp) {
                    }
                });
            }
        }

        /**
         * 暂停播放
         */
        public void stopMusic() {
            musicPlayerManager.pause();
        }

        public boolean getPlayingStatus() {
            return musicPlayerManager.getPlayingStatus();
        }

        public int getCurrentPosition() {
            return currentPosition;
        }
    }

    class MusicListServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicList.add((Music) intent.getSerializableExtra("music"));
        }
    }
}
