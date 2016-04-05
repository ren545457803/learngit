package com.example.admin.mytest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class Main2Activity extends SwipeBackActivity {
    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 在sdk>=19中，进行系统状态栏和应用标题栏，融为一体变身
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);

            // set a custom tint color for all system bars
            tintManager.setStatusBarTintResource(R.color.main);
// set a custom navigation bar resource
//        tintManager.setNavigationBarTintResource(R.mipmap.ic_launcher);
// set a custom status bar drawable
//        tintManager.setStatusBarTintDrawable(MyDrawable);
        }

        setContentView(R.layout.activity_main2);

        setRawBackground(android.R.color.white);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        Log.e(TAG, "Main2Activity--onStart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "Main2Activity--onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Main2Activity--onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Main2Activity--onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"Main2Activity---onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Log.e(TAG, "Main2Activity---onActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
