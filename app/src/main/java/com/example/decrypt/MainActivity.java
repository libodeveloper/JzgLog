package com.example.decrypt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jzg.crash.LogsActivity;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by libo on 2020/7/6.
 *
 * @Email: libo@jingzhengu.com
 * @Description:
 */
public class MainActivity extends AppCompatActivity {

    TextView tvLookLog;
    TextView tvPaste;
    TextView tvLoad;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionHelper.getInstance().requestPermission(new PermissionHelper.RequestResultListener() {
            @Override
            public void onResult(boolean granted) {
                if (granted) {

                } else {
                    Toast.makeText(MainActivity.this, "此功能需要开启SD卡读写授权!", Toast.LENGTH_SHORT).show();
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

        tvLookLog = findViewById(R.id.tvLookLog);
        tvPaste = findViewById(R.id.tvPaste);
        tvLoad = findViewById(R.id.tvLoad);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();

            }
        });
        tvLookLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LogsActivity.class);
                startActivity(intent);

            }
        });

        tvPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PasteActivity.class);
                startActivity(intent);
            }
        });

        tvLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,"选择日志压缩文件 JzgLog.zip",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);   //打开文件夹管理器选择日志压缩文件
                //intent.setType(“image/*”);//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

    }


    String path;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
            }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = PathUtil.getPath(this, uri);
            } else {//4.4以下下系统调用方法
                path = PathUtil.getRealPathFromURI(this,uri);
            }

            //解压到的路径
            String logDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"JzgLog";

            boolean del = deleteDir(new File(logDir));
            Log.e("deldel"," = "+del);
            ZipUtils.unzip(path,logDir);
            Intent intent = new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);

        }
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public  boolean deleteDir(File dir) {
        if (dir == null) return false;
        // 目录不存在返回true
        if (!dir.exists()) return true;
        // 不是目录返回false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (!deleteFile(file)) return false;
            } else if (file.isDirectory()) {
                if (!deleteDir(file)) return false;
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public  boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

}
