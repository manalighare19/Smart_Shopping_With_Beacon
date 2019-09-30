package com.example.inclass03_amad;

public class Product {
    String discount;
    String name;
    String photo;
    String price;
    String region;

    public Product(String discount, String name, String photo, String price, String region) {
        this.discount = discount;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.region = region;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


}
