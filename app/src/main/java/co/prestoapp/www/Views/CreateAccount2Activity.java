package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Requests.RegionRequest;
import co.prestoapp.www.WebServices.Requests.UpdateRequest;
import co.prestoapp.www.WebServices.Responses.RegionsResponse;
import co.prestoapp.www.WebServices.Responses.UpdateResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccount2Activity extends AppCompatActivity {

    private Spinner Zones;
    private EditText Address;
    private ArrayList ZonesList;
    private ArrayList ZonesIdList;
    private int Position;
    private Button Login;
    private  String NameStr;
    private String MailStr;
    private String  AddressStr;
    private String ApiTokenStr;
    private ProgressDialog loadingDialog;

    private static final String GOOGLE_PLATFORM="google";
    private static final String FACEBOOK_PLATFORM="facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account2);

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

        String comeFrom = getIntent().getExtras().getString("COME_FROM");
        if(comeFrom!=null && comeFrom.equals("create_account"))
        {
            //receive passed data from CreateAccountActivity
            NameStr = getIntent().getExtras().getString("NAME_KEY");
            MailStr = getIntent().getExtras().getString("MAIL_KEY");
        }

        else
        {
            SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
            NameStr = sharedPrefUser.getString("NAME","");
            MailStr = sharedPrefUser.getString("EMAIL","");

        }
        Zones = (Spinner)findViewById(R.id.activity_create2_spinner_Zones);
        Address = (EditText)findViewById(R.id.activity_create2_edit_Address);
        Login = (Button)findViewById(R.id.activity_create2_button_Login);

        ZonesList= new ArrayList<String>();
        ZonesIdList=new ArrayList<Integer>();
        //set the first item of spinner which not coming from the RegionApi response
        ZonesList.add("اختر موقعك");
        RegionsApi();


        /**
        create ArrayAdapter with Override of two functions

        getView >> change the properties of the selected item of spinner
        getDropDownView >> change the properties of the items of spinner when dropped down
         **/
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ZonesList){

            //change the font of text
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.seguisb);
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView item = ((TextView)v);
                item .setTextSize(17);
                item.setGravity(Gravity.CENTER);
                item.setTypeface(typeface);
                item.setTextColor(getResources().getColor(R.color.grayDark));
                return v;
            }

            @Override
            public View getDropDownView(int position,View convertView,ViewGroup parent) {

                View v =  super.getDropDownView(position, convertView, parent);
                TextView item = ((TextView)v);
                item .setTextSize(17);
                item.setGravity(Gravity.CENTER);
                item.setTypeface(typeface);
                item.setPadding(25,25,25,25);
                item.setTextColor(getResources().getColor(R.color.grayDark));
                return v;
            }
        };
        Zones.setAdapter(locationAdapter);



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressStr = Address.getText().toString();
                if(Zones.getSelectedItemPosition()==0)
                   Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_select_zone), Toast.LENGTH_SHORT).show();
               else if(AddressStr.matches("")||AddressStr==null)
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_enter_address), Toast.LENGTH_SHORT).show();
               else
                {
                    Position = Zones.getSelectedItemPosition();
                    UserUpdateApi(NameStr,MailStr,AddressStr,Integer.valueOf(ZonesIdList.get(Position-1).toString()));
                    SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                    SharedPreferences.Editor editUser = sharedPrefUser.edit();
                    editUser.putString("REGION_NAME",ZonesList.get(Position).toString()).commit();

                }

            }
        });
    }

    /*
     * this function is send RegionApi Request without required parameters
     * then receive the response , get response body and get the array of of zones(regions) and set it in the Spinner of Zones*/
    public ArrayList<String> RegionsApi(){

        RetrofitWebService.getService().REGIONS_RESPONSE_CALL(new RegionRequest())
                .enqueue(new Callback<RegionsResponse>() {
                    @Override
                    public void onResponse(Call<RegionsResponse> call, Response<RegionsResponse> response) {
                        RegionsResponse regionsResponse = response.body();

                        if(regionsResponse != null) {
                            String code = regionsResponse.getCode();
                            switch (code) {
                                case "200":
                                    for (int i=0;i<regionsResponse.getResponse().getRegions().size();i++)
                                    {
                                        ZonesList.add(regionsResponse.getResponse().getRegions().get(i).getName());
                                        ZonesIdList.add(regionsResponse.getResponse().getRegions().get(i).getId());
                                    }
                                    break;
                                case "422":
                                    Toast.makeText(CreateAccount2Activity.this, regionsResponse.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                                case "500":
                                    Toast.makeText(CreateAccount2Activity.this,getResources().getString(R.string.toast_500), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }}
                    @Override
                    public void onFailure(Call<RegionsResponse> call, Throwable t) {

                    }
                });

        return ZonesList;
    }


    /*
     * this function is send UserUpdateApi Request with phoneNumber , Mail , Address and RegionId parameters
     * then receive the response , get response body and get all data response
     * don't forget to check the code and handling the different cases*/
    public void UserUpdateApi(String NameStr,String MailStr,String AddressStr,Integer ZoneId){
        SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
        ApiTokenStr = sharedPrefUser.getString("API_TOKEN","");
        loadingDialog.show();
        RetrofitWebService.getService().UPDATE_RESPONSE_CALL(new UpdateRequest(ApiTokenStr,NameStr,null,MailStr,
                null,null, null,null,ZoneId,AddressStr,null,null))
                .enqueue(new Callback<UpdateResponse>() {
                    @Override
                    public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                        UpdateResponse updateResponse = response.body();
                        if(updateResponse != null) {
                            String code = updateResponse.getCode();
                            co.prestoapp.www.WebServices.Models.Response myResponse=updateResponse.getResponse();
                            switch (code) {
                                case "200":
                                    SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                                    SharedPreferences.Editor editUser = sharedPrefUser.edit();
                                    editUser.putString("NAME",myResponse.getUser().getName()).commit();
                                    editUser.putString("PHONE",myResponse.getUser().getPhone()).commit();
                                    editUser.putString("ACCOUNT_TYPE",myResponse.getUser().getAccount_type()).commit();
                                    editUser.putString("ADDRESS",myResponse.getUser().getAddress()).commit();
                                    editUser.putString("SOCIAL_PLATFORM",myResponse.getUser().getSocial_platform()).commit();
                                    editUser.putString("REGION_ID",myResponse.getUser().getRegion_id()).commit();
                                    int myRegionId = Integer.valueOf(sharedPrefUser.getString("REGION_ID","0"));
                                    String myRegionName = ZonesList.get(myRegionId-1).toString();
                                    editUser.putString("REGION_NAME",myRegionName).commit();
                                    editUser.putBoolean("CHECK_PHONE_LOGIN",true).commit();
                                    finish();
                                    startActivity(new Intent(CreateAccount2Activity.this,HomeActivity.class));
                                    break;

                                case "422":
                                    loadingDialog.dismiss();
                                    Toast.makeText(CreateAccount2Activity.this, updateResponse.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                                case "500":
                                    loadingDialog.dismiss();
                                    Toast.makeText(CreateAccount2Activity.this,getResources().getString(R.string.toast_500), Toast.LENGTH_SHORT).show();
                                    break;
                                case "401":
                                    sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                                    String loginWithStr = sharedPrefUser.getString("LOGIN_WITH", "");
                                    switch (loginWithStr) {
                                        case "phone":
                                            PhoneSignOut();
                                            break;
                                        case "google":
                                            GoogleSignOut();
                                            break;
                                        case "facebook":
                                            FacebookSignOut();
                                            break;
                                    }
                                    sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                                    editUser = sharedPrefUser.edit();
                                    editUser.putString("API_TOKEN","").commit();
                                    editUser.putString("NAME","").commit();
                                    editUser.putString("PHONE","").commit();
                                    editUser.putBoolean("ACCOUNT_EXISTS",false).commit();
                                    editUser.putString("SOCIAL_TOKEN","").commit();
                                    editUser.putString("SOCIAL_PLATFORM","").commit();
                                    editUser.putString("REGION_ID","").commit();
                                    editUser.putString("ACCOUNT_TYPE","").commit();
                                    editUser.putString("ADDRESS","").commit();
                                    editUser.putString("REGION_NAME","").commit();
                                    editUser.putString("REFERRAL_CODE","").commit();
                                    editUser.putString("ID","").commit();
                                    editUser.putBoolean("PHONE_VERIFIED",false).commit();
                                    editUser.putString("EMAIL","").commit();
                                    editUser.putString("REFERRER_ID","").commit();
                                    editUser.putFloat("BALANCE",0).commit();
                                    editUser.putString("FIREBASE_TOKEN","").commit();
                                    editUser.putBoolean("CHECK_PHONE_LOGIN",false).commit();

                                    break;
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<UpdateResponse> call, Throwable t) {

                    }
                });
    }

    public void GoogleSignOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(CreateAccount2Activity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void FacebookSignOut() {
        LoginManager.getInstance().logOut();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void PhoneSignOut() {
        SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
        sharedPrefUser.edit().putBoolean("CHECK_PHONE_LOGIN",false).commit();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
