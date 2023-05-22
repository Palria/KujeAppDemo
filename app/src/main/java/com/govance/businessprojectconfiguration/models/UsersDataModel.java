package com.govance.businessprojectconfiguration.models;


import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

/**This class is the user's data model*/
public class UsersDataModel implements Serializable {
    /**The username of the user*/
    String userName;
    /**The user Id of the user*/
    String userId;
    /**
     * The url that downloads the user's profile image/avatar
     * */
    String userProfileImageDownloadUrl;
    /**The user's gender/sex*/
    String gender;
    /**The date when the user created his profile */
    String dateRegistered;
    /**The user's mobile number*/
    String userPhoneNumber;
    /**The user's email address*/
    String userEmail;
    /**The user's country of origin*/
    String userCountryOfOrigin;




    /**Empty constructor may be used to instantiate the class object if it is not
     * necessary to instantiate the global variables through the constructor. In this way
     *Setters are used to as an alternative instantiate the global variables .
     * */
    public UsersDataModel(){

    }
    /**This parameterized Constructor helps us in initializing all the global variables
     * */
    public UsersDataModel(
            String userName,
            String userId,
            String userProfileImageDownloadUrl,
            String gender,
            String dateRegistered,
            String userPhoneNumber,
            String userEmail,
            String userCountryOfOrigin
    ){
        this.userName = userName;
        this.userId = userId;
        this.userProfileImageDownloadUrl = userProfileImageDownloadUrl;
        this.gender = gender;
        this.dateRegistered = dateRegistered;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.userCountryOfOrigin = userCountryOfOrigin;
    }


    //The getters are for querying  the queried data and setters for setting the data

    public String getUserName(){
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId(){
        return userId;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfileImageDownloadUrl() {
        return userProfileImageDownloadUrl;
    }

    void setUserProfileImageDownloadUrl(String userProfileImageDownloadUrl) {
        this.userProfileImageDownloadUrl = userProfileImageDownloadUrl;
    }


    public String getGender() {
        return gender;
    }

    void setGender(String gender) {
        this.gender = gender;
    }


    public String getDateRegistered() {
        return dateRegistered;
    }

    void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserCountryOfOrigin() {
        return userCountryOfOrigin;
    }

    void setUserCountryOfOrigin(String userCountryOfOrigin) {
        this.userCountryOfOrigin = userCountryOfOrigin;
    }


}
