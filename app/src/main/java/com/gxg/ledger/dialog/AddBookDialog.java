package com.gxg.ledger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.gxg.ledger.R;
import com.gxg.ledger.model.GiftBook;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddBookDialog extends AlertDialog {
    private TextInputEditText editTextBookName;
    private TextInputEditText editTextDescription;
    private Button buttonSave;
    private Button buttonCancel;
    private GiftBook existingBook;
    private OnBookSavedListener listener;
    
    public interface OnBookSavedListener {
        void onBookSaved(GiftBook book);
        void onBookUpdated(GiftBook book);
    }
    
    public AddBookDialog(@NonNull Context context, OnBookSavedListener listener) {
        super(context);
        this.listener = listener;
        this.existingBook = null;
    }
    
    public AddBookDialog(@NonNull Context context, GiftBook book, OnBookSavedListener listener) {
        super(context);
        this.listener = listener;
        this.existingBook = book;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_add_book, null);
        setView(view);
        
        editTextBookName = view.findViewById(R.id.editTextBookName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        
        // 如果是编辑模式，填充现有数据
        if (existingBook != null) {
            setTitle(getContext().getString(R.string.edit_book));
            editTextBookName.setText(existingBook.getName());
            editTextDescription.setText(existingBook.getDescription());
        } else {
            setTitle(getContext().getString(R.string.add_book));
        }
        
        setupListeners();
        super.onCreate(savedInstanceState);
    }
    
    private void setupListeners() {
        buttonSave.setOnClickListener(v -> saveBook());
        buttonCancel.setOnClickListener(v -> dismiss());
    }
    
    private void saveBook() {
        String name = editTextBookName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        
        if (name.isEmpty()) {
            editTextBookName.setError(getContext().getString(R.string.name_required));
            editTextBookName.requestFocus();
            return;
        }
        
        if (existingBook != null) {
            // 更新现有礼金簿
            existingBook.setName(name);
            existingBook.setDescription(description);
            if (listener != null) {
                listener.onBookUpdated(existingBook);
            }
        } else {
            // 创建新礼金簿
            GiftBook newBook = new GiftBook(name, description);
            if (listener != null) {
                listener.onBookSaved(newBook);
            }
        }
        
        dismiss();
    }
}