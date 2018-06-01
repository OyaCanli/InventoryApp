package com.example.oya.inventoryapp.model;

public class Transaction {

    private String enterpriseName;
    private String productName;
    private int quantity;
    private float price;
    private String date;
    private String transactionType;

    public Transaction(String enterpriseName, String productName, int quantity, float price, String date, String transactionType) {
        this.enterpriseName = enterpriseName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.transactionType = transactionType;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
