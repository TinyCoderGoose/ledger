package com.gxg.ledger.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "ledger_preferences";
    
    // 显示设置
    private static final String KEY_THEME = "theme";
    private static final String KEY_FONT_SIZE = "font_size";
    
    // 数据设置
    private static final String KEY_AUTO_BACKUP = "auto_backup";
    private static final String KEY_BACKUP_FREQUENCY = "backup_frequency";
    
    // 通知设置
    private static final String KEY_RETURN_REMINDER = "return_reminder";
    private static final String KEY_REMINDER_TIME = "reminder_time";
    
    // 默认值
    private static final boolean DEFAULT_THEME = false; // false = light, true = dark
    private static final String DEFAULT_FONT_SIZE = "normal";
    private static final boolean DEFAULT_AUTO_BACKUP = true;
    private static final String DEFAULT_BACKUP_FREQUENCY = "daily";
    private static final boolean DEFAULT_RETURN_REMINDER = true;
    private static final String DEFAULT_REMINDER_TIME = "09:00";
    
    private SharedPreferences sharedPreferences;
    
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // 主题设置
    public boolean isDarkTheme() {
        return sharedPreferences.getBoolean(KEY_THEME, DEFAULT_THEME);
    }
    
    public void setDarkTheme(boolean isDark) {
        sharedPreferences.edit().putBoolean(KEY_THEME, isDark).apply();
    }
    
    // 字体大小设置
    public String getFontSize() {
        return sharedPreferences.getString(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
    }
    
    public void setFontSize(String fontSize) {
        sharedPreferences.edit().putString(KEY_FONT_SIZE, fontSize).apply();
    }
    
    // 自动备份设置
    public boolean isAutoBackupEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_BACKUP, DEFAULT_AUTO_BACKUP);
    }
    
    public void setAutoBackupEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_BACKUP, enabled).apply();
    }
    
    // 备份频率设置
    public String getBackupFrequency() {
        return sharedPreferences.getString(KEY_BACKUP_FREQUENCY, DEFAULT_BACKUP_FREQUENCY);
    }
    
    public void setBackupFrequency(String frequency) {
        sharedPreferences.edit().putString(KEY_BACKUP_FREQUENCY, frequency).apply();
    }
    
    // 还礼提醒设置
    public boolean isReturnReminderEnabled() {
        return sharedPreferences.getBoolean(KEY_RETURN_REMINDER, DEFAULT_RETURN_REMINDER);
    }
    
    public void setReturnReminderEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_RETURN_REMINDER, enabled).apply();
    }
    
    // 提醒时间设置
    public String getReminderTime() {
        return sharedPreferences.getString(KEY_REMINDER_TIME, DEFAULT_REMINDER_TIME);
    }
    
    public void setReminderTime(String time) {
        sharedPreferences.edit().putString(KEY_REMINDER_TIME, time).apply();
    }
    
    // 清除所有设置
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}