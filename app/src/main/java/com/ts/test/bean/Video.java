package com.ts.test.bean;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class Video extends Audio {
    private String fileName;

    public Video(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "fileName: " + fileName;
    }
}
