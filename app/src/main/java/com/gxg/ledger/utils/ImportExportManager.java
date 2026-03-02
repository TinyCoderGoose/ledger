package com.gxg.ledger.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.model.GiftRecord;
import com.gxg.ledger.repository.GiftRecordRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class ImportExportManager {
    private static final String TAG = "ImportExportManager";
    
    public interface ExportCallback {
        void onSuccess(File file);
        void onError(String error);
    }
    
    public interface ImportCallback {
        void onSuccess(int importedCount);
        void onError(String error);
    }
    
    /**
     * 导出礼金簿数据
     */
    public static void exportBookData(Activity activity, GiftBook book, List<GiftRecord> records, 
                                    ExportCallback callback) {
        if (!FileUtils.checkStoragePermission(activity)) {
            callback.onError("请先授予存储权限");
            return;
        }
        
        new ExportTask(activity, book, records, callback).execute();
    }
    
    /**
     * 导入CSV数据
     */
    public static void importBookData(Activity activity, Uri uri, int bookId, 
                                    GiftRecordRepository repository, ImportCallback callback) {
        if (!FileUtils.checkStoragePermission(activity)) {
            callback.onError("请先授予存储权限");
            return;
        }
        
        if (!FileUtils.isValidExcelFile(getFileNameFromUri(activity, uri))) {
            callback.onError("请选择有效的CSV文件(.csv)");
            return;
        }
        
        new ImportTask(activity, uri, bookId, repository, callback).execute();
    }
    
    /**
     * 从Uri获取文件名
     */
    private static String getFileNameFromUri(Context context, Uri uri) {
        try {
            String fileName = uri.getLastPathSegment();
            if (fileName != null) {
                return fileName;
            }
        } catch (Exception e) {
            Log.w(TAG, "获取文件名失败", e);
        }
        return "unknown.csv";
    }
    
    /**
     * 导出任务
     */
    private static class ExportTask extends AsyncTask<Void, Void, File> {
        private Activity activity;
        private GiftBook book;
        private List<GiftRecord> records;
        private ExportCallback callback;
        private String errorMessage;
        
        public ExportTask(Activity activity, GiftBook book, List<GiftRecord> records, 
                         ExportCallback callback) {
            this.activity = activity;
            this.book = book;
            this.records = records;
            this.callback = callback;
        }
        
        @Override
        protected File doInBackground(Void... voids) {
            try {
                // 使用CSVUtils导出数据到字节数组
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                boolean success = CSVUtils.exportToCSV(activity, book, records, outputStream);
                
                if (success) {
                    // 保存到下载目录
                    File exportFile = FileUtils.createExportFile(book.getName());
                    // 修改文件扩展名为.csv
                    String csvFileName = exportFile.getAbsolutePath().replace(".xlsx", ".csv");
                    File csvFile = new File(csvFileName);
                    
                    FileUtils.saveToFile(
                        new ByteArrayInputStream(outputStream.toByteArray()), 
                        csvFile
                    );
                    return csvFile;
                } else {
                    errorMessage = "导出数据失败";
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "导出任务失败", e);
                errorMessage = "导出失败: " + e.getMessage();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(File file) {
            if (file != null && file.exists()) {
                callback.onSuccess(file);
            } else {
                callback.onError(errorMessage != null ? errorMessage : "未知错误");
            }
        }
    }
    
    /**
     * 导入任务
     */
    private static class ImportTask extends AsyncTask<Void, Integer, Integer> {
        private Activity activity;
        private Uri uri;
        private int bookId;
        private GiftRecordRepository repository;
        private ImportCallback callback;
        private String errorMessage;
        
        public ImportTask(Activity activity, Uri uri, int bookId, 
                         GiftRecordRepository repository, ImportCallback callback) {
            this.activity = activity;
            this.uri = uri;
            this.bookId = bookId;
            this.repository = repository;
            this.callback = callback;
        }
        
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                // 读取文件
                InputStream inputStream = activity.getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    errorMessage = "无法读取文件";
                    return 0;
                }
                
                // 解析CSV数据
                List<GiftRecord> records = CSVUtils.importFromCSV(activity, inputStream, bookId);
                inputStream.close();
                
                if (records.isEmpty()) {
                    errorMessage = "文件中没有有效数据";
                    return 0;
                }
                
                // 保存到数据库
                int importedCount = 0;
                for (GiftRecord record : records) {
                    repository.insert(record);
                    importedCount++;
                    publishProgress(importedCount);
                }
                
                return importedCount;
                
            } catch (Exception e) {
                Log.e(TAG, "导入任务失败", e);
                errorMessage = "导入失败: " + e.getMessage();
                return 0;
            }
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
            // 可以在这里更新进度
        }
        
        @Override
        protected void onPostExecute(Integer importedCount) {
            if (importedCount > 0) {
                callback.onSuccess(importedCount);
            } else {
                callback.onError(errorMessage != null ? errorMessage : "导入失败");
            }
        }
    }
    
    // ByteArrayInputStream的简单实现
    private static class ByteArrayInputStream extends InputStream {
        private byte[] data;
        private int position = 0;
        
        public ByteArrayInputStream(byte[] data) {
            this.data = data;
        }
        
        @Override
        public int read() {
            if (position >= data.length) {
                return -1;
            }
            return data[position++] & 0xFF;
        }
        
        @Override
        public int read(byte[] b, int off, int len) {
            if (position >= data.length) {
                return -1;
            }
            int bytesToRead = Math.min(len, data.length - position);
            System.arraycopy(data, position, b, off, bytesToRead);
            position += bytesToRead;
            return bytesToRead;
        }
    }
}