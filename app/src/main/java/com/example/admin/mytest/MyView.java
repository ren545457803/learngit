package com.example.admin.mytest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by admin on 2016-01-16.
 */
public class MyView extends View {
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    int mLastX,mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        Log.d("--","--rawX:"+x+" rawY:"+y);
        Log.d("--","--getLeft:"+getLeft()+" getRight:"+getRight());
        Log.d("--","--x:"+getX()+" y:"+getY());
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:

                int deltaX = x - mLastX;
                int deltaY = y - mLastY;

                Log.d("--","move:deltaX:"+deltaX+" deltaY:"+deltaY);
                int translationX = (int) (getTranslationX() + deltaX);
                int translationY = (int) (getTranslationY() + deltaY);

                setTranslationX(translationX);
                setTranslationY(translationY);
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }
}
