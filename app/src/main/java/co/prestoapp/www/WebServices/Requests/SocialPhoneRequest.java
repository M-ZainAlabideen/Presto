package co.prestoapp.www.WebServices.Requests;

import com.google.gson.annotations.SerializedName;

public class SocialPhoneRequest {
    @SerializedName("api_token")
    private String api_token;
    @SerializedName("phone")
    private String phone;

    public SocialPhoneRequest(String api_token, String phone) {
        this.api_token = api_token;
        this.phone = phone;
    }
}
