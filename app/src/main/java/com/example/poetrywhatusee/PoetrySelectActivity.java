package com.example.poetrywhatusee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class PoetrySelectActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static ViewDragPoetry myDrawViewP;
    private String photoPath;
    private ArrayList<String> poems;
    private String serverPhotoName;
    private int poemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poetry_select);

        //用于隐藏应用的上边框
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //用于获取上一个activity传过来的照片地址
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //空异常
        assert bundle != null;

        //上个activity的参数转储
        photoPath = bundle.getString("photo");
        poems = bundle.getStringArrayList("resultList");
        serverPhotoName = bundle.getString("serverPhoto");   //服务器照片名，暂时无用
        poemNum = 0;    //第0首古诗

        assert poems != null;

        //控件声明
        myDrawViewP = findViewById(R.id.myCustomP);
        ImageView m_imv = findViewById(R.id.select_imvP);

        TextView m_tv = findViewById(R.id.tv_chooseP);

        final RecyclerView m_rv = findViewById(R.id.rv_buttonP);

        Button btn_confer = findViewById(R.id.btn_conferP);
        Button m_btn = findViewById(R.id.myMenuP);

        //加载图片
        Glide.with(PoetrySelectActivity.this).load(photoPath).into(m_imv);

        //设置毛笔字字体
        Typeface typeface = ResourcesCompat.getFont(this, R.font.maobizi);

        m_tv.setTypeface(typeface);
        btn_confer.setTypeface(typeface);
        m_btn.setTypeface(typeface);

        //获取屏幕高度
        getHeight();

        //展示选项框
        m_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDrawViewP.setVisibility(View.VISIBLE);
                myDrawViewP.showMyMenu();
            }
        });

        //RecyclerView 视图 摆放识别结果
        LinearLayoutManager LayoutManager = new LinearLayoutManager(
                PoetrySelectActivity.this);
        LayoutManager.setOrientation(RecyclerView.VERTICAL);
        m_rv.setLayoutManager(LayoutManager);
        m_rv.setAdapter(new poemRecyclerAdapter(PoetrySelectActivity.this, poems,
                new poemRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(int pos) {
                        //通过找到上一个点击的checkbox并取消它的选中，来制造单选的表现
                        View itemView = Objects.requireNonNull(
                                m_rv.getLayoutManager()).findViewByPosition(poemNum);

                        assert itemView != null;
                        CheckBox cbPre = itemView.findViewById(R.id.cb_in_gridP);

                        assert cbPre != null;
                        if (poemNum == pos) {
                            //如果上一个就是现在这个pos，那么就保持不变（也就是避免checkbox再点一次取消选择的功能）
                            cbPre.setChecked(true);
                        } else {
                            cbPre.setChecked(false);
                        }
                        poemNum = pos;
                    }
                }));

        //完成选择 开始匹配并跳转
        btn_confer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String url = "https://www.rainbowstu.cn/processing.php";    //处理意象匹配古诗的地址
                Intent intent = new Intent(
                        PoetrySelectActivity.this, ResultActivity.class);
                intent.putExtra("photo", photoPath);
                intent.putExtra("serverPhoto", serverPhotoName);
                intent.putExtra("poem", poems.get(poemNum));
                startActivity(intent);
                finish();   //结束这个选择的activity
            }
        });

    }


    public static void hideView() {
        myDrawViewP.setVisibility(View.GONE);
    }

    /**
     * 这里覆写返回键，主要把收回选择栏区分开来
     *
     * @param keyCode 点击的按键
     * @param event   事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (myDrawViewP.getVisibility() != View.GONE) {
                hideView();
            } else {
                //弹出选择框
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        PoetrySelectActivity.this);
                builder.setTitle("是否返回");
                builder.setMessage("您是否要放弃当前进度，返回主页面？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //结束当前activity 返回主页面
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //暂无
                    }
                });
                builder.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getHeight() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        myDrawViewP.getHeight(screenHeight);
    }
}