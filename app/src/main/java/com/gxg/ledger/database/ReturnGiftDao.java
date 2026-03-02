package com.gxg.ledger.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gxg.ledger.model.ReturnGift;

import java.util.List;

@Dao
public interface ReturnGiftDao {
    
    @Insert
    long insert(ReturnGift returnGift);
    
    @Update
    void update(ReturnGift returnGift);
    
    @Delete
    void delete(ReturnGift returnGift);
    
    @Query("SELECT * FROM return_gifts WHERE bookId = :bookId ORDER BY returnDate DESC")
    List<ReturnGift> getReturnsByBookId(int bookId);
    
    @Query("SELECT * FROM return_gifts WHERE recordId = :recordId")
    List<ReturnGift> getReturnsByRecordId(int recordId);
    
    @Query("SELECT * FROM return_gifts WHERE id = :id")
    ReturnGift getReturnById(int id);
    
    @Query("SELECT SUM(amount) FROM return_gifts WHERE bookId = :bookId")
    Double getTotalReturnAmount(int bookId);
    
    @Query("DELETE FROM return_gifts WHERE bookId = :bookId")
    void deleteReturnsByBookId(int bookId);
    
    @Query("DELETE FROM return_gifts WHERE recordId = :recordId")
    void deleteReturnsByRecordId(int recordId);
}