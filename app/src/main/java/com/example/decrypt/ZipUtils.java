package com.example.decrypt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * * Java utils 实现的Zip工具
 * *
 * * @author once
 */
public class ZipUtils {
    public static final String TAG = "ZipUtils";

    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte


    public static boolean unzip(String zipFilePath, String unzipPath) {
        try{
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration emu = zipFile.entries();
            while(emu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)emu.nextElement();
                if(entry.isDirectory()) {
                    new File(unzipPath + "/" + entry.getName()).mkdirs();
                    continue;
                }
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File file = new File(unzipPath + "/" + entry.getName());
                if(file.exists())//每次都覆盖老的
                    file.delete();
                File parent = file.getParentFile();
                if(parent != null && (!parent.exists())) {
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int count;
                byte data[] = new byte[4 * 1024];
                while((count = bis.read(data, 0, 4 * 1024)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();
            }
            zipFile.close();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
