package co.prestoapp.www.WebServices.Requests;

public class LoginRequest {
private String phone;
private String verification_code;

    public LoginRequest(String phone, String verification_code) {
        this.phone = phone;
        this.verification_code = verification_code;
    }
}
