package com.jzg.crash;

import java.io.File;
import java.net.URI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by libo on 2020/7/7.
 *
 * @Email: libo@jingzhengu.com
 * @Description:
 */
public class FileLog extends File {

    //创建时间 用于排序
    long createTime;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public FileLog(@NonNull String pathname) {
        super(pathname);
    }

    public FileLog(@Nullable String parent, @NonNull String child) {
        super(parent, child);
    }

    public FileLog(@Nullable File parent, @NonNull String child) {
        super(parent, child);
    }

    public FileLog(@NonNull URI uri) {
        super(uri);
    }
}
