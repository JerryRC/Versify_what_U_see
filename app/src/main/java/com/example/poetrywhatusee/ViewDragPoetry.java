package com.example.poetrywhatusee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import org.jetbrains.annotations.NotNull;

public class ViewDragPoetry extends LinearLayout {

    private ViewDragHelper mDragHelper;
    private View mDragView;
    private LinearLayout autoTextView;
    private int my_top;
    int screenHeight;
    private MyScroView myScroView;

    public ViewDragPoetry(Context context) {
        super(context);
        initView();
    }

    public ViewDragPoetry(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ViewDragPoetry(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mDragHelper = ViewDragHelper.create(
                this, 1.0f, new ViewDragHelper.Callback() {
                    @Override
                    public boolean tryCaptureView(@NotNull View child, int pointerId) {
                        return mDragView == child;
                    }

                    //按压状态的处理：0和1
                    @Override
                    public void onViewDragStateChanged(int state) {
                        super.onViewDragStateChanged(state);
                    }

                    //平滑滑动的时候返回的xy：支持判断快速滑动
                    @Override
                    public void onViewReleased(@NotNull View releasedChild, float x_vel, float y_vel) {
                        super.onViewReleased(releasedChild, x_vel, y_vel);
                        if (y_vel > 0) {
                            translateScreenHeight();
                        } else if (my_top > screenHeight / 3 && my_top > 0) {
                            translateScreenHeight();
                        } else if (my_top < screenHeight / 3 && my_top > 0) {
                            translateEdge();
                        }
                        // y速度大于0的时候，收起面板
                        // 上端大于三分之一的时候收起

                    }

                    @Override
                    public void onViewPositionChanged(
                            @NotNull View changedView, int left, int top, int dx, int dy) {
                        my_top = top;
                        if (my_top == screenHeight) {
                            PoetrySelectActivity.hideView();
                        }

                    }

                    @Override
                    public void onViewCaptured(@NotNull View capturedChild, int activePointerId) {
                        super.onViewCaptured(capturedChild, activePointerId);

                    }

                    @Override
                    public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                        return Math.max(Math.min(top, screenHeight), 0);
                    }
                });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(ViewDragPoetry.this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoTextView = findViewById(R.id.myLP);
        FragEdge myBlankView = findViewById(R.id.myBlankViewP);
        myScroView = findViewById(R.id.myScroViewP);
        mDragView = autoTextView;
        myBlankView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragHelper.smoothSlideViewTo(autoTextView, 0, screenHeight);
                ViewCompat.postInvalidateOnAnimation(ViewDragPoetry.this);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
//            Log.i("iiiii viewDrag", "intercept false cancel");
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.i("iiiii viewDrag", "intercept false onTouch");
            onTouchEvent(ev);
            return false;
        }
//        if(mDragHelper.shouldInterceptTouchEvent(ev))
//            Log.i("iiiii viewDrag", "should intercept");
//        else
//            Log.i("iiiii viewDrag", "should not intercept");
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    public void getHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public void showMyMenu() {
        translateEdge();
        setShowSize();
    }

    private void translateScreenHeight() {
        mDragHelper.smoothSlideViewTo(autoTextView, 0, screenHeight);
        ViewCompat.postInvalidateOnAnimation(ViewDragPoetry.this);
    }

    private void translateEdge() {
        mDragHelper.smoothSlideViewTo(autoTextView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(ViewDragPoetry.this);
    }

    private void setShowSize() {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) myScroView.getLayoutParams();
        layoutParams.height = (screenHeight / 5) * 4;
        myScroView.setLayoutParams(layoutParams);
    }
}
