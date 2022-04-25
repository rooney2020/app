package com.ts.test.bean;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.ts.test.util.Common;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Music extends Audio implements Serializable {

    private String fileName = "未知";
    private String artist = "未知";
    private String title = "未知";
    private String album = "未知";
    private String year = "未知";
    private Bitmap albumImage;

    public Music() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        if (Common.isEmpty(artist)) {
            this.artist = artist;
        }
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        if (Common.isEmpty(year)) {
            this.year = year;
        }
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(Bitmap albumImage) {
        this.albumImage = albumImage;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        if (Common.isEmpty(album)) {
            this.album = album;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (Common.isEmpty(title)) {
            this.title = title;
        }
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "------------------------------------------------------------------"
                + "\nid: " + id
                + "\nfilePath: " + filePath
                + "\ntitle: " + title
                + "\nartist: " + artist
                + "\nalbum: " + album
                + "\nalbumImage: " + (albumImage == null ? "null" : "not null")
                + "\n------------------------------------------------------------------";
    }
}
