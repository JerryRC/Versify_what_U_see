package com.example.poetrywhatusee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class ResultActivity extends AppCompatActivity {

    private Button m_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //用于隐藏应用的上边框
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //用于获取上一个activity传过来的照片地址
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //空异常
        assert bundle != null;

        String photoPath = bundle.getString("photo");
        String poemJSON = bundle.getString("poem");

        ImageView res_imv = findViewById(R.id.res_imv);
        m_btn = findViewById(R.id.btn_save);
        TextView m_tv = findViewById(R.id.tv_hor);

        //加载图片
        Glide.with(ResultActivity.this).load(photoPath).into(res_imv);

        //设置毛笔字字体
        Typeface typeface = ResourcesCompat.getFont(this, R.font.maobizi);
        m_btn.setTypeface(typeface);
        m_tv.setTypeface(typeface);

        try {
            JSONObject jsonObject = new JSONObject(poemJSON);
            String text = jsonObject.getString("txt");
            StringTokenizer st = new StringTokenizer(text, "，");
            StringBuilder text1 = new StringBuilder();
            StringBuilder text2 = new StringBuilder();
            int sum = st.countTokens();
            if (st.hasMoreTokens()) {
                text1.append(st.nextToken());
                text1.append("，");
            }
            while (st.hasMoreTokens()) {
                text2.append(st.nextToken());
                text2.append("，");
            }
            if (sum > 1) {
                text2.deleteCharAt(text2.length() - 1);
            } else {
                text1.deleteCharAt(text1.length() - 1);
            }

            String name = jsonObject.getString("name");
            String author = jsonObject.getString("author");
            String show = text1.toString() + "\n\t\t" + text2.toString()
                    + "\n\t ——" + name + " " + author;
            m_tv.setText(show);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        m_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.先隐藏按钮
                m_btn.setVisibility(View.INVISIBLE);

                //2.处理截图
                //用时间来命名文件名
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
                String photo_name = sdf.format(date) + ".jpg";

                File p_dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM) + "/Poetry");

                //按照指定的路径创建文件夹
                if (!p_dir.exists()) {
                    try {
                        if (!p_dir.mkdirs()) {
                            Toast.makeText(getApplicationContext(), "文件夹创建失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM) + "/Poetry", photo_name);
                Bitmap bitmap = screenShot(ResultActivity.this);

                try {
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            Toast.makeText(getApplicationContext(), "文件创建失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    boolean ret = save(bitmap, file, Bitmap.CompressFormat.JPEG, true);

                    if (ret) {
                        Toast.makeText(getApplicationContext(), "截图已保持至 " +
                                file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "保存失败 ",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //3.重新显示按钮
                m_btn.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 保存图片到文件File。
     *
     * @param src     源图片
     * @param file    要保存到的文件
     * @param format  格式
     * @param recycle 是否回收
     * @return true 成功 false 失败
     */
    public static boolean save(Bitmap src, File file,
                               Bitmap.CompressFormat format, boolean recycle) {

        if (isEmptyBitmap(src))
            return false;

        OutputStream os;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled())
                src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * 根据activity创建一个bitmap画布
     *
     * @param view View
     * @return Bitmap
     */
    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    /**
     * 获取当前屏幕截图，不包含状态栏（Status Bar）。
     *
     * @param activity Activity
     * @return Bitmap
     */
    public static Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Bitmap bmp = getBitmapFromView(view);

        //裁剪
        int statusBarHeight = getStatusBarHeight(activity);
        int width = (int) getDeviceDisplaySize(activity)[0];
        int height = (int) getDeviceDisplaySize(activity)[1];

        return Bitmap.createBitmap(bmp, 0,
                statusBarHeight, width, height - statusBarHeight);
    }


    public static float[] getDeviceDisplaySize(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        float[] size = new float[2];
        size[0] = width;
        size[1] = height;

        return size;
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }

        return height;
    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }


    /**
     * @param keyCode 点击的按键
     * @param event   事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
            builder.setTitle("是否返回");
            builder.setMessage("您是否要返回主页面？");
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
                    Toast.makeText(getApplicationContext(), "取消 ",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

