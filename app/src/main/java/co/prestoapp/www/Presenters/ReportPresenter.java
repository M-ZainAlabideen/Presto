package co.prestoapp.www.Presenters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import co.prestoapp.www.R;
import co.prestoapp.www.Views.ReportActivity;
import co.prestoapp.www.WebServices.Requests.ReportRequest;
import co.prestoapp.www.WebServices.Responses.ReportResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ReportPresenter {
    ProgressDialog loadingDialog;
    View view;
    Context context;
    public ReportPresenter(Context context, View view){
        this.context = context;
        this.view = view;
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage(context.getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);
    }

    public void sendReport(int orderId){
        String reportStr =((EditText)view).getText().toString();
        if(reportStr.matches("") || reportStr == null)
        {
            Toast.makeText(context, R.string.toast_enter_report, Toast.LENGTH_SHORT).show();
        }
        else
        {
            SharedPreferences sharedPrefUser = context.getSharedPreferences("USER", MODE_PRIVATE);
            String api_token = sharedPrefUser.getString("API_TOKEN", "");
            ReportApi(api_token,orderId,reportStr);
        }
    }
    public void ReportApi(String api_token, int order_id, String details){
        loadingDialog.show();
        RetrofitWebService.getService().REPORT_RESPONSE_CALL(new ReportRequest(api_token,order_id,details))
                .enqueue(new Callback<ReportResponse>() {
                    @Override
                    public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                        ReportResponse reportResponse = response.body();
                        if(reportResponse != null) {
                            String code = reportResponse.getCode();
                            switch (code) {
                                case "200":
                                    Toast.makeText(context,reportResponse.getResponse().getMessage(), Toast.LENGTH_LONG).show();
                                    ((Activity)context).finish();
                                    loadingDialog.dismiss();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReportResponse> call, Throwable t) {

                    }
                });
    }
}
