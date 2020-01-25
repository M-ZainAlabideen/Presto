package co.prestoapp.www.Views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import co.prestoapp.www.Adapters.CartSQLiteAdapter;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Models.Slider;
import co.prestoapp.www.WebServices.Requests.NPSAddRequest;
import co.prestoapp.www.WebServices.Requests.NPSCheckRequest;
import co.prestoapp.www.WebServices.Requests.SliderRequest;
import co.prestoapp.www.WebServices.Requests.UpdateRequest;
import co.prestoapp.www.WebServices.Responses.NPSAddResponse;
import co.prestoapp.www.WebServices.Responses.NPSCheckResponse;
import co.prestoapp.www.WebServices.Responses.SliderResponse;
import co.prestoapp.www.WebServices.Responses.UpdateResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private ArrayList<Slider> SliderList;
    private String ApiToken;
    private String Name;
    private SharedPreferences sharedPrefUser;
    private int counter;
    private int data_id;
    private String destination;
    public static ImageView Badge;
    public static TextView OrdersCounter;
    private CartSQLiteAdapter cartSQLiteAdapter;
    Dialog ratingDialog;

    com.daimajia.slider.library.SliderLayout sliderLayout;
    DefaultSliderView sliderView;

    String FireBaseToken;

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);

        //get the FireBase Token of User
        getFireBaseToken();
        sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
        ApiToken = sharedPrefUser.getString("API_TOKEN", "");
        NPSCheckApi(ApiToken);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_home_Toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_Drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //change navigationIcon (this line should be after syncState function)
        toolbar.setNavigationIcon(R.mipmap.menu);
        //set title of actionBar
        getSupportActionBar().setTitle("");

        //change the light status bar
        toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_home_NavigatioView);
        navigationView.setNavigationItemSelectedListener(this);

        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.addOnPageChangeListener(this);

        View header = navigationView.getHeaderView(0);
        TextView userName = header.findViewById(R.id.nav_header_home_text_Name);
        sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
        Name = sharedPrefUser.getString("NAME", "");
        userName.setText(Name);

        Badge = (ImageView) findViewById(R.id.activity_home_image_badge);
        OrdersCounter = (TextView) findViewById(R.id.activity_home_text_OrdersCounter);
        cartSQLiteAdapter = new CartSQLiteAdapter(this);
        if (cartSQLiteAdapter.get().size() > 0) {
            Badge.setVisibility(View.VISIBLE);
            OrdersCounter.setVisibility(View.VISIBLE);
            OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
        } else {
            Badge.setVisibility(View.GONE);
            OrdersCounter.setVisibility(View.GONE);
        }


        SliderList = new ArrayList<Slider>();
        sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
        ApiToken = sharedPrefUser.getString("API_TOKEN", "");
        SliderApi(ApiToken);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_Drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.myOrders) {

            startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));

        } else if (id == R.id.Logout) {
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_home_Drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cartSQLiteAdapter.get().size() > 0) {
            Badge.setVisibility(View.VISIBLE);
            OrdersCounter.setVisibility(View.VISIBLE);
            OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
        } else {
            Badge.setVisibility(View.GONE);
            OrdersCounter.setVisibility(View.GONE);
        }
    }

    public void getFireBaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FireBaseToken = instanceIdResult.getToken();
                sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                SharedPreferences.Editor userEdit = sharedPrefUser.edit();
                userEdit.putString("FIREBASE_TOKEN", FireBaseToken).commit();
                ApiToken = sharedPrefUser.getString("API_TOKEN", "");
                RetrofitWebService.getService().UPDATE_RESPONSE_CALL(new UpdateRequest(ApiToken, FireBaseToken))
                        .enqueue(new Callback<UpdateResponse>() {
                            @Override
                            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {

                            }

                            @Override
                            public void onFailure(Call<UpdateResponse> call, Throwable t) {

                            }
                        });
            }
        });
    }


    public void NPSCheckApi(String api_token) {
        RetrofitWebService.getService().NPS_CHECK_RESPONSE_CALL(new NPSCheckRequest(api_token))
                .enqueue(new Callback<NPSCheckResponse>() {
                    @Override
                    public void onResponse(Call<NPSCheckResponse> call, Response<NPSCheckResponse> response) {
                        NPSCheckResponse npsCheckResponse = response.body();
                        if (npsCheckResponse != null) {
                            String code = npsCheckResponse.getCode();
                            switch (code) {
                                case "200":
                                    if (npsCheckResponse.getResponse().isReveal()) {
                                        ratingDialog = new Dialog(HomeActivity.this);
                                        ratingDialog.setContentView(R.layout.rating);
                                        ratingDialog.setCancelable(false);
                                        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        WindowManager.LayoutParams lp = ratingDialog.getWindow().getAttributes();
                                        ratingDialog.getWindow().setAttributes(lp);
                                        ratingDialog.show();
                                        final RatingBar ratingBar = (RatingBar) ratingDialog.findViewById(R.id.ratingBar);
                                        Button ratingEvaluation = (Button) ratingDialog.findViewById(R.id.rating_Evaluation);
                                        Button ratingLater = (Button) ratingDialog.findViewById(R.id.rating_Later);
                                        ratingBar.setStepSize(1.0f);
                                        ratingLater.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ratingDialog.dismiss();
                                            }
                                        });

                                        ratingEvaluation.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                NPSAddApi(ApiToken, ratingBar.getRating());
                                            }
                                        });
                                    }
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NPSCheckResponse> call, Throwable t) {

                    }
                });
    }


    public void NPSAddApi(String api_token, float ratingValue) {
        ratingDialog.dismiss();
        loadingDialog.show();

        RetrofitWebService.getService().NPS_ADD_RESPONSE_CALL(new NPSAddRequest(api_token, ratingValue))
                .enqueue(new Callback<NPSAddResponse>() {
                    @Override
                    public void onResponse(Call<NPSAddResponse> call, Response<NPSAddResponse> response) {
                        NPSAddResponse npsAddResponse = response.body();
                        if (npsAddResponse != null) {
                            String code = npsAddResponse.getCode();
                            switch (code) {
                                case "200":
                                    loadingDialog.dismiss();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NPSAddResponse> call, Throwable t) {

                    }
                });
    }

    public void GoogleSignOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void FacebookSignOut() {
        LoginManager.getInstance().logOut();
        finish();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void PhoneSignOut() {
        SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
        sharedPrefUser.edit().putBoolean("CHECK_PHONE_LOGIN", false).commit();
        finish();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void homeClick(View view) {
        switch (view.getId()) {
            case R.id.content_home_image_Foods:
                Intent restaurantIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                restaurantIntent.putExtra("VENDOR_CATEGORY", "restaurant");
                startActivity(restaurantIntent);
                break;

            case R.id.content_home_image_Bread:
                Intent bakeryIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                bakeryIntent.putExtra("VENDOR_CATEGORY", "bakery");
                startActivity(bakeryIntent);
                break;

            case R.id.content_home_image_Groceries:
                Intent supermarketIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                supermarketIntent.putExtra("VENDOR_CATEGORY", "supermarket");
                startActivity(supermarketIntent);
                break;

            case R.id.content_home_image_Stationary_tools:
                Intent stationaryToolsIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                stationaryToolsIntent.putExtra("VENDOR_CATEGORY", "stationary");
                startActivity(stationaryToolsIntent);
                break;

            case R.id.content_home_image_Pharma:
                Intent pharmaIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                pharmaIntent.putExtra("VENDOR_CATEGORY", "pharma");
                startActivity(pharmaIntent);
                break;

            case R.id.content_home_button_MyOrders:
                startActivity(new Intent(HomeActivity.this, MyOrdersActivity.class));
                break;

            case R.id.app_bar_home_Cart:
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                intent.putExtra("POSITION", 0);
                startActivity(intent);
                break;
        }
    }

    public void SliderApi(String ApiToken) {
        RetrofitWebService.getService().SLIDER_RESPONSE_CALL(new SliderRequest(ApiToken))
                .enqueue(new Callback<SliderResponse>() {
                    @Override
                    public void onResponse(Call<SliderResponse> call, Response<SliderResponse> response) {
                        SliderResponse sliderResponse = response.body();
                        if (sliderResponse != null) {
                            String code = sliderResponse.getCode();
                            ArrayList<Slider> myResponseSlider = sliderResponse.getResponse().getSlides();
                            switch (code) {
                                case "200":
                                    for (int i = 0; i < myResponseSlider.size(); i++) {
                                        SliderList.add(new Slider(myResponseSlider.get(i).getImage()
                                                , myResponseSlider.get(i).getType(), myResponseSlider.get(i).getDestination(),
                                                myResponseSlider.get(i).getData_id(), myResponseSlider.get(i).getData_title()
                                                , myResponseSlider.get(i).getData_type()));

                                    }

                                    CreateSliderItems();
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
                    }

                    @Override
                    public void onFailure(Call<SliderResponse> call, Throwable t) {

                    }
                });
    }


    public void CreateSliderItems() {
        for (counter = 0; counter < SliderList.size(); counter++) {


            sliderView = new DefaultSliderView(this);
            sliderView.image(SliderList.get(counter).getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(sliderView);
        }
        sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override

            public void onSliderClick(BaseSliderView slider) {


                String type = SliderList.get(sliderLayout.getCurrentPosition()).getType();
                if (type.equals("inApp")) {
                    data_id = SliderList.get(sliderLayout.getCurrentPosition()).getData_id();
                    Intent intent = new Intent(HomeActivity.this, VendorDetailsActivity.class);
                    intent.putExtra("VENDOR_ID", SliderList.get(sliderLayout.getCurrentPosition()).getData_id());
                    intent.putExtra("VENDOR_NAME", SliderList.get(sliderLayout.getCurrentPosition()).getData_title());
                    intent.putExtra("VENDOR_TYPE", SliderList.get(sliderLayout.getCurrentPosition()).getData_type());
                    startActivity(intent);
                } else if (type.equals("web")) {
                    destination = SliderList.get(sliderLayout.getCurrentPosition()).getDestination();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
                    startActivity(browserIntent);

                }

            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}