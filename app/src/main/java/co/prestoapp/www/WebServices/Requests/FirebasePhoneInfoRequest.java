package co.prestoapp.www.WebServices.Requests;

public class FirebasePhoneInfoRequest {
    private String api_token;
    private String phone;
    private String idToken;

    public FirebasePhoneInfoRequest(String api_token,String phone, String idToken) {
        this.api_token = api_token;
        this.phone = phone;
        this.idToken = idToken;
    }
}
