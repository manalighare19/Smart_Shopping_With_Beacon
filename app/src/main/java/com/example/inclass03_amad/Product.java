package com.example.inclass03_amad;

public class Product {
    String discount;
    String name;
    String photo;
    String price;
    String region;
    Boolean isAdded = false;

    @Override
    public String toString() {
        return "Product{" +
                "discount='" + discount + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", price='" + price + '\'' +
                ", region=" + region +
                ", isAdded='" + isAdded + '\'' +
                '}';
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
