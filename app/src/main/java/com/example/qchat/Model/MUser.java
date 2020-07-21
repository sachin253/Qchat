package com.example.qchat.Model;

public class MUser
{
    private String email;
    private String userName;
    private String imageURL;
    private String userId;
    private  String status;


    public MUser(String email, String userName, String imageURL, String userId,String status)
    {
        this.email = email;
        this.userName = userName;
        this.imageURL = imageURL;
        this.userId = userId;
        this.status = status;
    }
    public MUser()
    {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
