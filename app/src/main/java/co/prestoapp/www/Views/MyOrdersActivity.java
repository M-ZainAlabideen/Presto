package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.prestoapp.www.Adapters.MyOrdersAdapter;
import co.prestoapp.www.Models.MyOrdersModel;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Models.Order;
import co.prestoapp.www.WebServices.Requests.MyOrderRequest;
import co.prestoapp.www.WebServices.Responses.MyOrderResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView MyOrders;
    private ArrayList<MyOrdersModel> myOrdersModelList;
    private ArrayList<Order> OrdersModelList;
    private ArrayList<String> DateList;
    private ArrayList<String> StatusList;
    private ArrayList<String> StatusColorList;
    private ArrayList<String> PriceList;
    private ArrayList<String> AddressList;
    private ProgressDialog loadingDialog;
    private ImageView Back;
    private TextView NoOrders;
    int currentFirstVisible, visibleItemCount, totalItemCount,pageNumber;
    private LinearLayoutManager layoutManager;
    int firstVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //change the color of status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        MyOrders = (RecyclerView)findViewById(R.id.activity_my_orders_recyclerView);
        layoutManager=new LinearLayoutManager(this);
        MyOrders.setLayoutManager(layoutManager);
        myOrdersModelList = new ArrayList<MyOrdersModel>();
        DateList = new ArrayList<String>();
        StatusList = new ArrayList<String>();
        StatusColorList = new ArrayList<String>();
        PriceList = new ArrayList<String>();
        AddressList = new ArrayList<String>();

        Back = (ImageView)findViewById(R.id.activity_orders_image_Back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
        String api_token = sharedPrefUser.getString("API_TOKEN","");
        MyOrdersApi(api_token,1);

        MyOrders.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                firstVisible = layoutManager.findFirstVisibleItemPosition();
                return false;
            }
        });

        MyOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0)
                {
                    currentFirstVisible=layoutManager.findFirstVisibleItemPosition();
                    if(currentFirstVisible < firstVisible)
                    {
                        pageNumber++;
                        visibleItemCount=layoutManager.getChildCount();
                        totalItemCount=layoutManager.getItemCount();

                        if(totalItemCount==(visibleItemCount+currentFirstVisible))
                        {
                            SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                            String api_token = sharedPrefUser.getString("API_TOKEN","");
                            MyOrdersApi(api_token,pageNumber);
                        }}
                }
            }
        });

    }


    public String DateTimeFormat(String dateTime){
        Locale locale = new Locale("ar");
        StringTokenizer tk = new StringTokenizer(dateTime);
       String date = tk.nextToken();
       String time = tk.nextToken();
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm:ss",locale);
        SimpleDateFormat formate2 = new SimpleDateFormat("hh:mm a",locale);

        Date dt=null;
        try {
           dt = format1.parse(time);
       } catch (ParseException e) {
           e.printStackTrace();
       }

        //return formate2.format(dt);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date3 = null;


        try {
            date3 = sdf.parse(dateTime);
        } catch (Exception e) {

        }
        sdf = new SimpleDateFormat("EEEE,dd MMMM yyyy",locale);
        String format = sdf.format(date3);
        return format + " - "+formate2.format(dt);

    }

    public String getStatus(String status){
        switch (status)
        {
            case "pending approval":
                StatusColorList.add("#506CFF");
                return getResources().getString(R.string.pending_approval);
            case "preparing":
            case "prepared":
                StatusColorList.add("#506CFF");
                return getResources().getString(R.string.prepared);
            case "handed to courier":
                StatusColorList.add("#506CFF");
                return getResources().getString(R.string.handed_to_courier);
            case "successful":
                StatusColorList.add("#3AE168");
                return getResources().getString(R.string.successful);
            case "canceled":
                StatusColorList.add("#E86C5B");
                return getResources().getString(R.string.canceled);
        }

            return "";
    }

    public void MyOrdersApi(String ApiToken,int pageNumber){
        loadingDialog.show();
        RetrofitWebService.getService().MY_ORDER_RESPONSE_CALL(new MyOrderRequest(ApiToken,pageNumber))
                .enqueue(new Callback<MyOrderResponse>() {
                    @Override
                    public void onResponse(Call<MyOrderResponse> call, Response<MyOrderResponse> response) {
                        MyOrderResponse myOrderResponse = response.body();
                        if(myOrderResponse != null)
                        {
                            String code = myOrderResponse.getCode();
                            switch (code) {
                                case "200":
                                    SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                                    String address = sharedPrefUser.getString("ADDRESS","");
                                    OrdersModelList = new ArrayList<>();
                                    if(myOrderResponse.getResponse().getOrders().size()==0)
                                    {
                                        NoOrders = (TextView)findViewById(R.id.activity_my_orders_NoOrders);
                                        NoOrders.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        for (int i = 0; i < myOrderResponse.getResponse().getOrders().size(); i++) {
                                            String DateTime = DateTimeFormat(myOrderResponse.getResponse().getOrders().get(i).getCreated_at());
                                            DateList.add(DateTime);

                                            String Status = getStatus(myOrderResponse.getResponse().getOrders().get(i).getStatus());
                                            StatusList.add(Status);

                                            AddressList.add(myOrderResponse.getResponse().getOrders().get(i).getAddress());

                                            OrdersModelList.add(myOrderResponse.getResponse().getOrders().get(i));

                                        }
                                        for (int j = 0; j < DateList.size(); j++) {
                                            myOrdersModelList.add(new MyOrdersModel(DateList.get(j), StatusList.get(j), StatusColorList.get(j),
                                                    myOrderResponse.getResponse().getOrders().get(j).getTotal() + " EGP",AddressList.get(j)));
                                        }


                                        MyOrdersAdapter myOrdersAdapter = new MyOrdersAdapter(myOrdersModelList, OrdersModelList, StatusColorList, MyOrdersActivity.this);
                                        MyOrders.setAdapter(myOrdersAdapter);
                                    }
                                    loadingDialog.dismiss();

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
                                    SharedPreferences.Editor editUser = sharedPrefUser.edit();
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
                            }}
                    }

                    @Override
                    public void onFailure(Call<MyOrderResponse> call, Throwable t) {

                    }
                });
    }

    public void GoogleSignOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(MyOrdersActivity.this, MainActivity.class);
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
