package co.prestoapp.www.WebServices.Responses;

import com.google.gson.annotations.SerializedName;

import co.prestoapp.www.WebServices.Models.Response;

public class SocialPhoneResponse {
    @SerializedName("code")
    private String code;
    @SerializedName("response")
    private Response response;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
