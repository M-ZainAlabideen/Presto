package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.prestoapp.www.Presenters.ReportPresenter;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Models.Report;
import co.prestoapp.www.WebServices.Requests.ReportRequest;
import co.prestoapp.www.WebServices.Responses.ReportResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    @BindView(R.id.activity_report_editText_enterReport) EditText EnterReport;
    ReportPresenter reportPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //change the color of status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ButterKnife.bind(this);
        reportPresenter = new ReportPresenter(this,EnterReport);

    }

    @OnClick(R.id.activity_report_image_back)
    public void backClick(){
        finish();
    }

    @OnClick(R.id.activity_report_button_sendReport)
    public void sendReportClick(){
        int orderId = Integer.valueOf(getIntent().getExtras().getString("ORDER_ID"));
        reportPresenter.sendReport(orderId);

    }

}
