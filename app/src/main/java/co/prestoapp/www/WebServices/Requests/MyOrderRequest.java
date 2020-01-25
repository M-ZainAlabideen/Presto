package co.prestoapp.www.WebServices.Requests;

public class MyOrderRequest {
    private String api_token;
    private Integer page_number;


    public MyOrderRequest(String api_token,int page_number){
        this.api_token = api_token;
        this.page_number = page_number;
    }
}
