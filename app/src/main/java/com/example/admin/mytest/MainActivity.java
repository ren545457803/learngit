package com.example.admin.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SwipeBackActivity {

    static String TAG = "MainActivity";

    @Bind(R.id.test)
    TextView textView;

    @Bind(R.id.woshishi)
    View parent;

    @Bind(R.id.imageView)
    ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDragEdge(SwipeBackLayout.DragEdge.TOP);

        ButterKnife.bind(this);

        Log.e(TAG, "--onCreate");
    }


    @OnClick(R.id.click)
    void click() {
        textView.setText(NetStatus.getNetType(this) + "");

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "--onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "--onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "--onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Log.e(TAG, "---onActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
