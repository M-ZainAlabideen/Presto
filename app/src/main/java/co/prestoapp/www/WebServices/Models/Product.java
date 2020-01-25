package co.prestoapp.www.WebServices.Models;

import java.util.ArrayList;

public class Product {

    private String type;
    private ArrayList<VendorDetails> items;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<VendorDetails> getVendorDetails() {
        return items;
    }

    public void setVendorDetails(ArrayList<VendorDetails> items) {
        this.items = items;
    }
}
