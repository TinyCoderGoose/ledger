package com.gxg.ledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gxg.ledger.R;
import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.repository.GiftRecordRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<GiftBook> bookList;
    private OnBookClickListener listener;
    private GiftRecordRepository recordRepository;
    
    public interface OnBookClickListener {
        void onBookClick(GiftBook book);
        void onBookLongClick(GiftBook book);
    }
    
    public BookAdapter(List<GiftBook> bookList, OnBookClickListener listener, GiftRecordRepository recordRepository) {
        this.bookList = bookList;
        this.listener = listener;
        this.recordRepository = recordRepository;
    }
    
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gift_book, parent, false);
        return new BookViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        GiftBook book = bookList.get(position);
        holder.bind(book);
    }
    
    @Override
    public int getItemCount() {
        return bookList.size();
    }
    
    class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewBookName;
        private TextView textViewDescription;
        private TextView textViewRecordCount;
        private TextView textViewTotalAmount;
        private TextView textViewReturnAmount;
        private TextView textViewLastUpdated;
        private ImageButton buttonMore;
        
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookName = itemView.findViewById(R.id.textViewBookName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewRecordCount = itemView.findViewById(R.id.textViewRecordCount);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            textViewReturnAmount = itemView.findViewById(R.id.textViewReturnAmount);
            textViewLastUpdated = itemView.findViewById(R.id.textViewLastUpdated);
            buttonMore = itemView.findViewById(R.id.buttonMore);
        }
        
        public void bind(GiftBook book) {
            textViewBookName.setText(book.getName());
            textViewDescription.setText(book.getDescription());
            
            // 异步获取真实统计信息
            new Thread(() -> {
                try {
                    int recordCount = recordRepository.getRecordCount(book.getId());
                    Double totalAmount = recordRepository.getTotalAmount(book.getId());
                    Double returnAmount = recordRepository.getReturnedAmount(book.getId());
                    
                    // 在主线程更新UI
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> {
                        textViewRecordCount.setText(String.valueOf(recordCount));
                        textViewTotalAmount.setText(formatCurrency(totalAmount));
                        textViewReturnAmount.setText(formatCurrency(returnAmount));
                    });
                } catch (Exception e) {
                    // 出错时显示默认值
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> {
                        textViewRecordCount.setText("0");
                        textViewTotalAmount.setText("¥0.00");
                        textViewReturnAmount.setText("¥0.00");
                    });
                }
            }).start();
            
            // 格式化时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String lastUpdated = sdf.format(new Date(book.getUpdatedAt()));
            textViewLastUpdated.setText("最后更新: " + lastUpdated);
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(book);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onBookLongClick(book);
                }
                return true;
            });
            
            buttonMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookLongClick(book);
                }
            });
        }
        
        private String formatCurrency(Double amount) {
            if (amount == null) return "¥0.00";
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
            return "¥" + df.format(amount);
        }
    }
}