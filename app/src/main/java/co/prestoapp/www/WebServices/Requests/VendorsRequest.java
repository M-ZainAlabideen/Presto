package co.prestoapp.www.WebServices.Requests;

public class VendorsRequest {
    private String api_token;
    private String vendor_category;
    private int page_number;

    public VendorsRequest(String api_token, String vendor_category,int page_number) {
        this.api_token = api_token;
        this.vendor_category = vendor_category;
        this.page_number = page_number;
    }
}
