package com.gxg.ledger.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int REQUEST_CODE_STORAGE_MANAGER = 1002;
    
    /**
     * 检查并请求存储权限
     */
    public static boolean checkStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上版本
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_STORAGE_MANAGER);
                return false;
            }
            return true;
        } else {
            // Android 10及以下版本
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, 
                                   Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION);
                return false;
            }
            return true;
        }
    }
    
    /**
     * 创建导出文件路径
     */
    public static File createExportFile(String bookName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "礼金簿_" + bookName + "_" + timeStamp + ".xlsx";
        
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(downloadsDir, fileName);
    }
    
    /**
     * 保存数据到文件
     */
    public static boolean saveToFile(InputStream inputStream, File outputFile) {
        try {
            OutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "保存文件失败", e);
            return false;
        }
    }
    
    /**
     * 通过ContentResolver写入文件（适用于分区存储）
     */
    public static boolean writeFileToUri(Context context, Uri uri, byte[] data) {
        try {
            ContentResolver resolver = context.getContentResolver();
            OutputStream outputStream = resolver.openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.close();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "写入URI文件失败", e);
        }
        return false;
    }
    
    /**
     * 获取文件大小格式化显示
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 验证文件是否为CSV文件
     */
    public static boolean isValidExcelFile(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".csv");
    }
    
    /**
     * 处理权限请求结果
     */
    public static boolean handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    
    /**
     * 处理存储管理权限结果
     */
    public static boolean handleStorageManagerResult(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return false;
    }
}