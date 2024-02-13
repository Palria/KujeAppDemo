package com.palria.kujeapp;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductDataModel implements Serializable {

    String productId ;
    String productOwnerId ;
    String productTitle;
    String productPrice;
    String productDescription ;
    String location;
    String phone;
    String email;
    String residentialAddress;
    boolean isSold;
    String datePosted;
    long productViewCount;
    long productOrderCount;
    long productNewOrderCount;
    ArrayList<String> imageUrlList ;
    boolean isPrivate;
    boolean isFromSubmission;
    boolean isApproved;
                public ProductDataModel(String productId,String productOwnerId, String productTitle,  String productPrice, String productDescription, String location, String phone, String email, String residentialAddress, boolean isSold,String datePosted,long productViewCount,long productOrderCount,long productNewOrderCount, ArrayList<String> imageUrlList, boolean isPrivate, boolean isFromSubmission, boolean isApproved){
                    this.productId = productId;
                    this.productOwnerId = productOwnerId;
                    this.productTitle=productTitle;
                    this.productPrice=productPrice;
                    this.productDescription= productDescription;
                    this.location=location;
                    this.phone=phone;
                    this.email=email;
                    this.residentialAddress=residentialAddress;
                    this.isSold=isSold;
                    this.datePosted=datePosted;
                    this.productViewCount=productViewCount;
                    this.productOrderCount=productOrderCount;
                    this.productNewOrderCount=productNewOrderCount;
                    this.imageUrlList= imageUrlList;
                    this.isPrivate=isPrivate;
                    this.isFromSubmission=isFromSubmission;
                    this.isApproved=isApproved;
                }
    public  String getProductId(){
                    return productId;
                }

    public  String getProductOwnerId() {
        return productOwnerId;
    }
    public  String getProductTitle() {
        return productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public  String getProductDescription() {
        return productDescription;
    }

    public  String getLocation() {
        return location;
    }
    public  String getPhone() {
        return phone;
    }
    public  String getEmail() {
        return email;
    }
    public  String getResidentialAddress() {
        return residentialAddress;
    }
    public  boolean isSold() {
        return isSold;
    }
    public  boolean setIsSold(boolean isSold) {
        return this.isSold = isSold;
    }
    public  String getDatePosted() {
        return datePosted;
    }
    public  long getProductViewCount() {
        return productViewCount;
    }
    public  long getProductOrderCount() {
        return productOrderCount;
    }
    public  long getProductNewOrderCount() {
        return productNewOrderCount;
    }

    public  ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
    public boolean isFromSubmission() {
        return isFromSubmission;
    }
    public boolean isApproved() {
        return isApproved;
    }
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }
}
