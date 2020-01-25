package co.prestoapp.www.WebServices.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String id;
    private String status;
    private String created_at;
    private String total;
    private String subtotal;
    private String notes;
    private String delivery_fees;
    private String courier;
    private ArrayList<VendorDetails> items;
    private float discount;
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDelivery_fees() {
        return delivery_fees;
    }

    public void setDelivery_fees(String delivery_fees) {
        this.delivery_fees = delivery_fees;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public ArrayList<VendorDetails> getItems() {
        return items;
    }

    public void setItems(ArrayList<VendorDetails> items) {
        this.items = items;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
