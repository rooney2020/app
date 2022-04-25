package com.ts.test.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ts.test.R;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity_info";

    private TextView tvContent;

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            } else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return null;
    }

    private void bytesToImageFile(byte[] bytes, String fileName) {
        try {
            File file = new File(getFilesDir().getAbsolutePath() + "/image/" + fileName + ".jpg");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initView();
    }

    public void initView() {
        tvContent = findViewById(R.id.tv_content);

//        checkPermission();
//        File file = new File("/storage/emulated/0/netease/cloudmusic/Music/汪峰 - 飞得更高(Live).mp3");
//        System.out.println(file.getName());
////        testID3(file.getAbsolutePath());
//        Mp3File mp3File = null;
//        try {
//            mp3File = new Mp3File(file.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UnsupportedTagException e) {
//            e.printStackTrace();
//        } catch (InvalidDataException e) {
//            e.printStackTrace();
//        }
//        if (mp3File!=null) {
//            byte[] albumImage = mp3File.getId3v2Tag().getAlbumImage();
//            ((ImageView) findViewById(R.id.image)).setImageBitmap(getPicFromBytes(albumImage, new BitmapFactory.Options()));
//        }
        File storageDirectory = Environment.getExternalStorageDirectory();
        List<File> files = filterDirectory(storageDirectory);
        for (File file : files) {
            Log.e(TAG, file.getAbsolutePath());
            testID3(file.getAbsolutePath());
        }
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 300) {
            Log.i("PERMISSION_CHECK", "--------------requestCode == 300->" + requestCode + "," + permissions.length + "," + grantResults.length);
        } else {
            Log.i("PERMISSION_CHECK", "--------------requestCode != 300->" + requestCode + "," + permissions + "," + grantResults);
        }
    }

    public List<File> filterDirectory(File file) {
        List<File> fileArr = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File temp : files) {
                    fileArr.addAll(filterDirectory(temp));
                }
            }
        } else {
            if (file.getName().endsWith(".mp3")) {
                fileArr.add(file);
            }
        }
        return fileArr;
    }

    public void testID3(String fileName) {
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            System.out.println("Track: " + id3v1Tag.getTrack());
            System.out.println("Artist: " + id3v1Tag.getArtist());
            System.out.println("Title: " + id3v1Tag.getTitle());
            System.out.println("Album: " + id3v1Tag.getAlbum());
            System.out.println("Year: " + id3v1Tag.getYear());
            System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
            System.out.println("Comment: " + id3v1Tag.getComment());
        }
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            System.out.println("Track: " + id3v2Tag.getTrack());
            System.out.println("Artist: " + id3v2Tag.getArtist());
            System.out.println("Title: " + id3v2Tag.getTitle());
            System.out.println("Album: " + id3v2Tag.getAlbum());
            System.out.println("Year: " + id3v2Tag.getYear());
            System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
            System.out.println("Comment: " + id3v2Tag.getComment());
            System.out.println("Version: " + id3v2Tag.getVersion());
            byte[] imageData = id3v2Tag.getAlbumImage();
//            bytesToImageFile(imageData, new);
        }
    }
}