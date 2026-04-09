package com.switchscale.cart.model;

import java.io.Serializable;

public class Cartitem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String productId;
    private String productName;
    private Double price;
    private String imageurl;
    private int quantity;

    public Cartitem() {
    }

    public Cartitem(String productId, String productName, Double price, String imageurl, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageurl = imageurl;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    

}
