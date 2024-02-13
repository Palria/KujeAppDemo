package com.palria.kujeapp.models;

public class RequestDataModel {
    String serviceId;
    String serviceTitle;
    String requestId;
    String customerId;
    String dateRequested;
    String requestDescription;
    String customerContactPhoneNumber;
    String customerContactEmail;
    String customerContactAddress;
    String customerContactLocation;
    boolean isResolved;

    public RequestDataModel(
            String serviceId,
            String serviceTitle,
            String requestId,
            String customerId,
            String dateRequested,
            String requestDescription,
            String customerContactPhoneNumber,
            String customerContactEmail,
            String customerContactAddress,
            String customerContactLocation,
            boolean isResolved
    ){
        this.serviceId = serviceId;
        this.serviceTitle =serviceTitle;
        this.requestId = requestId;
        this.customerId = customerId;
        this.dateRequested = dateRequested;
        this.requestDescription = requestDescription;
        this.customerContactPhoneNumber = customerContactPhoneNumber;
        this.customerContactEmail = customerContactEmail;
        this.customerContactAddress = customerContactAddress;
        this.customerContactLocation = customerContactLocation;
        this.isResolved = isResolved;
    }


    public String getServiceId() {
        return serviceId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRequestDescription() {
        return requestDescription;
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
}

