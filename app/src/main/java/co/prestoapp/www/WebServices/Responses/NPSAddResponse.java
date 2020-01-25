package co.prestoapp.www.WebServices.Responses;

import co.prestoapp.www.WebServices.Models.Response;

public class NPSAddResponse {
    private String code;
    private Response response;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
