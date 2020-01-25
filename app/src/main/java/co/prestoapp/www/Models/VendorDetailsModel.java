package co.prestoapp.www.Models;

public class VendorDetailsModel {
    private Integer id;
    private String vendor_id;
    private String vendorName;
    private String name;
    private String price;
    private String size;
    private String description;
    private String type;
    private String numberOfOrders = "";
    private String qty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuantity() {
        return qty;
    }

    public void setQuantity(String qty) {
        this.qty = qty;
    }

    public String getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(String numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }



    public VendorDetailsModel(Integer id, String vendor_id, String vendorName, String name, String price, String size, String description, String type, String qty)
    {
        this.id = id;
        this.vendor_id = vendor_id;
        this.vendorName = vendorName;
        this.name = name;
        this.price = price;
        this.size = size;
        this.description = description;
        this.type = type;
        this.qty = qty;
    }
}
