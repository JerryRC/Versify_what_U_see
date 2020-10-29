package com.example.poetrywhatusee;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;


public class MyScroView extends NestedScrollView {
    public boolean myScroSign = false;

    public MyScroView(Context context) {
        this(context, null);
    }

    public MyScroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mo_lastX;
    private int mo_lastY;
    private float lastY;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        int x = (int) ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:

                int moveX = x - mo_lastX;
                int moveY = y - mo_lastY;

                if (Math.abs(moveX) > Math.abs(moveY)) {
                    //横向移动时，直接禁止父layout拦截触控事件，以此使得recyclerview可以横向滚动
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                    Log.i("iiiiiScroView", "dispatch true");
                } else if (lastY > ev.getY()) {
                    if (!canScrollVertically(1)) {
                        getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                        Log.i("iiiiiScroView", "dispatch false");
                        return false;
                    } else {
                        getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                        Log.i("iiiiiScroView", "dispatch true");
                    }
                } else if (ev.getY() > lastY) {
                    if (!canScrollVertically(-1)) {
                        getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                        Log.i("iiiiiScroView", "dispatch false");
                        return false;
                    } else {
                        getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                        Log.i("iiiiiScroView", "dispatch true");
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mo_lastX = x;
                mo_lastY = y;
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                Log.i("iiiiiScroView", "dispatch Down");
                break;
        }
        lastY = ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (lastY > ev.getY()) {
            return myScroSign;
        } else if (ev.getY() > lastY) {
            return myScroSign;
        }

        return super.onInterceptTouchEvent(ev);
    }

}

