package co.prestoapp.www.WebServices.Models;

public class User {

    private String id;
    private String name;
    private String phone;
    private Boolean phone_verified;
    private String email;
    private String referral_code;
    private String referrer_id;
    private Float balance;
    private String address;
    private String social_token;
    private String social_platform;
    private String account_type;
    private String firebase_token;
    private String region_id;
    private Region region;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(Boolean phone_verified) {
        this.phone_verified = phone_verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

    public String getReferrer_id() {
        return referrer_id;
    }

    public void setReferrer_id(String referrer_id) {
        this.referrer_id = referrer_id;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSocial_token() {
        return social_token;
    }

    public void setSocial_token(String social_token) {
        this.social_token = social_token;
    }

    public String getSocial_platform() {
        return social_platform;
    }

    public void setSocial_platform(String social_platform) {
        this.social_platform = social_platform;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

}
