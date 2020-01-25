package co.prestoapp.www.WebServices.Requests;

public class FirebasePhoneAuthRequest {
    private String api_token;
    private String phone;
    private String verificationId;
    private String code;

    public FirebasePhoneAuthRequest(String api_token,String phone,String verificationId, String code) {
        this.api_token = api_token;
        this.phone = phone;
        this.verificationId = verificationId;
        this.code = code;
    }
}
