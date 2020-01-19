package com.example.findworkshopuser;

public class Workshopinformation {

    private String Image;
    String name;
    private String address;
    private String contact;
    private String spintext;
    private double latitude;
    private double longitude;
    private double average;


    public Workshopinformation(){

    }
      Workshopinformation(String name,String address,String contact,String spintext,double latitude,double longitude) {
        this.name=name;
        this.address=address;
        this.contact=contact;
        this.spintext=spintext;
        this.latitude=latitude;
        this.longitude=longitude;


    }

    Workshopinformation(String Image, String name, String address, String contact, String spintext, double latitude, double longitude, double average, int role){
        this.Image=Image;
        this.name=name;
        this.address=address;
        this.contact=contact;
        this.spintext=spintext;
        this.latitude=latitude;
        this.longitude=longitude;
        this.average=average;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSpintext() {
        return spintext;
    }

    public void setSpintext(String spintext) {
        this.spintext = spintext;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
