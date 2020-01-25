package co.prestoapp.www.WebServices.Requests;

public class SocialValidationRequest {
    private String api_token;
    private String code;

    public SocialValidationRequest(String api_token, String code) {
        this.api_token = api_token;
        this.code = code;
    }
}
