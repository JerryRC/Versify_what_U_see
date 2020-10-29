package com.example.poetrywhatusee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.provider.MediaStore;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.EasyPermissions;

public class FullscreenActivity extends AppCompatActivity
        implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private File cameraSavePath;    //拍照照片文件
    private String photoPath = "";  //最终照片路径（拍照以及选择）
    private static ProgressDialog wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        //用于隐藏应用的上边框
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Button mBtnL = findViewById(R.id.btn_L);
        Button mBtnR = findViewById(R.id.btn_R);

        //设置毛笔字字体
        Typeface typeface = ResourcesCompat.getFont(this, R.font.maobizi);
        mBtnL.setTypeface(typeface);
        mBtnR.setTypeface(typeface);

        mBtnL.setOnClickListener(this);
        mBtnR.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 这里用于第一次加载时检测是否有 '相机' 以及 '储存' 权限
            // 否则调用 getPermission()函数请求获取权限
            getPermission();
        }
    }

//    /**
//     * 重写 getResource 方法，防止系统字体影响
//     */
//    @Override
//    public Resources getResources() {//禁止app字体大小跟随系统字体大小调节
//        Resources resources = super.getResources();
//        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
//            Configuration configuration = resources.getConfiguration();
//            configuration.fontScale = 1.0f;
//            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//        }
//        return resources;
//    }

    @Override
    public void onClick(@NotNull View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_L:
                goCamera();
                break;
            case R.id.btn_R:
                goPhotoAlbum();
                break;
        }
    }

    //获取权限
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this,
                    "需要获取您的相册、照相使用权限", 1, permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults, this);
    }

    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    //激活相册操作
    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }


    //激活相机操作
    private void goCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //用系统默认时间来命名文件名
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.getDefault());
        String photo_name = "IMG_" + sdf.format(date) + ".jpg";

        cameraSavePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/Camera/" + photo_name);

        Uri uri = FileProvider.getUriForFile(FullscreenActivity.this,
                "com.example.poetrywhatusee.fileprovider", cameraSavePath);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        FullscreenActivity.this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //两个语句分别处理拍照的情况和选择照片的情况
        if (requestCode == 1 && resultCode == RESULT_OK) {
            photoPath = String.valueOf(cameraSavePath);
//            new EncodeImage().execute(photoPath);   //把bitmap转换成base64字符串

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
//            new EncodeImage().execute(photoPath);   //把bitmap转换成base64字符串
        }

        if (resultCode == RESULT_OK) {
            String url = "https://www.rainbowstu.cn/getImage_Android.php";  //我们的服务器地址

            RequestParams params = new RequestParams();
            params.put("submit", "get");    //自定义的使php处理图片的必要参数
            try {
                //添加图片文件
                params.put("file", new File(photoPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            setWait();  //展示进度

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String jsonStr = new String(responseBody, StandardCharsets.UTF_8);

                    //把json传过来的识别出的意象存到数组，以便传递
                    ArrayList<String> resultList = new ArrayList<>();
                    ArrayList<String> resultListPoint = new ArrayList<>();
                    //服务器的文件命名
                    String serverPhotoName = null;

                    //开始做Json解析
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        Log.i("iiiii json", jsonObject.toString());

                        JSONArray jsArr = jsonObject.getJSONArray("list");
                        //Log.i("iiiii list", jsonObject.getString("list"));
                        serverPhotoName = jsonObject.getString("name");
                        Log.i("iiiii name", serverPhotoName);

                        for (int i = 0; i < jsArr.length(); i++) {
                            JSONArray jsonTmp = (JSONArray) jsArr.get(i);
                            //Log.i("iiiii list list", jsonTmp.toString());

                            String strTmp = jsonTmp.getString(0);
                            Log.i("iiiii list list str", strTmp);

                            String pointTmp = jsonTmp.getString(1);
                            Log.i("iiiii list list str", pointTmp);

                            resultList.add(strTmp);
                            resultListPoint.add(pointTmp);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(FullscreenActivity.this, "上传成功!",
                            Toast.LENGTH_SHORT).show();

                    //如果选择照片成功则跳转至选择界面
                    Intent intent = new Intent(FullscreenActivity.this,
                            SelectActivity.class);

                    //把图片地址传到第二个activity显示
                    intent.putExtra("photo", photoPath);
                    intent.putExtra("resultList", resultList);
                    intent.putExtra("resultListPoint", resultListPoint);
                    intent.putExtra("serverPhoto", serverPhotoName);

                    wait.dismiss();
                    startActivity(intent);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    wait.dismiss();
                    Toast.makeText(FullscreenActivity.this, "上传失败，请回到主页面重新尝试。",
                            Toast.LENGTH_LONG).show();
                }
            });


//            //For test
//            ArrayList<String> resultList = new ArrayList<>();
//            resultList.add("abcdefg1");
//            resultList.add("abcdefg2");
//            resultList.add("abcdefg3");
//
//            Intent intent = new Intent(FullscreenActivity.this, SelectActivity.class);
//            intent.putExtra("photo", photoPath);
//            intent.putExtra("resultList", resultList);
//            intent.putExtra("serverPhoto", "serverPhotoName");
//            startActivity(intent);

        }

    }

    private void setWait() {
        wait = new ProgressDialog(FullscreenActivity.this);
        wait.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wait.setMessage("正在处理");
        wait.setCancelable(false);
        wait.setIndeterminate(true);
        wait.show();
    }

//    //继承AsyncTask，实现照片的异步处理
//    @SuppressLint("StaticFieldLeak")
//    private class EncodeImage extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... photopath) {
//            Bitmap bitmap = BitmapFactory.decodeFile(photopath[0]);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
//            byte[] array = stream.toByteArray();
//            String encoded_string = Base64.encodeToString(array, 0);
//            bitmap.recycle();  //防止oom
//            return null;
//        }
//    }
}

