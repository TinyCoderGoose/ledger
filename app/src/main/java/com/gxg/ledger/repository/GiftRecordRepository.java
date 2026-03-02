package com.gxg.ledger.repository;

import android.app.Application;

import com.gxg.ledger.database.AppDatabase;
import com.gxg.ledger.database.GiftRecordDao;
import com.gxg.ledger.model.GiftRecord;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GiftRecordRepository {
    private GiftRecordDao giftRecordDao;
    private ExecutorService executorService;
    
    public GiftRecordRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        giftRecordDao = db.giftRecordDao();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    public void insert(GiftRecord record) {
        executorService.execute(() -> giftRecordDao.insert(record));
    }
    
    public void update(GiftRecord record) {
        executorService.execute(() -> giftRecordDao.update(record));
    }
    
    public void delete(GiftRecord record) {
        executorService.execute(() -> giftRecordDao.delete(record));
    }
    
    public List<GiftRecord> getRecordsByBookId(int bookId) {
        return giftRecordDao.getRecordsByBookId(bookId);
    }
    
    // 同步版本，用于导出功能
    public List<GiftRecord> getRecordsByBookIdSync(int bookId) {
        return giftRecordDao.getRecordsByBookId(bookId);
    }
    
    public GiftRecord getRecordById(int id) {
        return giftRecordDao.getRecordById(id);
    }
    
    public List<GiftRecord> searchRecords(int bookId, String searchTerm) {
        return giftRecordDao.searchRecords(bookId, searchTerm);
    }
    
    public int getRecordCount(int bookId) {
        return giftRecordDao.getRecordCount(bookId);
    }
    
    public Double getTotalAmount(int bookId) {
        return giftRecordDao.getTotalAmount(bookId);
    }
    
    public Double getReturnedAmount(int bookId) {
        return giftRecordDao.getReturnedAmount(bookId);
    }
    
    public List<GiftRecord> getUnreturnedRecords(int bookId) {
        return giftRecordDao.getUnreturnedRecords(bookId);
    }
    
    public void deleteRecordsByBookId(int bookId) {
        executorService.execute(() -> giftRecordDao.deleteRecordsByBookId(bookId));
    }
}