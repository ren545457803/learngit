package com.liuguangqiang.swipeback;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * Created by Eric on 15/3/3.
 */
public class SwipeBackActivity extends Activity implements SwipeBackLayout.SwipeBackListener {

    private SwipeBackLayout swipeBackLayout;
    private ImageView ivShadow;
    private View rawView;// 原始的布局

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        rawView = LayoutInflater.from(this).inflate(layoutResID, null);
        swipeBackLayout.addView(rawView);
    }

    /**
     * 设置原始View的背景，不需要在布局里设置了:android:background
     * @param colorResId 颜色资源ID
     */
    protected void setRawBackground(int colorResId){
        rawView.setBackgroundResource(colorResId);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.setOnSwipeBackListener(this);
        ivShadow = new ImageView(this);
        ivShadow.setBackgroundColor(getResources().getColor(R.color.black_p50));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        container.addView(ivShadow, params);
        container.addView(swipeBackLayout);
        return container;
    }

    public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
        swipeBackLayout.setDragEdge(dragEdge);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        ivShadow.setAlpha(1 - fractionScreen);
    }

}
