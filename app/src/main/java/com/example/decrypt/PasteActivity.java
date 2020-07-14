package com.example.decrypt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzg.crash.EncryptUtils;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by libo on 2020/7/8.
 *
 * @Email: libo@jingzhengu.com
 * @Description:
 */
public class PasteActivity extends AppCompatActivity {

    private TextView tvContent;
    private ImageView ivBack;
    private LinearLayout llRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste);

        tvContent = findViewById(R.id.tvContent);
        ivBack = findViewById(R.id.ivBack);
        llRoot = findViewById(R.id.llRoot);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llRoot.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

    }


    /**
     * Created by 李波 on 2020/7/14.
     * 发现低版本可以直接在 onCreate里面取，但是在 Android 高版本里，即便在 onResume 里也取不到
     * 所以让其在界面加载完毕以后再取，就没问题了
     */
    public static String getClipboardContent(Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData data = cm.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                ClipData.Item item = data.getItemAt(0);
                if (item != null) {
                    CharSequence sequence = item.coerceToText(context);

                    if (sequence != null) {
                        return sequence.toString();
                    }
                }
            }
        }
        return "null";
    }


    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {

            String content = getClipboardContent(PasteActivity.this);
            String decryptText = EncryptUtils.decryptText(content);
            tvContent.setText(decryptText);

            llRoot.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
        }
    };


}
