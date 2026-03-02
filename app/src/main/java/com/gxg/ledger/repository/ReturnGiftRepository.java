package com.gxg.ledger.repository;

import android.app.Application;

import com.gxg.ledger.database.AppDatabase;
import com.gxg.ledger.database.ReturnGiftDao;
import com.gxg.ledger.model.ReturnGift;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReturnGiftRepository {
    private ReturnGiftDao returnGiftDao;
    private ExecutorService executorService;
    
    public ReturnGiftRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        returnGiftDao = db.returnGiftDao();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    public void insert(ReturnGift returnGift) {
        executorService.execute(() -> returnGiftDao.insert(returnGift));
    }
    
    public void update(ReturnGift returnGift) {
        executorService.execute(() -> returnGiftDao.update(returnGift));
    }
    
    public void delete(ReturnGift returnGift) {
        executorService.execute(() -> returnGiftDao.delete(returnGift));
    }
    
    public List<ReturnGift> getReturnsByBookId(int bookId) {
        return returnGiftDao.getReturnsByBookId(bookId);
    }
    
    public List<ReturnGift> getReturnsByRecordId(int recordId) {
        return returnGiftDao.getReturnsByRecordId(recordId);
    }
    
    public ReturnGift getReturnById(int id) {
        return returnGiftDao.getReturnById(id);
    }
    
    public Double getTotalReturnAmount(int bookId) {
        return returnGiftDao.getTotalReturnAmount(bookId);
    }
    
    public void deleteReturnsByBookId(int bookId) {
        executorService.execute(() -> returnGiftDao.deleteReturnsByBookId(bookId));
    }
    
    public void deleteReturnsByRecordId(int recordId) {
        executorService.execute(() -> returnGiftDao.deleteReturnsByRecordId(recordId));
    }
}