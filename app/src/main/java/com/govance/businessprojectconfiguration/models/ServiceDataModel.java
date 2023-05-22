package com.govance.businessprojectconfiguration.models;

import java.io.Serializable;

public class ServiceDataModel implements Serializable {

    String serviceId;
    String serviceOwnerId;
    String  title;
    String  description;
    String  dateAdded;
    long totalRequests;
    long numberOfNewRequests;
    public ServiceDataModel(String serviceId,String serviceOwnerId,String  title,String  description,String  dateAdded,long totalRequests,long numberOfNewRequests){
                this.serviceId = serviceId;
                this.serviceOwnerId = serviceOwnerId;
                this.title = title;
                this.description = description;
                this.dateAdded = dateAdded;
                this.totalRequests = totalRequests;
                this.numberOfNewRequests = numberOfNewRequests;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceOwnerId() {
        return serviceOwnerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getNumberOfNewRequests() {
        return numberOfNewRequests;
    }
}

