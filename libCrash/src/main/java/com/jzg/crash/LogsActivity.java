package com.jzg.crash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LogsActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rvLogs;
    private TextView tvEmpty;
    private ImageView ivBack;
    private TextView tvTitle;
    private ProgressDialog dialog;
    private LogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        rvLogs = (RecyclerView) findViewById(R.id.rvLog);
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.log_list_title);
        ivBack.setOnClickListener(this);
        dialog = ProgressDialog.show(this,"","loading...",false,false);
        Observable.create(new Observable.OnSubscribe<List<File>>() {
            @Override
            public void call(Subscriber<? super List<File>> subscriber) {
                subscriber.onNext(readLogs());
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<File>>() {
                    @Override
                    public void onCompleted() {
                        if(dialog!=null && dialog.isShowing())
                            dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<File> files) {
                        if(files!=null && files.size()>0){
                            rvLogs.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(LogsActivity.this);
                            rvLogs.setLayoutManager(layoutManager);
                            adapter = new LogAdapter(LogsActivity.this,R.layout.item_logs,files);
                            rvLogs.setAdapter(adapter);
                            adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                                    File file = adapter.getDatas().get(position);
                                    Intent intent = new Intent(LogsActivity.this,LogDetailActivity.class);
                                    intent.putExtra("logPath",file.getAbsolutePath());
                                    startActivity(intent);
                                }

                                @Override
                                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                                    return false;
                                }
                            });
                        }
                    }
                });

    }

    public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSSS", Locale.getDefault());

    private List<File> readLogs(){
        List<File> logs = new ArrayList<>();
        List<FileLog> logstime = new ArrayList<>();
        String logDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"JzgLog"+File.separator + "JzgCrash";
        File dir = new File(logDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File[] logArr = dir.listFiles();
        if(logArr!=null && logArr.length>0){
            for(File log:logArr){

                String fileName = log.getName();
                int timeSIndex = fileName.indexOf("-")+1;
                int timeEindex = fileName.indexOf(".");
                String fileCreateTime = fileName.substring(timeSIndex,timeEindex);

                FileLog fileLog = new FileLog(log.getAbsolutePath());

                try {
                    long time = DEFAULT_SDF.parse(fileCreateTime).getTime();
                    fileLog.setCreateTime(time);
                }catch (Exception e){
                    e.printStackTrace();
                }

                logstime.add(fileLog);
            }
        }

        Collections.sort(logstime ,new FileLogComparator());
        logs.addAll(logstime);
        return logs;
    }

    @Override
    public void onClick(View v) {
        finish();
    }

   public class  FileLogComparator implements Comparator<FileLog> {
        //o2在前，o1在后==倒序（降序）从大 --> 小
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public int compare(FileLog o1, FileLog o2) {
            return Long.compare(o2.getCreateTime(),o1.getCreateTime());
        }
    }

}
