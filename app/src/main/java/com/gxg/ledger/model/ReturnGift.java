package com.gxg.ledger.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "return_gifts",
    foreignKeys = {
        @ForeignKey(
            entity = GiftBook.class,
            parentColumns = "id",
            childColumns = "bookId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = GiftRecord.class,
            parentColumns = "id",
            childColumns = "recordId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("bookId"), @Index("recordId")}
)
public class ReturnGift {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int bookId;
    private int recordId; // 对应的礼金记录ID
    private String personName;
    private double amount;
    private long returnDate;
    private String returnType; // 还礼方式：现金、礼品等
    private String notes;

    public ReturnGift(int bookId, int recordId, String personName, double amount) {
        this.bookId = bookId;
        this.recordId = recordId;
        this.personName = personName;
        this.amount = amount;
        this.returnDate = System.currentTimeMillis();
        this.returnType = "现金";
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

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
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

    public long getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(long returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}