package com.gxg.ledger.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.gxg.ledger.R;
import com.gxg.ledger.model.SearchCriteria;
import com.google.android.material.textfield.TextInputEditText;

public class AdvancedSearchDialog extends DialogFragment {
    
    private TextInputEditText editTextKeyword;
    private TextInputEditText editTextAddress;
    private TextInputEditText editTextMinAmount;
    private TextInputEditText editTextMaxAmount;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private RadioGroup radioGroupReturned;
    private RadioButton radioAll;
    private RadioButton radioReturned;
    private RadioButton radioNotReturned;
    private CheckBox checkBoxEnableAmountFilter;
    private CheckBox checkBoxEnableDateFilter;
    
    private SearchCriteria searchCriteria;
    private OnSearchConfirmedListener listener;
    
    public interface OnSearchConfirmedListener {
        void onSearchConfirmed(SearchCriteria criteria);
        void onSearchCancelled();
    }
    
    public AdvancedSearchDialog(SearchCriteria searchCriteria, OnSearchConfirmedListener listener) {
        this.searchCriteria = searchCriteria != null ? searchCriteria : new SearchCriteria();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_advanced_search, null);
        
        initViews(view);
        populateFields();
        setupListeners();
        
        builder.setView(view)
                .setTitle("高级搜索")
                .setPositiveButton("搜索", (dialog, which) -> {
                    collectSearchCriteria();
                    if (listener != null) {
                        listener.onSearchConfirmed(searchCriteria);
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    if (listener != null) {
                        listener.onSearchCancelled();
                    }
                })
                .setNeutralButton("清空", null); // 稍后处理
        
        AlertDialog dialog = builder.create();
        
        // 处理清空按钮
        dialog.setOnShowListener(dialogInterface -> {
            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(v -> {
                clearAllFields();
            });
        });
        
        return dialog;
    }
    
    private void initViews(View view) {
        editTextKeyword = view.findViewById(R.id.editTextKeyword);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextMinAmount = view.findViewById(R.id.editTextMinAmount);
        editTextMaxAmount = view.findViewById(R.id.editTextMaxAmount);
        editTextStartDate = view.findViewById(R.id.editTextStartDate);
        editTextEndDate = view.findViewById(R.id.editTextEndDate);
        radioGroupReturned = view.findViewById(R.id.radioGroupReturned);
        radioAll = view.findViewById(R.id.radioAll);
        radioReturned = view.findViewById(R.id.radioReturned);
        radioNotReturned = view.findViewById(R.id.radioNotReturned);
        checkBoxEnableAmountFilter = view.findViewById(R.id.checkBoxEnableAmountFilter);
        checkBoxEnableDateFilter = view.findViewById(R.id.checkBoxEnableDateFilter);
    }
    
    private void populateFields() {
        editTextKeyword.setText(searchCriteria.getKeyword());
        editTextAddress.setText(searchCriteria.getAddress());
        
        // 金额筛选
        if (searchCriteria.getMinAmount() != null || searchCriteria.getMaxAmount() != null) {
            checkBoxEnableAmountFilter.setChecked(true);
            if (searchCriteria.getMinAmount() != null) {
                editTextMinAmount.setText(String.valueOf(searchCriteria.getMinAmount()));
            }
            if (searchCriteria.getMaxAmount() != null) {
                editTextMaxAmount.setText(String.valueOf(searchCriteria.getMaxAmount()));
            }
        } else {
            checkBoxEnableAmountFilter.setChecked(false);
            editTextMinAmount.setEnabled(false);
            editTextMaxAmount.setEnabled(false);
        }
        
        // 日期筛选
        if (searchCriteria.getStartDate() != null || searchCriteria.getEndDate() != null) {
            checkBoxEnableDateFilter.setChecked(true);
            // 这里可以添加日期格式化显示
        } else {
            checkBoxEnableDateFilter.setChecked(false);
            editTextStartDate.setEnabled(false);
            editTextEndDate.setEnabled(false);
        }
        
        // 还礼状态
        if (searchCriteria.getReturned() == null) {
            radioAll.setChecked(true);
        } else if (searchCriteria.getReturned()) {
            radioReturned.setChecked(true);
        } else {
            radioNotReturned.setChecked(true);
        }
    }
    
    private void setupListeners() {
        // 金额筛选启用/禁用
        checkBoxEnableAmountFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextMinAmount.setEnabled(isChecked);
            editTextMaxAmount.setEnabled(isChecked);
            if (!isChecked) {
                editTextMinAmount.setText("");
                editTextMaxAmount.setText("");
            }
        });
        
        // 日期筛选启用/禁用
        checkBoxEnableDateFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextStartDate.setEnabled(isChecked);
            editTextEndDate.setEnabled(isChecked);
            if (!isChecked) {
                editTextStartDate.setText("");
                editTextEndDate.setText("");
            }
        });
    }
    
    private void collectSearchCriteria() {
        searchCriteria.setKeyword(editTextKeyword.getText().toString().trim());
        searchCriteria.setAddress(editTextAddress.getText().toString().trim());
        
        // 收集金额筛选条件
        if (checkBoxEnableAmountFilter.isChecked()) {
            try {
                String minAmountStr = editTextMinAmount.getText().toString().trim();
                String maxAmountStr = editTextMaxAmount.getText().toString().trim();
                
                if (!minAmountStr.isEmpty()) {
                    searchCriteria.setMinAmount(Double.parseDouble(minAmountStr));
                } else {
                    searchCriteria.setMinAmount(null);
                }
                
                if (!maxAmountStr.isEmpty()) {
                    searchCriteria.setMaxAmount(Double.parseDouble(maxAmountStr));
                } else {
                    searchCriteria.setMaxAmount(null);
                }
            } catch (NumberFormatException e) {
                // 忽略无效数字
                searchCriteria.setMinAmount(null);
                searchCriteria.setMaxAmount(null);
            }
        } else {
            searchCriteria.setMinAmount(null);
            searchCriteria.setMaxAmount(null);
        }
        
        // 收集还礼状态
        int selectedRadioId = radioGroupReturned.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.radioReturned) {
            searchCriteria.setReturned(true);
        } else if (selectedRadioId == R.id.radioNotReturned) {
            searchCriteria.setReturned(false);
        } else {
            searchCriteria.setReturned(null);
        }
        
        // TODO: 添加日期收集逻辑
        searchCriteria.setStartDate(null);
        searchCriteria.setEndDate(null);
    }
    
    private void clearAllFields() {
        editTextKeyword.setText("");
        editTextAddress.setText("");
        editTextMinAmount.setText("");
        editTextMaxAmount.setText("");
        editTextStartDate.setText("");
        editTextEndDate.setText("");
        radioAll.setChecked(true);
        checkBoxEnableAmountFilter.setChecked(false);
        checkBoxEnableDateFilter.setChecked(false);
        
        // 更新UI状态
        editTextMinAmount.setEnabled(false);
        editTextMaxAmount.setEnabled(false);
        editTextStartDate.setEnabled(false);
        editTextEndDate.setEnabled(false);
    }
}