package co.prestoapp.www.Models;

public class StatusProductsModel {
    private String product_name;
    private String price;
    private String qty;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public StatusProductsModel(String product_name, String price, String qty) {
        this.product_name = product_name;
        this.price = price;
        this.qty = qty;
    }
}
