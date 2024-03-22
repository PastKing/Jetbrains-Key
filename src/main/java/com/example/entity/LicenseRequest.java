package com.example.entity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: PastKing
 * @Date: 2024/03/22/9:19
 * @Description:
 */
public class LicenseRequest {
    private String licenseId;
    private String date;
    private List<Product> products;

    // Getters and setters
    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
