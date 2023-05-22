package com.govance.businessprojectconfiguration.models;

public class ProductOrderDataModel {
    String productImageDownloadUrl;
    String productId;
    String productOwnerId;
    String productName;
    String orderId;
    String customerId;
    String dateOrdered;
    String orderDescription;
    String customerContactPhoneNumber;
    String customerContactEmail;
    String customerContactAddress;
    String customerContactLocation;
    boolean isResolved;
    boolean isOwnerSeen;

    public ProductOrderDataModel(
            String productId,
            String productOwnerId,
            String productName,
            String productImageDownloadUrl,
            String orderId,
            String customerId,
            String dateOrdered,
            String orderDescription,
            String customerContactPhoneNumber,
            String customerContactEmail,
            String customerContactAddress,
            String customerContactLocation,
            boolean isResolved,
            boolean isOwnerSeen
    ){
        this.productImageDownloadUrl = productImageDownloadUrl;
        this.productId = productId;
        this.productOwnerId = productOwnerId;
        this.productName = productName;
        this.orderId = orderId;
        this.customerId = customerId;
        this.dateOrdered = dateOrdered;
        this.orderDescription = orderDescription;
        this.customerContactPhoneNumber = customerContactPhoneNumber;
        this.customerContactEmail = customerContactEmail;
        this.customerContactAddress = customerContactAddress;
        this.customerContactLocation = customerContactLocation;
        this.isResolved = isResolved;
        this.isOwnerSeen = isOwnerSeen;
    }

    public String getProductImageDownloadUrl() {
        return productImageDownloadUrl;
    }

    public String getProductId() {
        return productId;
    }
    public String getProductOwnerId() {
        return productOwnerId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public String getCustomerContactPhoneNumber() {
        return customerContactPhoneNumber;
    }

    public String getCustomerContactEmail() {
        return customerContactEmail;
    }

    public String getCustomerContactAddress() {
        return customerContactAddress;
    }

    public String getCustomerContactLocation() {
        return customerContactLocation;
    }
    public boolean isResolved() {
        return isResolved;
    }
    public boolean isOwnerSeen() {
        return isOwnerSeen;
    }
}

