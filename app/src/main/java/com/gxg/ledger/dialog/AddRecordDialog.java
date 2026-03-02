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
    private Button buttonSelectDate;
    private Button buttonSave;
    private Button buttonCancel;
    
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
        buttonSelectDate = view.findViewById(R.id.buttonSelectDate);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);
    }
    
    private void setupListeners() {
        buttonSelectDate.setOnClickListener(v -> showDatePicker());
        buttonSave.setOnClickListener(v -> saveRecord());
        buttonCancel.setOnClickListener(v -> dismiss());
    }
    
    private void populateFields() {
        editTextPersonName.setText(existingRecord.getPersonName());
        editTextAmount.setText(String.valueOf(existingRecord.getAmount()));
        editTextPhoneNumber.setText(existingRecord.getPhoneNumber());
        editTextAddress.setText(existingRecord.getAddress());
        editTextNotes.setText(existingRecord.getNotes());
        selectedDate = existingRecord.getEventDate();
        updateDateButton();
    }
    
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("选择事件日期")
                .setSelection(selectedDate)
                .build();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = selection;
            updateDateButton();
        });
        
        datePicker.show(((androidx.fragment.app.FragmentActivity) getContext()).getSupportFragmentManager(), "DATE_PICKER");
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