package co.prestoapp.www.WebServices.Requests;

import android.support.annotation.Nullable;

public class SocialLoginRequest {
    private String name;
    private String phone;
    private String email;
    private String social_token;
    private String social_platform;

    public SocialLoginRequest(String name, @Nullable String phone,@Nullable String email, String social_token, String social_platform) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.social_token = social_token;
        this.social_platform = social_platform;
    }
}
