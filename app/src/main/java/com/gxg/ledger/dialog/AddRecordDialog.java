package com.gxg.ledger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.gxg.ledger.R;
import com.gxg.ledger.model.GiftRecord;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddRecordDialog extends AlertDialog {
    private TextInputEditText editTextPersonName;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextPhoneNumber;
    private TextInputEditText editTextAddress;
    private TextInputEditText editTextNotes;
    private TextInputEditText editTextReturnNotes;
    private Button buttonSelectDate;
    private Button buttonSelectReturnDate;
    private Button buttonSave;
    private Button buttonCancel;
    private com.google.android.material.checkbox.MaterialCheckBox checkBoxReturned;
    
    private GiftRecord existingRecord;
    private OnRecordSavedListener listener;
    private long selectedDate;
    
    public interface OnRecordSavedListener {
        void onRecordSaved(GiftRecord record);
        void onRecordUpdated(GiftRecord record);
    }
    
    public AddRecordDialog(@NonNull Context context, OnRecordSavedListener listener) {
        super(context);
        this.listener = listener;
        this.existingRecord = null;
        this.selectedDate = System.currentTimeMillis();
    }
    
    public AddRecordDialog(@NonNull Context context, GiftRecord record, OnRecordSavedListener listener) {
        super(context);
        this.listener = listener;
        this.existingRecord = record;
        this.selectedDate = record.getEventDate();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_add_record_new, null);
        setView(view);
        
        initViews(view);
        setupListeners();
        
        if (existingRecord != null) {
            setTitle(getContext().getString(R.string.edit_record));
            populateFields();
        } else {
            setTitle(getContext().getString(R.string.add_record));
        }
        
        updateDateButton();
        super.onCreate(savedInstanceState);
    }
    
    private void initViews(View view) {
        editTextPersonName = view.findViewById(R.id.editTextPersonName);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        editTextReturnNotes = view.findViewById(R.id.editTextReturnNotes);
        buttonSelectDate = view.findViewById(R.id.buttonSelectDate);
        buttonSelectReturnDate = view.findViewById(R.id.buttonSelectReturnDate);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        checkBoxReturned = view.findViewById(R.id.checkBoxReturned);
    }
    
    private void setupListeners() {
        buttonSelectDate.setOnClickListener(v -> showDatePicker());
        buttonSelectReturnDate.setOnClickListener(v -> showReturnDatePicker());
        buttonSave.setOnClickListener(v -> saveRecord());
        buttonCancel.setOnClickListener(v -> dismiss());
        
        // 监听复选框状态变化
        checkBoxReturned.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonSelectReturnDate.setEnabled(isChecked);
            editTextReturnNotes.setEnabled(isChecked);
        });
    }
    
    private void populateFields() {
        editTextPersonName.setText(existingRecord.getPersonName());
        editTextAmount.setText(String.valueOf(existingRecord.getAmount()));
        editTextPhoneNumber.setText(existingRecord.getPhoneNumber());
        editTextAddress.setText(existingRecord.getAddress());
        editTextNotes.setText(existingRecord.getNotes());
        selectedDate = existingRecord.getEventDate();
        updateDateButton();
        // 设置还礼状态
        boolean isReturned = existingRecord.isReturned();
        checkBoxReturned.setChecked(isReturned);
        // 根据还礼状态启用/禁用相关组件
        buttonSelectReturnDate.setEnabled(isReturned);
        editTextReturnNotes.setEnabled(isReturned);
        // 如果已经还礼，填充还礼信息
        if (isReturned) {
            // 初始化还礼日期
            selectedReturnDate = existingRecord.getReturnDate() > 0 ? 
                existingRecord.getReturnDate() : System.currentTimeMillis();
            updateReturnDateButton();
            editTextReturnNotes.setText(existingRecord.getReturnNotes());
        }
    }
    
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getContext().getString(R.string.select_date))
                .setSelection(selectedDate)
                .build();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = selection;
            updateDateButton();
        });
        
        // 获取 Activity 上下文
        android.content.Context activityContext = getContext();
        if (activityContext instanceof androidx.fragment.app.FragmentActivity) {
            datePicker.show(((androidx.fragment.app.FragmentActivity) activityContext).getSupportFragmentManager(), "DATE_PICKER");
        }
    }
    
    private long selectedReturnDate;
    
    private void showReturnDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getContext().getString(R.string.select_return_date))
                .setSelection(selectedReturnDate > 0 ? selectedReturnDate : System.currentTimeMillis())
                .build();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedReturnDate = selection;
            updateReturnDateButton();
        });
        
        // 获取 Activity 上下文
        android.content.Context activityContext = getContext();
        if (activityContext instanceof androidx.fragment.app.FragmentActivity) {
            datePicker.show(((androidx.fragment.app.FragmentActivity) activityContext).getSupportFragmentManager(), "RETURN_DATE_PICKER");
        }
    }
    
    private void updateReturnDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
        String dateText = sdf.format(new Date(selectedReturnDate));
        buttonSelectReturnDate.setText(dateText);
    }
    
    private void updateDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        String dateText = sdf.format(new Date(selectedDate));
        buttonSelectDate.setText(dateText);
    }
    
    private void saveRecord() {
        String personName = editTextPersonName.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();
        
        // 验证必填字段
        if (personName.isEmpty()) {
            editTextPersonName.setError(getContext().getString(R.string.person_name_required));
            editTextPersonName.requestFocus();
            return;
        }
        
        if (amountStr.isEmpty()) {
            editTextAmount.setError(getContext().getString(R.string.amount_required));
            editTextAmount.requestFocus();
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                editTextAmount.setError(getContext().getString(R.string.amount_positive));
                editTextAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editTextAmount.setError(getContext().getString(R.string.amount_invalid));
            editTextAmount.requestFocus();
            return;
        }
        
        if (existingRecord != null) {
            // 更新现有记录
            existingRecord.setPersonName(personName);
            existingRecord.setAmount(amount);
            existingRecord.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
            existingRecord.setAddress(address.isEmpty() ? null : address);
            existingRecord.setNotes(notes.isEmpty() ? null : notes);
            existingRecord.setEventDate(selectedDate);
            // 更新还礼状态
            boolean isReturned = checkBoxReturned.isChecked();
            existingRecord.setReturned(isReturned);
            if (isReturned) {
                existingRecord.setReturnDate(selectedReturnDate > 0 ? selectedReturnDate : System.currentTimeMillis());
                String returnNotes = editTextReturnNotes.getText().toString().trim();
                existingRecord.setReturnNotes(returnNotes.isEmpty() ? null : returnNotes);
            } else {
                existingRecord.setReturnDate(0);
                existingRecord.setReturnNotes(null);
            }
            
            if (listener != null) {
                listener.onRecordUpdated(existingRecord);
            }
        } else {
            // 创建新记录
            GiftRecord newRecord = new GiftRecord(0, personName, amount);
            newRecord.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
            newRecord.setAddress(address.isEmpty() ? null : address);
            newRecord.setNotes(notes.isEmpty() ? null : notes);
            newRecord.setEventDate(selectedDate);
            
            if (listener != null) {
                listener.onRecordSaved(newRecord);
            }
        }
        
        dismiss();
    }
}