package co.prestoapp.www.WebServices.Requests;

public class ReportRequest {
    private String api_token;
    private int order_id;
    private String details;

    public ReportRequest(String api_token, int order_id, String details) {
        this.api_token = api_token;
        this.order_id = order_id;
        this.details = details;
    }
}
