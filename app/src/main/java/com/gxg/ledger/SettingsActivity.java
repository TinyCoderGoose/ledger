package com.gxg.ledger;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.gxg.ledger.utils.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    
    private Toolbar toolbar;
    private PreferenceManager preferenceManager;
    
    // 显示设置
    private SwitchMaterial switchTheme;
    private TextView textViewThemeValue;
    private TextView textViewFontSizeValue;
    private ImageButton buttonFontSize;
    
    // 数据设置
    private SwitchMaterial switchAutoBackup;
    private TextView textViewBackupFrequencyValue;
    private ImageButton buttonBackupFrequency;
    
    // 通知设置
    private SwitchMaterial switchReturnReminder;
    private TextView textViewReminderTimeValue;
    private ImageButton buttonReminderTime;
    
    // 关于
    private View layoutAbout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        setupToolbar();
        setupPreferences();
        setupListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        
        // 显示设置
        switchTheme = findViewById(R.id.switchTheme);
        textViewThemeValue = findViewById(R.id.textViewThemeValue);
        textViewFontSizeValue = findViewById(R.id.textViewFontSizeValue);
        buttonFontSize = findViewById(R.id.buttonFontSize);
        
        // 数据设置
        switchAutoBackup = findViewById(R.id.switchAutoBackup);
        textViewBackupFrequencyValue = findViewById(R.id.textViewBackupFrequencyValue);
        buttonBackupFrequency = findViewById(R.id.buttonBackupFrequency);
        
        // 通知设置
        switchReturnReminder = findViewById(R.id.switchReturnReminder);
        textViewReminderTimeValue = findViewById(R.id.textViewReminderTimeValue);
        buttonReminderTime = findViewById(R.id.buttonReminderTime);
        
        // 关于
        layoutAbout = findViewById(R.id.layoutAbout);
        
        preferenceManager = new PreferenceManager(this);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    
    private void setupPreferences() {
        // 主题设置
        switchTheme.setChecked(preferenceManager.isDarkTheme());
        updateThemeValue();
        
        // 字体大小设置
        updateFontSizeValue();
        
        // 自动备份设置
        switchAutoBackup.setChecked(preferenceManager.isAutoBackupEnabled());
        updateBackupFrequencyValue();
        
        // 还礼提醒设置
        switchReturnReminder.setChecked(preferenceManager.isReturnReminderEnabled());
        updateReminderTimeValue();
    }
    
    private void setupListeners() {
        // 主题切换
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setDarkTheme(isChecked);
            updateThemeValue();
            Toast.makeText(this, "主题设置将在下次启动时生效", Toast.LENGTH_SHORT).show();
        });
        
        // 字体大小选择
        buttonFontSize.setOnClickListener(v -> showFontSizeDialog());
        
        // 自动备份切换
        switchAutoBackup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setAutoBackupEnabled(isChecked);
            Toast.makeText(this, isChecked ? "已开启自动备份" : "已关闭自动备份", Toast.LENGTH_SHORT).show();
        });
        
        // 备份频率选择
        buttonBackupFrequency.setOnClickListener(v -> showBackupFrequencyDialog());
        
        // 还礼提醒切换
        switchReturnReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setReturnReminderEnabled(isChecked);
            Toast.makeText(this, isChecked ? "已开启还礼提醒" : "已关闭还礼提醒", Toast.LENGTH_SHORT).show();
        });
        
        // 提醒时间选择
        buttonReminderTime.setOnClickListener(v -> showTimePickerDialog());
        
        // 关于
        layoutAbout.setOnClickListener(v -> showAboutDialog());
    }
    
    private void updateThemeValue() {
        boolean isDark = preferenceManager.isDarkTheme();
        textViewThemeValue.setText(isDark ? R.string.dark_theme : R.string.light_theme);
    }
    
    private void updateFontSizeValue() {
        String fontSize = preferenceManager.getFontSize();
        int resourceId;
        switch (fontSize) {
            case "small":
                resourceId = R.string.small;
                break;
            case "large":
                resourceId = R.string.large;
                break;
            default:
                resourceId = R.string.normal;
                break;
        }
        textViewFontSizeValue.setText(resourceId);
    }
    
    private void updateBackupFrequencyValue() {
        String frequency = preferenceManager.getBackupFrequency();
        int resourceId;
        switch (frequency) {
            case "weekly":
                resourceId = R.string.weekly;
                break;
            case "monthly":
                resourceId = R.string.monthly;
                break;
            default:
                resourceId = R.string.daily;
                break;
        }
        textViewBackupFrequencyValue.setText(resourceId);
    }
    
    private void updateReminderTimeValue() {
        textViewReminderTimeValue.setText(preferenceManager.getReminderTime());
    }
    
    private void showFontSizeDialog() {
        String[] fontSizes = {"小", "正常", "大"};
        String[] fontSizeValues = {"small", "normal", "large"};
        int selectedIndex = getCurrentFontSizeIndex();
        
        new AlertDialog.Builder(this)
                .setTitle("选择字体大小")
                .setSingleChoiceItems(fontSizes, selectedIndex, (dialog, which) -> {
                    preferenceManager.setFontSize(fontSizeValues[which]);
                    updateFontSizeValue();
                    dialog.dismiss();
                    Toast.makeText(this, "字体大小已设置为" + fontSizes[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private int getCurrentFontSizeIndex() {
        String currentSize = preferenceManager.getFontSize();
        switch (currentSize) {
            case "small":
                return 0;
            case "large":
                return 2;
            default:
                return 1;
        }
    }
    
    private void showBackupFrequencyDialog() {
        String[] frequencies = {"每天", "每周", "每月"};
        String[] frequencyValues = {"daily", "weekly", "monthly"};
        int selectedIndex = getCurrentFrequencyIndex();
        
        new AlertDialog.Builder(this)
                .setTitle("选择备份频率")
                .setSingleChoiceItems(frequencies, selectedIndex, (dialog, which) -> {
                    preferenceManager.setBackupFrequency(frequencyValues[which]);
                    updateBackupFrequencyValue();
                    dialog.dismiss();
                    Toast.makeText(this, "备份频率已设置为" + frequencies[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private int getCurrentFrequencyIndex() {
        String currentFrequency = preferenceManager.getBackupFrequency();
        switch (currentFrequency) {
            case "weekly":
                return 1;
            case "monthly":
                return 2;
            default:
                return 0;
        }
    }
    
    private void showTimePickerDialog() {
        // 简化的时间选择实现
        String[] times = {"08:00", "09:00", "10:00", "14:00", "18:00", "20:00"};
        int selectedIndex = getCurrentTimeIndex(times);
        
        new AlertDialog.Builder(this)
                .setTitle("选择提醒时间")
                .setSingleChoiceItems(times, selectedIndex, (dialog, which) -> {
                    preferenceManager.setReminderTime(times[which]);
                    updateReminderTimeValue();
                    dialog.dismiss();
                    Toast.makeText(this, "提醒时间已设置为" + times[which], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private int getCurrentTimeIndex(String[] times) {
        String currentTime = preferenceManager.getReminderTime();
        for (int i = 0; i < times.length; i++) {
            if (times[i].equals(currentTime)) {
                return i;
            }
        }
        return 1; // 默认选择09:00
    }
    
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("关于礼金簿")
                .setMessage("礼金簿 v1.0\n\n一个简单易用的礼金管理工具\n\n主要功能：\n• 礼金簿管理\n• 宾客记录管理\n• 还礼提醒\n• 数据导入导出\n• 个性化设置")
                .setPositiveButton("确定", null)
                .show();
    }
}