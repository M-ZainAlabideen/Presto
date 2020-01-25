package co.prestoapp.www.WebServices.Requests;

public class VendorDetailsRequest {
    private String api_token;
    private int vendor_id;

    public VendorDetailsRequest(String api_token,int vendor_id) {
        this.api_token = api_token;
        this.vendor_id = vendor_id;
    }
}
