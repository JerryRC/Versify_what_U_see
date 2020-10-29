package com.example.poetrywhatusee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;


public class MyViewDragHelper extends LinearLayout {
    private ViewDragHelper mDragHelper;
    private View mDragView;
    private LinearLayout autoTextView;
    //    private int my_left;
    private int my_top;
    //    int screenWidth;
    int screenHeight;
    private MyScroView myScroView;

    public MyViewDragHelper(Context context) {
        super(context);
        initView();
    }

    public MyViewDragHelper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyViewDragHelper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
//                if (x_vel > 0) {
//                    translateScreenWidth();
//                } else if (my_left > screenWidth / 3 && my_left > 0) {
//                    translateScreenWidth();
//                } else if (my_left < screenWidth / 3 && my_left > 0) {
//                    translateEdge();
//                }
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
                    public void onViewPositionChanged(@NotNull View changedView,
                                                      int left, int top, int dx, int dy) {
//                my_left = left;
//                Log.e("距离", top + "");
//                if (my_left == screenWidth) {
//                    SelectActivity.hideView();
//                }
                        my_top = top;
                        if (my_top == screenHeight) {
                            SelectActivity.hideView();
                        }
                    }

                    @Override
                    public void onViewCaptured(@NotNull View capturedChild, int activePointerId) {
                        super.onViewCaptured(capturedChild, activePointerId);

                    }

//            @Override
//            public int clampViewPositionHorizontal(View child, int left, int dx) {
//                return Math.max(Math.min(left, screenWidth), 0);
//            }

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
            ViewCompat.postInvalidateOnAnimation(MyViewDragHelper.this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoTextView = findViewById(R.id.myLl);
        FragEdge myBlankView = findViewById(R.id.myBlankView);
        myScroView = findViewById(R.id.myScroView);
        mDragView = autoTextView;
        myBlankView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDragHelper.smoothSlideViewTo(autoTextView, screenWidth, 0);
                mDragHelper.smoothSlideViewTo(autoTextView, 0, screenHeight);
                ViewCompat.postInvalidateOnAnimation(MyViewDragHelper.this);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.i("iiiiiViewDragHelper", "Intercept");
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchEvent(ev);
            return false;
        }
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
//        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void showMyMenu() {
        translateEdge();
        setShowSize();
    }

    private void translateScreenHeight() {
//        mDragHelper.smoothSlideViewTo(autoTextView, screenWidth, 0);
        mDragHelper.smoothSlideViewTo(autoTextView, 0, screenHeight);
        ViewCompat.postInvalidateOnAnimation(MyViewDragHelper.this);
    }

    private void translateEdge() {
        mDragHelper.smoothSlideViewTo(autoTextView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(MyViewDragHelper.this);
    }

    private void setShowSize() {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) myScroView.getLayoutParams();
//        layoutParams.width = (screenWidth / 4) * 3;
        layoutParams.height = (screenHeight / 25) * 14;
        myScroView.setLayoutParams(layoutParams);
    }

}



