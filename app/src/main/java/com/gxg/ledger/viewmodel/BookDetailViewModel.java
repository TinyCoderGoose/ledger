package com.gxg.ledger.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.gxg.ledger.model.GiftRecord;
import com.gxg.ledger.model.ReturnGift;
import com.gxg.ledger.repository.GiftRecordRepository;
import com.gxg.ledger.repository.ReturnGiftRepository;

import java.util.List;

public class BookDetailViewModel extends AndroidViewModel {
    private GiftRecordRepository recordRepository;
    private ReturnGiftRepository returnRepository;
    private int bookId;
    
    public BookDetailViewModel(@NonNull Application application) {
        super(application);
        recordRepository = new GiftRecordRepository(application);
        returnRepository = new ReturnGiftRepository(application);
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    // Gift Record operations
    public void insertRecord(GiftRecord record) {
        recordRepository.insert(record);
    }
    
    public void updateRecord(GiftRecord record) {
        recordRepository.update(record);
    }
    
    public void deleteRecord(GiftRecord record) {
        recordRepository.delete(record);
    }
    
    public List<GiftRecord> getRecords() {
        return recordRepository.getRecordsByBookId(bookId);
    }
    
    public GiftRecord getRecordById(int id) {
        return recordRepository.getRecordById(id);
    }
    
    public List<GiftRecord> searchRecords(String searchTerm) {
        return recordRepository.searchRecords(bookId, searchTerm);
    }
    
    public int getRecordCount() {
        return recordRepository.getRecordCount(bookId);
    }
    
    public Double getTotalAmount() {
        return recordRepository.getTotalAmount(bookId);
    }
    
    public Double getReturnedAmount() {
        return recordRepository.getReturnedAmount(bookId);
    }
    
    public List<GiftRecord> getUnreturnedRecords() {
        return recordRepository.getUnreturnedRecords(bookId);
    }
    
    // Return Gift operations
    public void insertReturn(ReturnGift returnGift) {
        returnRepository.insert(returnGift);
    }
    
    public void updateReturn(ReturnGift returnGift) {
        returnRepository.update(returnGift);
    }
    
    public void deleteReturn(ReturnGift returnGift) {
        returnRepository.delete(returnGift);
    }
    
    public List<ReturnGift> getReturns() {
        return returnRepository.getReturnsByBookId(bookId);
    }
    
    public List<ReturnGift> getReturnsByRecordId(int recordId) {
        return returnRepository.getReturnsByRecordId(recordId);
    }
    
    public Double getTotalReturnAmount() {
        return returnRepository.getTotalReturnAmount(bookId);
    }
}