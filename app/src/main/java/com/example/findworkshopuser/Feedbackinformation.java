package com.example.findworkshopuser;

public class Feedbackinformation {
    private float ratingbar;
    private String ratingscale;
    private String sendfeedback;
    private String userName;

    public Feedbackinformation(){}

    public Feedbackinformation(float ratingbar, String ratingscale, String sendfeedback, String userName){
        this.ratingbar=ratingbar;
        this.ratingscale=ratingscale;
        this.sendfeedback=sendfeedback;
        this.userName=userName;


    }

    public float getRatingbar() {
        return ratingbar;
    }

    public void setRatingbar(float ratingbar) {
        this.ratingbar = ratingbar;
    }

    public String getRatingscale() {
        return ratingscale;
    }

    public void setRatingscale(String ratingscale) {
        this.ratingscale = ratingscale;
    }

    public String getSendfeedback() {
        return sendfeedback;
    }

    public void setSendfeedback(String sendfeedback) {
        this.sendfeedback = sendfeedback;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
