package co.prestoapp.www.Models;

public class MyOrdersModel {
    private String Date;
    private String Status;
    private String StatusColor;
    private String Price;
    private String Address;

    public MyOrdersModel(String Date,String Status,String StatusColor,String Price,String Address){
        this.Date = Date;
        this.Status = Status;
        this.Price = Price;
        this.Address = Address;
        this.StatusColor = StatusColor;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatusColor() {
        return StatusColor;
    }

    public void setStatusColor(String statusColor) {
        StatusColor = statusColor;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }


}
