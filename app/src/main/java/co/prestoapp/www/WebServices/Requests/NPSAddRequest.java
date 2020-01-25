package co.prestoapp.www.WebServices.Requests;

public class NPSAddRequest {
    private String api_token;
    private float rating;

    public NPSAddRequest(String api_token, float rating) {
        this.api_token = api_token;
        this.rating = rating;
    }
}
