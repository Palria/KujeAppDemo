package com.govance.businessprojectconfiguration;

public class CatalogDataModel {
        String catalogId;
        String serviceId;
        String catalogTitle;
        String dateAdded;
        String catalogDescription;
        String catalogCoverPhotoDownloadUrl;

        public CatalogDataModel(
                String catalogId,
                String serviceId,
                String catalogTitle,
                String dateAdded,
                String catalogDescription,
                String catalogCoverPhotoDownloadUrl
        ){
            this.catalogId = catalogId;
            this.serviceId = serviceId;
            this.catalogTitle =catalogTitle;
            this.dateAdded = dateAdded;
            this.catalogDescription = catalogDescription;
            this.catalogCoverPhotoDownloadUrl = catalogCoverPhotoDownloadUrl;
        }


        public String getCatalogId() {
            return catalogId;
        }
        public String getServiceId() {
            return serviceId;
        }

        public String getCatalogTitle() {
            return catalogTitle;
        }

        public String getDateAdded() {
            return dateAdded;
        }

        public String getCatalogDescription() {
            return catalogDescription;
        }

        public String getCatalogCoverPhotoDownloadUrl() {
            return catalogCoverPhotoDownloadUrl;
        }

    }

