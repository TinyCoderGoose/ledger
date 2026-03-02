package com.gxg.ledger.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "gift_records",
    foreignKeys = @ForeignKey(
        entity = GiftBook.class,
        parentColumns = "id",
        childColumns = "bookId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("bookId")}
)
public class GiftRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int bookId;
    private String personName;
    private double amount;
    private String phoneNumber;
    private String address;
    private long eventDate;
    private long recordDate;
    private String notes;
    private boolean isReturned; // 是否已还礼
    private long returnDate; // 还礼日期
    private String returnNotes; // 还礼备注

    public GiftRecord(int bookId, String personName, double amount) {
        this.bookId = bookId;
        this.personName = personName;
        this.amount = amount;
        this.recordDate = System.currentTimeMillis();
        this.eventDate = System.currentTimeMillis();
        this.isReturned = false;
        this.returnDate = 0;
        this.returnNotes = null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
        
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public long getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(long recordDate) {
        this.recordDate = recordDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
        if (returned && returnDate == 0) {
            returnDate = System.currentTimeMillis();
        }
    }

    public long getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(long returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getReturnNotes() {
        return returnNotes;
    }
    
    public void setReturnNotes(String returnNotes) {
        this.returnNotes = returnNotes;
    }
}