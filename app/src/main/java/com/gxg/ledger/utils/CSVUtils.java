package com.gxg.ledger.utils;

import android.content.Context;
import android.util.Log;

import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.model.GiftRecord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CSVUtils {
    private static final String TAG = "CSVUtils";
    private static final String CSV_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";
    
    /**
     * 导出礼金簿数据到CSV文件
     */
    public static boolean exportToCSV(Context context, GiftBook book, List<GiftRecord> records, OutputStream outputStream) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            
            // 写入BOM以支持Excel正确显示中文
            writer.write("\uFEFF");
            
            // 创建表头
            String[] headers = {"序号", "宾客姓名", "礼金金额", "联系电话", "地址", "事件日期", "备注", "是否还礼", "还礼日期", "还礼备注"};
            writer.write(String.join(CSV_SEPARATOR, headers) + LINE_SEPARATOR);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            
            // 填充数据
            for (int i = 0; i < records.size(); i++) {
                GiftRecord record = records.get(i);
                
                List<String> rowData = new ArrayList<>();
                rowData.add(String.valueOf(i + 1)); // 序号
                rowData.add(escapeCSV(record.getPersonName())); // 宾客姓名
                rowData.add(String.valueOf(record.getAmount())); // 礼金金额
                rowData.add(escapeCSV(record.getPhoneNumber() != null ? record.getPhoneNumber() : "")); // 电话
                rowData.add(escapeCSV(record.getAddress() != null ? record.getAddress() : "")); // 地址
                rowData.add(dateFormat.format(new Date(record.getEventDate()))); // 事件日期
                rowData.add(escapeCSV(record.getNotes() != null ? record.getNotes() : "")); // 备注
                rowData.add(record.isReturned() ? "是" : "否"); // 是否还礼
                
                if (record.isReturned() && record.getReturnDate() > 0) {
                    rowData.add(dateFormat.format(new Date(record.getReturnDate()))); // 还礼日期
                } else {
                    rowData.add("");
                }
                
                rowData.add(escapeCSV(record.getReturnNotes() != null ? record.getReturnNotes() : "")); // 还礼备注
                
                writer.write(String.join(CSV_SEPARATOR, rowData) + LINE_SEPARATOR);
            }
            
            writer.flush();
            writer.close();
            
            Log.d(TAG, "导出CSV成功，共导出 " + records.size() + " 条记录");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "导出CSV失败", e);
            return false;
        }
    }
    
    /**
     * 从CSV文件导入数据
     */
    public static List<GiftRecord> importFromCSV(Context context, InputStream inputStream, int bookId) {
        List<GiftRecord> records = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            boolean isFirstLine = true;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            
            while ((line = reader.readLine()) != null) {
                // 跳过表头
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                try {
                    String[] columns = parseCSVLine(line);
                    if (columns.length < 10) continue;
                    
                    // 解析各列数据
                    String personName = unescapeCSV(columns[1]); // 宾客姓名
                    if (personName == null || personName.trim().isEmpty()) continue;
                    
                    double amount = Double.parseDouble(columns[2]); // 礼金金额
                    String phoneNumber = unescapeCSV(columns[3]); // 电话
                    String address = unescapeCSV(columns[4]); // 地址
                    
                    Date eventDate = dateFormat.parse(columns[5]); // 事件日期
                    String notes = unescapeCSV(columns[6]); // 备注
                    String isReturnedStr = columns[7]; // 是否还礼
                    String returnDateString = columns[8]; // 还礼日期
                    String returnNotes = unescapeCSV(columns[9]); // 还礼备注
                    
                    // 创建记录对象
                    GiftRecord record = new GiftRecord(bookId, personName, amount);
                    record.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
                    record.setAddress(address.isEmpty() ? null : address);
                    record.setEventDate(eventDate != null ? eventDate.getTime() : System.currentTimeMillis());
                    record.setNotes(notes.isEmpty() ? null : notes);
                    record.setReturned("是".equals(isReturnedStr));
                    
                    if (!returnDateString.isEmpty()) {
                        Date returnDate = dateFormat.parse(returnDateString);
                        record.setReturnDate(returnDate != null ? returnDate.getTime() : 0);
                    } else {
                        record.setReturnDate(0);
                    }
                    
                    record.setReturnNotes(returnNotes.isEmpty() ? null : returnNotes);
                    
                    records.add(record);
                    
                } catch (Exception e) {
                    Log.w(TAG, "解析CSV行数据时出错: " + line, e);
                }
            }
            
            reader.close();
            Log.d(TAG, "导入CSV成功，共导入 " + records.size() + " 条记录");
            
        } catch (Exception e) {
            Log.e(TAG, "导入CSV失败", e);
        }
        
        return records;
    }
    
    /**
     * CSV字段转义
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * CSV字段反转义
     */
    private static String unescapeCSV(String value) {
        if (value == null) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    
    /**
     * 解析CSV行
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 双引号转义
                    currentField.append('"');
                    i++; // 跳过下一个引号
                } else {
                    // 开始或结束引号
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 字段分隔符
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        result.add(currentField.toString());
        return result.toArray(new String[0]);
    }
}