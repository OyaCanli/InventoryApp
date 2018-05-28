package com.example.oya.inventoryapp.model;

public final class Enterprise {

    private String enterpriseName;
    private String enterprisePhone;
    private String enterpriseAddress;
    private String enterpriseEmail;
    private String enterpriseContactPerson;

    public Enterprise(String enterpriseName, String enterprisePhone, String enterpriseAddress, String enterpriseEmail, String enterpriseContactPerson) {
        this.enterpriseName = enterpriseName;
        this.enterprisePhone = enterprisePhone;
        this.enterpriseAddress = enterpriseAddress;
        this.enterpriseEmail = enterpriseEmail;
        this.enterpriseContactPerson = enterpriseContactPerson;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public String getEnterprisePhone() {
        return enterprisePhone;
    }

    public String getEnterpriseAddress() {
        return enterpriseAddress;
    }

    public String getEnterpriseEmail() {
        return enterpriseEmail;
    }

    public String getEnterpriseContactPerson() {
        return enterpriseContactPerson;
    }
}
