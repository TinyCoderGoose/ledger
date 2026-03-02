package com.gxg.ledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gxg.ledger.R;
import com.gxg.ledger.model.GiftRecord;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GiftRecordAdapter extends RecyclerView.Adapter<GiftRecordAdapter.RecordViewHolder> {
    private List<GiftRecord> recordList;
    private OnRecordClickListener listener;
    
    public interface OnRecordClickListener {
        void onRecordClick(GiftRecord record);
        void onRecordLongClick(GiftRecord record);
        void onMarkReturnClick(GiftRecord record);
        void onEditClick(GiftRecord record);
        void onDeleteClick(GiftRecord record);
    }
    
    public GiftRecordAdapter(List<GiftRecord> recordList, OnRecordClickListener listener) {
        this.recordList = recordList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gift_record, parent, false);
        return new RecordViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        GiftRecord record = recordList.get(position);
        holder.bind(record);
    }
    
    @Override
    public int getItemCount() {
        return recordList.size();
    }
    
    class RecordViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPersonName;
        private TextView textViewAddress;
        private TextView textViewAmount;
        private TextView textViewEventDate;
        private Chip chipReturnStatus;
        private TextView textViewNotes;
        private MaterialButton buttonMarkReturn;
//        private ImageButton buttonEdit;
        private ImageButton buttonDelete;
        private GiftRecord record;
        
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPersonName = itemView.findViewById(R.id.textViewPersonName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);
            chipReturnStatus = itemView.findViewById(R.id.chipReturnStatus);
            textViewNotes = itemView.findViewById(R.id.textViewNotes);
            buttonMarkReturn = itemView.findViewById(R.id.buttonMarkReturn);
//            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
        
        public void bind(GiftRecord record) {
            textViewPersonName.setText(record.getPersonName());
            
            // 显示地址（如果有）
            if (record.getAddress() != null && !record.getAddress().isEmpty()) {
                textViewAddress.setText(record.getAddress());
            } else {
                textViewAddress.setText("");
            }
            
            // 格式化金额
            DecimalFormat df = new DecimalFormat("#,##0.00");
            textViewAmount.setText("¥" + df.format(record.getAmount()));
            
            // 格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            textViewEventDate.setText(sdf.format(new Date(record.getEventDate())));
            
            // 设置还礼状态
            if (record.isReturned()) {
                chipReturnStatus.setText(R.string.returned);
                chipReturnStatus.setChipBackgroundColorResource(android.R.color.holo_green_light);
                chipReturnStatus.setTextColor(itemView.getResources().getColor(android.R.color.white));
                buttonMarkReturn.setText(R.string.already_returned);
                buttonMarkReturn.setEnabled(false);
            } else {
                chipReturnStatus.setText(R.string.not_returned);
                chipReturnStatus.setChipBackgroundColorResource(android.R.color.holo_red_light);
                chipReturnStatus.setTextColor(itemView.getResources().getColor(android.R.color.white));
                buttonMarkReturn.setText(R.string.mark_return);
                buttonMarkReturn.setEnabled(true);
            }
            
            // 保存记录引用
            this.record = record;
            
            // 显示备注（如果有）
            if (record.getNotes() != null && !record.getNotes().isEmpty()) {
                textViewNotes.setText(record.getNotes());
                textViewNotes.setVisibility(View.VISIBLE);
            } else {
                textViewNotes.setVisibility(View.GONE);
            }
            
            // 设置操作按钮点击事件
            buttonMarkReturn.setOnClickListener(v -> {
                if (!record.isReturned() && listener != null) {
                    listener.onMarkReturnClick(record);
                }
            });
            
//            buttonEdit.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onEditClick(record);
//                }
//            });
            
            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(record);
                }
            });
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecordClick(record);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onRecordLongClick(record);
                }
                return true;
            });
        }
    }
}