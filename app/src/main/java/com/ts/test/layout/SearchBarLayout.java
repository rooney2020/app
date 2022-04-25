package com.ts.test.layout;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.ts.test.R;

import org.jetbrains.annotations.NotNull;

public class SearchBarLayout extends CardView {
    public SearchBarLayout(@NonNull @NotNull Context context) {
        super(context);
        // 对布局进行动态加载
        LayoutInflater.from(context).inflate(R.layout.search_bar, this);
    }
}
