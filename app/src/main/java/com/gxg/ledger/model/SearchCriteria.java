package com.gxg.ledger.model;

public class SearchCriteria {
    private String keyword;        // 关键词搜索
    private String address;        // 地址筛选
    private Boolean returned;      // 还礼状态筛选 (null表示不限制)
    private Double minAmount;      // 最小金额
    private Double maxAmount;      // 最大金额
    private Long startDate;        // 开始日期
    private Long endDate;          // 结束日期
    
    public SearchCriteria() {
        this.keyword = "";
        this.address = "";
        this.returned = null;
        this.minAmount = null;
        this.maxAmount = null;
        this.startDate = null;
        this.endDate = null;
    }
    
    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword != null ? keyword : "";
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address != null ? address : "";
    }
    
    public Boolean getReturned() {
        return returned;
    }
    
    public void setReturned(Boolean returned) {
        this.returned = returned;
    }
    
    public Double getMinAmount() {
        return minAmount;
    }
    
    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }
    
    public Double getMaxAmount() {
        return maxAmount;
    }
    
    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }
    
    public Long getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    
    public Long getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
    
    // 判断是否为空条件
    public boolean isEmpty() {
        return (keyword == null || keyword.isEmpty()) &&
               (address == null || address.isEmpty()) &&
               returned == null &&
               minAmount == null &&
               maxAmount == null &&
               startDate == null &&
               endDate == null;
    }
    
    // 清空所有条件
    public void clear() {
        this.keyword = "";
        this.address = "";
        this.returned = null;
        this.minAmount = null;
        this.maxAmount = null;
        this.startDate = null;
        this.endDate = null;
    }
    
    @Override
    public String toString() {
        return "SearchCriteria{" +
                "keyword='" + keyword + '\'' +
                ", address='" + address + '\'' +
                ", returned=" + returned +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}