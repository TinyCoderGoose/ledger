package com.gxg.ledger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gift_books")
public class GiftBook {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private String eventType; // 事件类型：婚礼、生日、满月等
    private long createdAt;
    private long updatedAt;

    public GiftBook(String name, String description) {
        this.name = name;
        this.description = description;
        this.eventType = "其他"; // 默认事件类型
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}