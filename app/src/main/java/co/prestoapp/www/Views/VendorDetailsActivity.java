package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.prestoapp.www.Adapters.CartSQLiteAdapter;
import co.prestoapp.www.Adapters.VendorDetailsAdapter;
import co.prestoapp.www.Models.VendorDetailsModel;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Models.Product;
import co.prestoapp.www.WebServices.Requests.VendorDetailsRequest;
import co.prestoapp.www.WebServices.Responses.VendorDetailsResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorDetailsActivity extends AppCompatActivity {

    private LinearLayout VendorDetailsLayout;
    public static TextView Title;
    public static Button CheckOrder;
    public static Button AddProduct;
    public static ImageView Badge;
    public static TextView OrdersCounter;
    private int vendorId;
    private String ApiToken;
    private ArrayList<VendorDetailsModel> vendorModelList;
    private RecyclerView VendorDetails;
    private ArrayList<Integer> IdsList;
    private ArrayList<String> VendorIdsList;
    private ArrayList<String> nameList;
    private ArrayList<String> priceList;
    private ArrayList<String> sizeList;
    private ArrayList<String> descriptionList;
    private ArrayList<String> typesList;
    private CartSQLiteAdapter cartSQLiteAdapter;
    private ProgressDialog Loading;
    private int progress;
    private Toolbar toolbar;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        Loading = new ProgressDialog(this);
        Loading.setMessage(getResources().getString(R.string.please_wait));
        Loading.setIndeterminate(true);
        Loading.setCancelable(true);
        Loading.show();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        vendorId = getIntent().getExtras().getInt("VENDOR_ID");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        VendorDetailsLayout = (LinearLayout) findViewById(R.id.activity_vendor_details_layout);
        Title = (TextView) findViewById(R.id.activity_vendor_details_text_Title);
        Title.setText(getIntent().getExtras().getString("VENDOR_NAME"));

        String rest = getResources().getString(R.string.restaurant);
        String market = getResources().getString(R.string.supermarket);
        String bake = getResources().getString(R.string.bakery);

        switch (getIntent().getExtras().getString("VENDOR_TYPE")) {
            case "أكل ومشروبات":
            case "restaurant":
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.food_drinks));
                }
                toolbar.setBackgroundResource(R.color.food_drinks);
                break;
            case "بقالة":
            case "supermarket":
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.groceries));
                }
                toolbar.setBackgroundResource(R.color.groceries);
                break;
            case "مخبوزات":
            case "bakery":
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bread));
                }
                toolbar.setBackgroundResource(R.color.bread);
                break;

            case "صيدلية":
            case "pharma":
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.pharma));
                }
                toolbar.setBackgroundResource(R.color.pharma);
                break;

            case "مكتبة":
            case "stationary":
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.stationary_tools));
                }
                toolbar.setBackgroundResource(R.color.stationary_tools);
                break;
        }

        CheckOrder = (Button) findViewById(R.id.activity_vendor_details_button_CheckOrder);
        AddProduct = (Button) findViewById(R.id.activity_vendor_details_button_AddProduct);
        CheckOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(VendorDetailsActivity.this, CartActivity.class);
                intent.putExtra("POSITION", 0);
                startActivity(intent);
            }
        });
        AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(VendorDetailsActivity.this, HomeActivity.class));
            }
        });
        Badge = (ImageView) findViewById(R.id.activity_vendor_details_image_badge);
        OrdersCounter = (TextView) findViewById(R.id.activity_vendor_details_text_OrdersCounter);
        cartSQLiteAdapter = new CartSQLiteAdapter(this);
        if (cartSQLiteAdapter.get().size() > 0) {
            Badge.setVisibility(View.VISIBLE);
            OrdersCounter.setVisibility(View.VISIBLE);
            AddProduct.setVisibility(View.VISIBLE);
            CheckOrder.setVisibility(View.VISIBLE);
            OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
        }


        SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
        ApiToken = sharedPrefUser.getString("API_TOKEN", "");


        VendorDetailsApi(ApiToken, vendorId);
    }

    public void CategoryDetailsClick(View view) {
        switch (view.getId()) {
            case R.id.activity_vendor_details_image_Cart:
                finish();
                Intent intent = new Intent(VendorDetailsActivity.this, CartActivity.class);
                intent.putExtra("POSITION", 0);
                startActivity(intent);
                break;
            case R.id.activity_vendor_details_image_Back:
                finish();
                break;
        }
    }


    public void VendorDetailsApi(String ApiToken, int VendorId) {
        RetrofitWebService.getService().VENDOR_DETAILS_RESPONSE_CALL(new VendorDetailsRequest(ApiToken, VendorId))
                .enqueue(new Callback<VendorDetailsResponse>() {
                    @Override
                    public void onResponse(Call<VendorDetailsResponse> call, Response<VendorDetailsResponse> response) {
                        String code = response.body().getCode();
                        switch (code) {
                            case "200":
                                Typeface typeface = ResourcesCompat.getFont(VendorDetailsActivity.this, R.font.seguisb);
                                ArrayList<Product> products = response.body().getResponse().getVendor().getProducts();
                                for (int i = 0; i < products.size(); i++) {
                                    TextView Type = new TextView(VendorDetailsActivity.this);
                                    Type.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
                                    Type.setTypeface(typeface);
                                    Type.setTextSize(22);
                                    Type.setTextColor(getResources().getColor(R.color.vendor_details_text));
                                    if (products.get(i).getType() == null || products.get(i).getType().matches("")) {
                                        Type.setText("أخري");

                                    } else {
                                        Type.setText(products.get(i).getType());
                                    }
                                    Type.setGravity(Gravity.CENTER);
                                    VendorDetailsLayout.addView(Type);

                                    View line = new View(VendorDetailsActivity.this);
                                    line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                                    ViewGroup.MarginLayoutParams AddMargin = (ViewGroup.MarginLayoutParams)line.getLayoutParams();
                                    AddMargin.setMargins(24,0,24,16);
                                    line.setBackgroundResource(R.color.vendor_details_text);
                                    line.requestLayout();
                                    VendorDetailsLayout.addView(line);


                                    VendorDetails = new RecyclerView(VendorDetailsActivity.this);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VendorDetailsActivity.this);
                                    VendorDetails.setLayoutManager(linearLayoutManager);
                                    vendorModelList = new ArrayList<VendorDetailsModel>();
                                    IdsList = new ArrayList<Integer>();
                                    VendorIdsList = new ArrayList<String>();
                                    nameList = new ArrayList<String>();
                                    priceList = new ArrayList<String>();
                                    sizeList = new ArrayList<String>();
                                    descriptionList = new ArrayList<String>();
                                    typesList = new ArrayList<String>();

                                    for (int j = 0; j < products.get(i).getVendorDetails().size(); j++) {

                                        IdsList.add(products.get(i).getVendorDetails().get(j).getId());
                                        VendorIdsList.add(products.get(i).getVendorDetails().get(j).getVendor_id());
                                        nameList.add(products.get(i).getVendorDetails().get(j).getName());
                                        priceList.add("EGP" + products.get(i).getVendorDetails().get(j).getPrice());
                                        sizeList.add(products.get(i).getVendorDetails().get(j).getSize());
                                        descriptionList.add(products.get(i).getVendorDetails().get(j).getDescription());
                                        if (products.get(i).getType() == null || products.get(i).getType().matches(""))
                                            typesList.add("أخري");
                                        else
                                            typesList.add(products.get(i).getType());
                                    }
                                    for (int k = 0; k < nameList.size(); k++) {
                                        vendorModelList.add(new VendorDetailsModel(IdsList.get(k), VendorIdsList.get(k), Title.getText().toString(), nameList.get(k), priceList.get(k), sizeList.get(k), descriptionList.get(k), typesList.get(k), null));
                                    }
                                    adapter = new VendorDetailsAdapter(vendorModelList, VendorDetailsActivity.this);

                                    VendorDetails.setAdapter(adapter);
                                    if (i == products.size() - 1)
                                        VendorDetails.setPadding(0, 0, 0, 160);
                                    VendorDetailsLayout.addView(VendorDetails);


                                    CountDownTimer mCountDownTimer=new CountDownTimer(3000,1000) {

                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            Loading.show();
                                        }

                                        @Override
                                        public void onFinish() {
                                            //Do what you want
                                           Loading.dismiss();
                                        }
                                    };
                                    mCountDownTimer.start();

                                }


                                break;
                            case "401":
                                SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
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
                                sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                                SharedPreferences.Editor editUser = sharedPrefUser.edit();
                                editUser.putString("API_TOKEN", "").commit();
                                editUser.putString("NAME", "").commit();
                                editUser.putString("PHONE", "").commit();
                                editUser.putBoolean("ACCOUNT_EXISTS", false).commit();
                                editUser.putString("SOCIAL_TOKEN", "").commit();
                                editUser.putString("SOCIAL_PLATFORM", "").commit();
                                editUser.putString("REGION_ID", "").commit();
                                editUser.putString("ACCOUNT_TYPE", "").commit();
                                editUser.putString("ADDRESS", "").commit();
                                editUser.putString("REGION_NAME", "").commit();
                                editUser.putString("REFERRAL_CODE", "").commit();
                                editUser.putString("ID", "").commit();
                                editUser.putBoolean("PHONE_VERIFIED", false).commit();
                                editUser.putString("EMAIL", "").commit();
                                editUser.putString("REFERRER_ID", "").commit();
                                editUser.putFloat("BALANCE", 0).commit();
                                editUser.putString("FIREBASE_TOKEN", "").commit();
                                editUser.putBoolean("CHECK_PHONE_LOGIN", false).commit();

                                break;
                        }

                    }

                    @Override
                    public void onFailure(Call<VendorDetailsResponse> call, Throwable t) {

                    }
                });

    }


    public void GoogleSignOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(VendorDetailsActivity.this, MainActivity.class);
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
