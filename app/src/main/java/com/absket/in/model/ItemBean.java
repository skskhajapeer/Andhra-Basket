package com.absket.in.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemBean{
    private String id;
    private String productId;
    private String productMainCatId;
    private String productSubCatId;
    private String productMrp;
    private String productDiscount;
    private String productPrice;
    private String productUnit;
    private String productUnitprice;
    private String productBrand;
    private String productQuanity;
    private String supplierId;
    private String supplierName;
    private String productFeatured;
    private String productStatus;
    private String createdOn;

    private String productIamge;
    private String updatedOn;
    private String hasDiscount;
    private int productStock;


    public String getProductIamge() {
        return productIamge;
    }

    public void setProductIamge(String productIamge) {
        this.productIamge = productIamge;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String productName;

    public ItemBean(){

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductMainCatId() {
        return productMainCatId;
    }

    public void setProductMainCatId(String productMainCatId) {
        this.productMainCatId = productMainCatId;
    }

    public String getProductSubCatId() {
        return productSubCatId;
    }

    public void setProductSubCatId(String productSubCatId) {
        this.productSubCatId = productSubCatId;
    }

    public String getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(String productMrp) {
        this.productMrp = productMrp;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductUnitprice() {
        return productUnitprice;
    }

    public void setProductUnitprice(String productUnitprice) {
        this.productUnitprice = productUnitprice;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductQuanity() {
        return productQuanity;
    }

    public void setProductQuanity(String productQuanity) {
        this.productQuanity = productQuanity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getProductFeatured() {
        return productFeatured;
    }

    public void setProductFeatured(String productFeatured) {
        this.productFeatured = productFeatured;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(String hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }


}
