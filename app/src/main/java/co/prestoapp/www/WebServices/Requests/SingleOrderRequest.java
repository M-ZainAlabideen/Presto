package co.prestoapp.www.WebServices.Requests;

public class SingleOrderRequest {
    private String api_token;
    private String order_id;

    public SingleOrderRequest(String api_token, String order_id) {
        this.api_token = api_token;
        this.order_id = order_id;
    }
}
