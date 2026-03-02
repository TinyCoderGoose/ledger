package com.gxg.ledger.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.gxg.ledger.model.GiftBook;

import java.util.List;

@Dao
public interface GiftBookDao {
    
    @Insert
    long insert(GiftBook book);
    
    @Update
    void update(GiftBook book);
    
    @Delete
    void delete(GiftBook book);
    
    @Query("SELECT * FROM gift_books ORDER BY updatedAt DESC")
    LiveData<List<GiftBook>> getAllBooks();
    
    @Query("SELECT * FROM gift_books WHERE id = :id")
    GiftBook getBookById(int id);
    
    @Query("SELECT * FROM gift_books WHERE name LIKE '%' || :searchTerm || '%' OR description LIKE '%' || :searchTerm || '%'")
    List<GiftBook> searchBooks(String searchTerm);
    
    @Query("DELETE FROM gift_books")
    void deleteAll();
}