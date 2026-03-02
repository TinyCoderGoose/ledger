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

public class MarkReturnDialog extends AlertDialog {
    private TextInputEditText editTextReturnNotes;
    private Button buttonSelectReturnDate;
    private Button buttonMarkReturn;
    private Button buttonCancel;
    
    private GiftRecord record;
    private OnReturnMarkedListener listener;
    private long selectedReturnDate;
    
    public interface OnReturnMarkedListener {
        void onReturnMarked(GiftRecord record);
    }
    
    public MarkReturnDialog(@NonNull Context context, GiftRecord record, OnReturnMarkedListener listener) {
        super(context);
        this.record = record;
        this.listener = listener;
        this.selectedReturnDate = record.getReturnDate() > 0 ? record.getReturnDate() : System.currentTimeMillis();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_mark_return, null);
        setView(view);
        
        setTitle(getContext().getString(R.string.mark_return_title));
        
        initViews(view);
        setupListeners();
        updateDateButton();
        
        // 如果已经标记为还礼，填充现有数据
        if (record.isReturned()) {
            editTextReturnNotes.setText(record.getReturnNotes());
        }
        
        super.onCreate(savedInstanceState);
    }
    
    private void initViews(View view) {
        editTextReturnNotes = view.findViewById(R.id.editTextReturnNotes);
        buttonSelectReturnDate = view.findViewById(R.id.buttonSelectReturnDate);
        buttonMarkReturn = view.findViewById(R.id.buttonMarkReturn);
        buttonCancel = view.findViewById(R.id.buttonCancel);
    }
    
    private void setupListeners() {
        buttonSelectReturnDate.setOnClickListener(v -> showDatePicker());
        buttonMarkReturn.setOnClickListener(v -> markAsReturned());
        buttonCancel.setOnClickListener(v -> dismiss());
    }
    
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getContext().getString(R.string.select_return_date))
                .setSelection(selectedReturnDate)
                .build();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedReturnDate = selection;
            updateDateButton();
        });
        
        // 获取 Activity 上下文
        android.content.Context activityContext = getContext();
        if (activityContext instanceof androidx.fragment.app.FragmentActivity) {
            datePicker.show(((androidx.fragment.app.FragmentActivity) activityContext).getSupportFragmentManager(), "RETURN_DATE_PICKER");
        }
    }
    
    private void updateDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        String dateText = sdf.format(new Date(selectedReturnDate));
        buttonSelectReturnDate.setText(dateText);
    }
    
    private void markAsReturned() {
        String returnNotes = editTextReturnNotes.getText().toString().trim();
        
        // 更新记录的还礼状态
        record.setReturned(true);
        record.setReturnDate(selectedReturnDate);
        record.setReturnNotes(returnNotes.isEmpty() ? null : returnNotes);
        
        if (listener != null) {
            listener.onReturnMarked(record);
        }
        
        dismiss();
    }
}