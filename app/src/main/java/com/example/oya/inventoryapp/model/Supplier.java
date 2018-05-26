package com.example.oya.inventoryapp.model;

public final class Supplier {

    private String supplierName;
    private String supplierPhone;
    private String supplierAddress;
    private String supplierEmail;
    private String supplierContactPerson;

    public Supplier(String supplierName, String supplierPhone, String supplierAddress, String supplierEmail, String supplierContactPerson) {
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
        this.supplierAddress = supplierAddress;
        this.supplierEmail = supplierEmail;
        this.supplierContactPerson = supplierContactPerson;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public String getSupplierContactPerson() {
        return supplierContactPerson;
    }
}
