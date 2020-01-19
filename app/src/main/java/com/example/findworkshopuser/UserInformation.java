package com.example.findworkshopuser;

public class UserInformation {
    private String fullName;
    private String userName;
    private String email;
    private String password;
    private String confirmPassword;
    private String spintext;
    private int role;

    public UserInformation(){}

    public UserInformation(String fullName,String userName,String email,String password,String confirmPassword,String spintext, int role){
        this.fullName=fullName;
        this.userName=userName;
        this.email=email;
        this.password=password;
        this.confirmPassword=confirmPassword;
        this.spintext=spintext;
        this.role=role;
    }
    public UserInformation(String fullName,String userName,String email,String password,String spintext, int role){
        this.fullName=fullName;
        this.userName=userName;
        this.email=email;
        this.password=password;
        this.spintext=spintext;
        this.role=role;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getSpintext() {
        return spintext;
    }

    public void setSpintext(String spintext) {
        this.spintext = spintext;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}

