package com.ts.test.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ts.test.R;

public class NavBarLayout extends LinearLayout {

    public NavBarLayout(Context context) {
        super(context);
        // 对布局进行动态加载
        LayoutInflater.from(context).inflate(R.layout.nav_bar, this);
    }


}
