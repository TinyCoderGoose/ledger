package com.gxg.ledger.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.repository.GiftBookRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private GiftBookRepository repository;
    private MutableLiveData<List<GiftBook>> allBooks;
    
    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new GiftBookRepository(application);
        allBooks = new MutableLiveData<>();
    }
    
    public LiveData<List<GiftBook>> getAllBooks() {
        return repository.getAllBooks();
    }
    
    public void insert(GiftBook book) {
        repository.insert(book);
    }
    
    public void update(GiftBook book) {
        repository.update(book);
    }
    
    public void delete(GiftBook book) {
        repository.delete(book);
    }
    
    public GiftBook getBookById(int id) {
        return repository.getBookById(id);
    }
    
    public List<GiftBook> searchBooks(String searchTerm) {
        return repository.searchBooks(searchTerm);
    }
}