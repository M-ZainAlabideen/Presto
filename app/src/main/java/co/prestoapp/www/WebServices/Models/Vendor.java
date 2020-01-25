package co.prestoapp.www.WebServices.Models;

import java.util.ArrayList;

public class Vendor {
    private int id;
    private String name;
    private String image;
    private String location;
    private String vendor_category;
    private String product_category;

    private ArrayList<Product> products;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVendor_category() {
        return vendor_category;
    }

    public void setVendor_category(String vendor_category) {
        this.vendor_category = vendor_category;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
