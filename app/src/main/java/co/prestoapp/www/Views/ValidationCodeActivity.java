package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Requests.FirebasePhoneAuthRequest;
import co.prestoapp.www.WebServices.Requests.FirebasePhoneInfoRequest;
import co.prestoapp.www.WebServices.Responses.FirebasePhoneAuthResponse;
import co.prestoapp.www.WebServices.Responses.FirebasePhoneInfoResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;

import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ValidationCodeActivity extends AppCompatActivity {

    private EditText ValidationCode;
    private Button Login;
    private String ValidationCodeSt;
    private ProgressDialog loadingDialog;
    private FirebaseAuth mAuth;
    private String VerificationId;
    private String Phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_code);

        VerificationId = getIntent().getExtras().getString("VERIFICATION_ID");
        Phone = getIntent().getExtras().getString("PHONE");

        //make a keyPad below editText (editText not hidden by keyPad)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //change the color of status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);

        ValidationCode = (EditText)findViewById(R.id.activity_validation_edit_Code);
        Login = (Button)findViewById(R.id.activity_validation_button_Login);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidationCodeSt = ValidationCode.getText().toString();
                if(ValidationCodeSt.matches("")||ValidationCodeSt==null)
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_enter_code), Toast.LENGTH_SHORT).show();
                else
                {

                    loadingDialog.show();
                    SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                    FirebasePhoneAuthApi(sharedPrefUser.getString("API_TOKEN", ""),Phone,VerificationId,ValidationCodeSt);

                }
            }
        });
    }


    public void FirebasePhoneAuthApi(String api_token,String phone,String verificationId,String code){
        RetrofitWebService.getService().FIREBASE_PHONE_AUTH_RESPONSE_CALL(new FirebasePhoneAuthRequest(api_token,phone,verificationId,code))
                .enqueue(new Callback<FirebasePhoneAuthResponse>() {
                    @Override
                    public void onResponse(Call<FirebasePhoneAuthResponse> call, Response<FirebasePhoneAuthResponse> response) {
                        FirebasePhoneAuthResponse firebasePhoneAuthResponse = response.body();
                        if(firebasePhoneAuthResponse != null)
                        {
                            String code = response.body().getCode();
                        switch (code) {
                            case "200":
                                SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                                SharedPreferences.Editor editUser = sharedPrefUser.edit();
                                editUser.putString("ID_TOKEN", firebasePhoneAuthResponse.getResponse().getIdToken()).commit();
                                FirebasePhoneInfoApi(sharedPrefUser.getString("API_TOKEN", ""),Phone,firebasePhoneAuthResponse.getResponse().getIdToken());

                                break;

                                default:
                                    loadingDialog.dismiss();
                                    Toast.makeText(ValidationCodeActivity.this, getResources().getString(R.string.toast_incorrect_code), Toast.LENGTH_SHORT).show();
                                    break;
                        }}
                    }
                    @Override
                    public void onFailure(Call<FirebasePhoneAuthResponse> call, Throwable t) {

                    }
                });
    }



    public void FirebasePhoneInfoApi(String api_token,String phone,String idToken){
        RetrofitWebService.getService().FIREBASE_PHONE_INFO_RESPONSE_CALL(new FirebasePhoneInfoRequest(api_token,phone,idToken))
                .enqueue(new Callback<FirebasePhoneInfoResponse>() {
                    @Override
                    public void onResponse(Call<FirebasePhoneInfoResponse> call, Response<FirebasePhoneInfoResponse> response) {
                        FirebasePhoneInfoResponse firebasePhoneInfoResponse = response.body();
                        if(firebasePhoneInfoResponse != null)
                        {
                            String code = response.body().getCode();
                            switch (code) {
                                case "200":
                                    SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                                    SharedPreferences.Editor editUser = sharedPrefUser.edit();
                                    editUser.putString("NAME", firebasePhoneInfoResponse.getResponse().getUser().getName()).commit();
                                    editUser.putString("ACCOUNT_TYPE", firebasePhoneInfoResponse.getResponse().getUser().getAccount_type()).commit();
                                    editUser.putString("EMAIL", firebasePhoneInfoResponse.getResponse().getUser().getEmail()).commit();
                                    editUser.putString("ADDRESS", firebasePhoneInfoResponse.getResponse().getUser().getAddress()).commit();
                                    editUser.putString("PHONE",firebasePhoneInfoResponse.getResponse().getUser().getPhone()).commit();
                                    if(sharedPrefUser.getString("LOGIN_WITH","").equals("phone"))
                                        editUser.putBoolean("CHECK_PHONE_LOGIN", true).commit();
                                    editUser.putString("API_TOKEN", firebasePhoneInfoResponse.getResponse().getApi_token()).commit();
                                    finish();
                                    //if AccountExists Parameter is true , the region data will be received and the next activity will be home activity
                                    //because the customer is already have an account but if the value is false , the next activity will be createAccountActivity
                                    if (firebasePhoneInfoResponse.getResponse().getUser().getRegion_id() != null
                                            && !firebasePhoneInfoResponse.getResponse().getUser().getRegion_id().matches("")) {

                                        editUser.putString("REGION_ID", firebasePhoneInfoResponse.getResponse().getUser().getRegion_id()).commit();
                                        editUser.putString("CITY_ID", firebasePhoneInfoResponse.getResponse().getUser().getRegion().getCity_id()).commit();
                                        editUser.putString("REGION_NAME", firebasePhoneInfoResponse.getResponse().getUser().getRegion().getName()).commit();
                                        startActivity(new Intent(ValidationCodeActivity.this, HomeActivity.class));
                                    } else if (!sharedPrefUser.getString("LOGIN_WITH", "").equals("phone")) {
                                        Intent intent = new Intent(ValidationCodeActivity.this, CreateAccount2Activity.class);
                                        intent.putExtra("COME_FROM", "validation");
                                        startActivity(intent);


                                    } else {
                                        startActivity(new Intent(ValidationCodeActivity.this, CreateAccountActivity.class));
                                    }
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FirebasePhoneInfoResponse> call, Throwable t) {

                    }
                });
    }

}
