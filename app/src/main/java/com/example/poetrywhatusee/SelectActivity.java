package com.example.poetrywhatusee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SelectActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static MyViewDragHelper myDrawView;
    private String serverPhotoName;
    private String photoPath;
    private CheckBox m_cb_m1;
    private CheckBox m_cb_m2;
    private CheckBox m_cb_w1;
    private CheckBox m_cb_w2;
    private CheckBox m_cb_w3;
    private CheckBox m_cb_w4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //用于隐藏应用的上边框
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //用于获取上一个activity传过来的照片地址
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;  //空异常

        //上个activity的参数转储
        photoPath = bundle.getString("photo");
        final ArrayList<String> resultList;
        final ArrayList<String> resultListPoint;
        resultList = bundle.getStringArrayList("resultList");
        resultListPoint = bundle.getStringArrayList("resultListPoint");
        serverPhotoName = bundle.getString("serverPhoto");

        //控件声明
        myDrawView = findViewById(R.id.myCustom);
        ImageView m_imv = findViewById(R.id.select_imv);

        TextView m_tv_match = findViewById(R.id.tv_match);
        RecyclerView m_rv = findViewById(R.id.rv_button);

        TextView m_tv_choose = findViewById(R.id.tv_choose);
        m_cb_m1 = findViewById(R.id.cb_m1);
        m_cb_m2 = findViewById(R.id.cb_m2);
        m_cb_w1 = findViewById(R.id.cb_w1);
        m_cb_w2 = findViewById(R.id.cb_w2);
        m_cb_w3 = findViewById(R.id.cb_w3);
        m_cb_w4 = findViewById(R.id.cb_w4);

        Button btn_confer = findViewById(R.id.btn_confer);
        Button m_btn = findViewById(R.id.myMenu);

        //加载图片
        Glide.with(SelectActivity.this).load(photoPath).into(m_imv);

        //设置毛笔字字体
        Typeface typeface = ResourcesCompat.getFont(this, R.font.maobizi);
        m_tv_match.setTypeface(typeface);
        m_tv_choose.setTypeface(typeface);

        m_cb_m1.setTypeface(typeface);
        m_cb_m2.setTypeface(typeface);

        m_cb_w1.setTypeface(typeface);
        m_cb_w2.setTypeface(typeface);
        m_cb_w3.setTypeface(typeface);

        m_cb_w4.setTypeface(typeface);
        btn_confer.setTypeface(typeface);
        m_btn.setTypeface(typeface);

        //获取屏幕高度
        getHeight();

        //展示选项框
        m_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDrawView.setVisibility(View.VISIBLE);
                myDrawView.showMyMenu();
            }
        });

        //RecyclerView 视图 摆放识别结果
        LinearLayoutManager LayoutManager = new LinearLayoutManager(SelectActivity.this);
        LayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        m_rv.setLayoutManager(LayoutManager);
        m_rv.setAdapter(new keywordRecyclerAdapter(SelectActivity.this, resultList));

        //完成选择 开始匹配并跳转
        btn_confer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert resultList != null;
                assert resultListPoint != null;

                //创建json格式数组
                StringBuilder jsonStr = new StringBuilder("[");
                for (int i = 0; i < resultList.size(); i++) {
                    String tmp = "[" + resultList.get(i) + "," +
                            Double.parseDouble(resultListPoint.get(i)) + "],";
                    jsonStr.append(tmp);
                }

                double maxP = 0.1;
                for (int i = 0; i < resultListPoint.size(); i++) {
                    if (Double.parseDouble(resultListPoint.get(i)) > maxP) {
                        maxP = Double.parseDouble(resultListPoint.get(i));
                    }
                }

                if (m_cb_m1.isChecked()) {
                    String tmp = "[乐观," + maxP + "],";
                    jsonStr.append(tmp);
                }
                if (m_cb_m2.isChecked()) {
                    String tmp = "[悲观," + maxP + "],";
                    jsonStr.append(tmp);
                }
                if (m_cb_w1.isChecked()) {
                    String tmp = "[晴," + maxP + "],";
                    jsonStr.append(tmp);
                }
                if (m_cb_w2.isChecked()) {
                    String tmp = "[雨," + maxP + "],";
                    jsonStr.append(tmp);
                }
                if (m_cb_w3.isChecked()) {
                    String tmp = "[多云," + maxP + "],";
                    jsonStr.append(tmp);
                }
                if (m_cb_w4.isChecked()) {
                    String tmp = "[月夜," + maxP + "],";
                    jsonStr.append(tmp);
                }

                //不只有 [ 时，去掉最后一个逗号
                if (jsonStr.length() != 1) {
                    jsonStr.deleteCharAt(jsonStr.length() - 1);
                }
                jsonStr.append("]");

                try {
                    //处理意象匹配古诗的地址
                    String url = "https://www.rainbowstu.cn/processing.php";
                    //转换成json数组
                    JSONArray jsArr = new JSONArray(jsonStr.toString());
//                    Log.i("iiiii JSONException", jsArr.toString());

                    RequestParams params = new RequestParams();
                    params.put("submit", "get");    //自定义的使php处理图片的必要参数
                    params.put("poetry", jsArr);

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(
                                int statusCode, Header[] headers, byte[] responseBody) {
                            String jsonStr = new String(responseBody, StandardCharsets.UTF_8);
//                            if(jsonStr.isEmpty())
//                                Log.i("iiiii", "000");
                            Log.i("iiiii poetry", jsonStr);

                            //把json传过来的匹配出的古诗存到数组，以便传递
//                            ArrayList<JSONObject> poems = new ArrayList<>();
                            ArrayList<String> poems = new ArrayList<>();

                            //开始做Json解析
                            try {
                                JSONArray poetryArr = new JSONArray(jsonStr);
                                for (int i = 0; i < poetryArr.length(); i++) {
//                                    JSONObject jsonObject = poetryArr.getJSONObject(i);
                                    String jsonObject = poetryArr.getString(i);
                                    poems.add(jsonObject);
                                    Log.i("iiiii json obj", jsonObject);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(SelectActivity.this, "古诗获取成功!",
                                    Toast.LENGTH_SHORT).show();

                            //把图片地址传到古诗选择activity显示
                            Intent intent = new Intent(SelectActivity.this,
                                    PoetrySelectActivity.class);
                            intent.putExtra("photo", photoPath);
                            intent.putExtra("serverPhoto", serverPhotoName);
                            intent.putExtra("resultList", poems);
                            startActivity(intent);
                            finish();   //结束这个选择的activity

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              byte[] responseBody, Throwable error) {
                            Toast.makeText(SelectActivity.this,
                                    "古诗获取失败，请回到主页面重新尝试。",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //For test
//                Intent intent = new Intent(SelectActivity.this, PoetrySelectActivity.class);
//                intent.putExtra("photo", photoPath);
//                intent.putExtra("serverPhoto", serverPhotoName);
//                ArrayList<String> resultList = new ArrayList<>();
//                resultList.add("abc def g1");
//                resultList.add("abc def g2");
//                resultList.add("abc def g3");
//                intent.putExtra("resultList", resultList);
//                startActivity(intent);
//                finish();   //结束这个选择的activity

            }
        });

    }

    public static void hideView() {
        myDrawView.setVisibility(View.GONE);
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
            if (myDrawView.getVisibility() != View.GONE) {
                hideView();
            } else {
                //弹出选择框
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);
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
        myDrawView.getHeight(screenHeight);
    }
}




