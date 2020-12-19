package com.absket.in.model;

import java.util.ArrayList;

public class ProductItemBean{

    private String productName;
    private String productMainCat;
    private String productSubcat;
    private String productLongdesc;
    private String productShortdesc;
    private String productImage1;
    private String productImage2;
    private String productImage3;

    public ArrayList<ItemBean> itemBeanArrayList = new ArrayList<>();

    public ProductItemBean(){

    }


    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    private String tags;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductMainCat() {
        return productMainCat;
    }

    public void setProductMainCat(String productMainCat) {
        this.productMainCat = productMainCat;
    }

    public String getProductSubcat() {
        return productSubcat;
    }

    public void setProductSubcat(String productSubcat) {
        this.productSubcat = productSubcat;
    }

    public String getProductLongdesc() {
        return productLongdesc;
    }

    public void setProductLongdesc(String productLongdesc) {
        this.productLongdesc = productLongdesc;
    }

    public String getProductShortdesc() {
        return productShortdesc;
    }

    public void setProductShortdesc(String productShortdesc) {
        this.productShortdesc = productShortdesc;
    }

    public String getProductImage1() {
        return productImage1;
    }

    public void setProductImage1(String productImage1) {
        this.productImage1 = productImage1;
    }

    public String getProductImage2() {
        return productImage2;
    }

    public void setProductImage2(String productImage2) {
        this.productImage2 = productImage2;
    }

    public String getProductImage3() {
        return productImage3;
    }

    public void setProductImage3(String productImage3) {
        this.productImage3 = productImage3;
    }


}
