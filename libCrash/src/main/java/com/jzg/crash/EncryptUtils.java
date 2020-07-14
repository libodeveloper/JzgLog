package com.jzg.crash;

import android.util.Base64;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class EncryptUtils {

    /** 加密key */
    private static final String PASSWORD_ENC_SECRET = "3CEA5DCD-451F-5W96-87B6-B2N7C7C8A5FD";

	/**
     * 加密
     **/
    public  static String encryptText(String encryptText) {
        try {
            DESKeySpec keySpec = new DESKeySpec(PASSWORD_ENC_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(encryptText.getBytes("UTF-8")), Base64.DEFAULT);
            return encrypedPwd;
        } catch (Exception e) {
        }
        return encryptText;
    }

    /**
     * 解密
     **/
    public static String decryptText(String encryptedText) {
        if (encryptedText == null || "".equals(encryptedText)) {
            return null;
        }
        try {
            DESKeySpec keySpec = new DESKeySpec(PASSWORD_ENC_SECRET.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            byte[] encryptedWithoutB64 = Base64.decode(encryptedText, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {

            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            e.printStackTrace(pw);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
            pw.close();
            String crashInfo = writer.toString();  // 得到完整的异常信息字符串
            Log.e("EXCEPTION",crashInfo);

        }
        return encryptedText;
    }

}
