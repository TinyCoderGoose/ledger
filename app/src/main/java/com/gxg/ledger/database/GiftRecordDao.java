package com.gxg.ledger.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gxg.ledger.model.GiftRecord;

import java.util.List;

@Dao
public interface GiftRecordDao {
    
    @Insert
    long insert(GiftRecord record);
    
    @Update
    void update(GiftRecord record);
    
    @Delete
    void delete(GiftRecord record);
    
    @Query("SELECT * FROM gift_records WHERE bookId = :bookId ORDER BY eventDate DESC")
    List<GiftRecord> getRecordsByBookId(int bookId);
    
    @Query("SELECT * FROM gift_records WHERE id = :id")
    GiftRecord getRecordById(int id);
    
    @Query("SELECT * FROM gift_records WHERE bookId = :bookId AND (personName LIKE '%' || :searchTerm || '%' OR notes LIKE '%' || :searchTerm || '%') ORDER BY eventDate DESC")
    List<GiftRecord> searchRecords(int bookId, String searchTerm);
    
    @Query("SELECT COUNT(*) FROM gift_records WHERE bookId = :bookId")
    int getRecordCount(int bookId);
    
    @Query("SELECT SUM(amount) FROM gift_records WHERE bookId = :bookId")
    Double getTotalAmount(int bookId);
    
    @Query("SELECT SUM(amount) FROM gift_records WHERE bookId = :bookId AND isReturned = 1")
    Double getReturnedAmount(int bookId);
    
    @Query("SELECT * FROM gift_records WHERE bookId = :bookId AND isReturned = 0 ORDER BY eventDate DESC")
    List<GiftRecord> getUnreturnedRecords(int bookId);
    
    @Query("DELETE FROM gift_records WHERE bookId = :bookId")
    void deleteRecordsByBookId(int bookId);
}