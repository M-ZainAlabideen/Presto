package co.prestoapp.www.WebServices.Requests;

import android.support.annotation.Nullable;

import co.prestoapp.www.Models.VendorDetailsModel;

public class AddOrderRequest {
    private String api_token;
    private int delivery_fees;
    private String region_id;
    private String address;
    private String notes;
    private VendorDetailsModel[] items;
    String promo_code;

    public AddOrderRequest(String api_token, int delivery_fees, String region_id, String address, @Nullable String notes,VendorDetailsModel[] items,String promo_code) {
        this.api_token = api_token;
        this.delivery_fees = delivery_fees;
        this.region_id = region_id;
        this.address = address;
        this.notes = notes;
        this.items = items;
        this.promo_code = promo_code;
    }
}
