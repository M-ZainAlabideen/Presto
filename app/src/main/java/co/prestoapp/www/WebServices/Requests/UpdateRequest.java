package co.prestoapp.www.WebServices.Requests;

import android.support.annotation.Nullable;

public class UpdateRequest {
    private String api_token;
    private String name;
    private String phone;
    private String email;
    private String referral_code;
    private Integer referrer_id;
    private Float balance;
    private String account_type;
    private Integer region_id;
    private String address;
    private String password;
    private String social_token;
    private String firebase_token;


    public UpdateRequest(String api_token,String firebase_token){

        this.api_token = api_token;
        this.firebase_token = firebase_token;
    }

    public UpdateRequest(@Nullable String api_token, @Nullable String name, @Nullable String phone, @Nullable String email, @Nullable String referral_code, @Nullable Integer referrer_id,
                         @Nullable Float balance, @Nullable String account_type, @Nullable Integer region_id, @Nullable String address, @Nullable String password, @Nullable String social_token) {
        this.api_token = api_token;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.referral_code = referral_code;
        this.referrer_id = referrer_id;
        this.balance = balance;
        this.account_type = account_type;
        this.region_id = region_id;
        this.address = address;
        this.password = password;
        this.social_token = social_token;
    }
}
