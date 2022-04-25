package com.ts.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.ts.test.R;
import com.ts.test.util.ActivityCollector;

import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    private Button titleBack, usb1, usb2;
    private TextView mTvTitle;
    private ImageView searchIcon;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * findViewById
     *
     * @param id  资源id
     * @param <T> 真实类型
     * @return
     */
    protected <T extends View> T fd(@IdRes int id) {
        return findViewById(id);
    }

    /**
     * 初始化 Navigation
     *
     * @param isShowBack 是否显示返回按钮
     * @param title      标题
     * @param isShowUsb  是否显示usb图标
     */
    protected void initNavBar(boolean isShowBack, String title, boolean isShowUsb) {
        titleBack = fd(R.id.title_back);
        usb1 = fd(R.id.usb1);
        usb2 = fd(R.id.usb2);
        mTvTitle = fd(R.id.title);
        titleBack.setVisibility(isShowBack ? View.VISIBLE : View.GONE);
        usb1.setVisibility(isShowUsb ? View.VISIBLE : View.GONE);
        usb2.setVisibility(isShowUsb ? View.VISIBLE : View.GONE);
        mTvTitle.setText(title);

        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void initDiskSearchBar() {
        searchIcon = fd(R.id.search_icon);
        searchInput = fd(R.id.search_input);
        searchInput.setFocusable(false);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toIntent(SearchActivity.class, false);
            }
        });
        searchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toIntent(SearchActivity.class, false);
            }
        });
    }

    /**
     * 页面跳转
     *
     * @param cls      目标
     * @param isFinish 是否关闭当前Activity
     */
    protected void toIntent(Class cls, boolean isFinish) {
        startActivity(new Intent(this, cls));
        if (isFinish) {
            finish();
        }
    }

    /**
     * 延迟delay毫秒后执行任务
     *
     * @param task  任务
     * @param delay 毫秒
     */
    protected void sleep(TimerTask task, int delay) {
        Timer timer = new Timer();
        timer.schedule(task, delay);
    }

    public void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
