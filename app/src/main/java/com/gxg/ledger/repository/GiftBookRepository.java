package com.gxg.ledger.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.gxg.ledger.database.AppDatabase;
import com.gxg.ledger.database.GiftBookDao;
import com.gxg.ledger.model.GiftBook;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GiftBookRepository {
    private GiftBookDao giftBookDao;
    private ExecutorService executorService;
    
    public GiftBookRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        giftBookDao = db.giftBookDao();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    public LiveData<List<GiftBook>> getAllBooks() {
        return giftBookDao.getAllBooks();
    }
    
    public void insert(GiftBook book) {
        executorService.execute(() -> giftBookDao.insert(book));
    }
    
    public void update(GiftBook book) {
        executorService.execute(() -> giftBookDao.update(book));
    }
    
    public void delete(GiftBook book) {
        executorService.execute(() -> giftBookDao.delete(book));
    }
    
    public GiftBook getBookById(int id) {
        return giftBookDao.getBookById(id);
    }
    
    public List<GiftBook> searchBooks(String searchTerm) {
        // 注意：这里可能需要同步获取数据，但在实际使用中应该避免在主线程调用
        // 建议改为异步方式或者在ViewModel中处理
        return giftBookDao.searchBooks(searchTerm);
    }
    
    public void deleteAll() {
        executorService.execute(() -> giftBookDao.deleteAll());
    }
}