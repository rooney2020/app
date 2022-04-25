package com.ts.test.activity;

import android.os.Bundle;

import com.ts.test.R;
import com.ts.test.util.Common;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    protected void initView() {
        initNavBar(true, Common.TITLE_DISK, true);
    }
}