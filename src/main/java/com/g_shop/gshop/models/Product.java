package com.g_shop.gshop.models;

public class Product {

    private String pID;
    private String name;
    private String category;
    private String description;
    private String image;
    private Double price;
    private Double rating;
    private Integer quantity;
    private Integer ratingCount;

    public Product(String pID, String name, String category, String description,
    String image, Double price, Double rating, Integer quantity, Integer ratingCount) {
        this.pID = pID;
        this.name = name;
        this.category = category;
        this.description = description;
        this.image = image;
        this.price = price;
        this.rating = rating;
        this.quantity = quantity;
        this.ratingCount = ratingCount;
    }

    public String getPID() {
        return this.pID;
    }

    public void setPID(String pID) {
        this.pID = pID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRating() {
        return this.rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRatingCount() {
        return this.ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

}
