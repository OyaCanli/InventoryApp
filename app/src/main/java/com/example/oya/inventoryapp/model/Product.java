package com.example.oya.inventoryapp.model;

public final class Product {

    private String productName;
    private float salePrice;
    private int quantityInStock;
    private String supplierName;

    public Product(String productName, float salePrice, int quantityInStock, String supplierName) {
        this.productName = productName;
        this.salePrice = salePrice;
        this.quantityInStock = quantityInStock;
        this.supplierName = supplierName;
    }

    public String getProductName() {
        return productName;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public String getSupplierName() {
        return supplierName;
    }
}
