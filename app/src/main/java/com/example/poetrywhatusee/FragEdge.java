package com.example.poetrywhatusee;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class FragEdge extends FrameLayout {

    public FragEdge(@NonNull Context context) {
        super(context);
    }

    public FragEdge(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FragEdge(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * FragEdge的主要作用是在点击菜单外部的时候将菜单收回。
     * 实现方式是通过内部拦截将点击事件和滑动事件分开处理。
     */
    private int my_lastX;
    private int my_lastY;

    public boolean dispatchTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        int x = (int) ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int moveX = x - my_lastX;
                int moveY = y - my_lastY;
                if (Math.abs(moveX) > Math.abs(moveY)) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                    Log.i("iiiiiFragEdge_dispatch", "true");
                } else {
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                    Log.i("iiiiiFragEdge_dispatch", "false");
                }

                break;
            case MotionEvent.ACTION_DOWN:
                my_lastX = x;
                my_lastY = y;
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                Log.i("iiiiiFragEdge_dispatch", "Down");
                break;

        }
        return super.dispatchTouchEvent(ev);
    }
}

