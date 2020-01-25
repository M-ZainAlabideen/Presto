package co.prestoapp.www.WebServices.Models;

import java.util.ArrayList;

public class Response {

    private boolean account_exists;
    private User user;
    private String api_token;
    private String idToken;
    private String message;
    private ArrayList<Region> regions;
    private String phone_verified;
    private ArrayList<Vendor> vendors;
    private ArrayList<Order> orders;
    private ArrayList<Slider> slides;
    private Integer current_page;
    private Integer last_page;
    private Vendor vendor;
    private Order order;
    private float old_total;
    private float discount;
    private float new_total;
    private boolean reveal;
    private NPS nps;
    private ArrayList<String> addresses;
    private Report report;


    public boolean isAccount_exists() {
        return account_exists;
    }

    public void setAccount_exists(boolean account_exists) {
        this.account_exists = account_exists;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }

    public String getPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(String phone_verified) {
        this.phone_verified = phone_verified;
    }

    public ArrayList<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(ArrayList<Vendor> vendors) {
        this.vendors = vendors;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public ArrayList<Slider> getSlides() {
        return slides;
    }

    public void setSlides(ArrayList<Slider> slides) {
        this.slides = slides;
    }


    public Integer getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(Integer current_page) {
        this.current_page = current_page;
    }

    public Integer getLast_page() {
        return last_page;
    }

    public void setLast_page(Integer last_page) {
        this.last_page = last_page;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public float getOld_total() {
        return old_total;
    }

    public void setOld_total(float old_total) {
        this.old_total = old_total;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getNew_total() {
        return new_total;
    }

    public void setNew_total(float new_total) {
        this.new_total = new_total;
    }

    public boolean isReveal() {
        return reveal;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }

    public NPS getNps() {
        return nps;
    }

    public void setNps(NPS nps) {
        this.nps = nps;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
