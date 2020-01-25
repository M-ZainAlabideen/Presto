package co.prestoapp.www.WebServices.Requests;

import android.support.annotation.Nullable;

import co.prestoapp.www.Models.VendorDetailsModel;

public class PromoCodeRequest {
    private String api_token;
    private String promo_code;
    private VendorDetailsModel[] items;

    public PromoCodeRequest(String api_token,String promo_code, VendorDetailsModel[] items) {
        this.api_token = api_token;
        this.promo_code = promo_code;
        this.items = items;
    }
}
