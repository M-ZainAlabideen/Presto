package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.prestoapp.www.Adapters.CartSQLiteAdapter;
import co.prestoapp.www.Adapters.VendorDetailsAdapter;
import co.prestoapp.www.Models.VendorDetailsModel;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Requests.AddOrderRequest;
import co.prestoapp.www.WebServices.Requests.AddressHistoryRequest;
import co.prestoapp.www.WebServices.Requests.PromoCodeRequest;
import co.prestoapp.www.WebServices.Requests.RegionRequest;
import co.prestoapp.www.WebServices.Responses.AddOrderResponse;
import co.prestoapp.www.WebServices.Responses.AddressHistoryResponse;
import co.prestoapp.www.WebServices.Responses.PromoCodeResponse;
import co.prestoapp.www.WebServices.Responses.RegionsResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import com.facebook.login.LoginManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    @BindView(R.id.activity_cart_text_Region) TextView Region;
    @BindView(R.id.activity_cart_autocomplete_Address) AutoCompleteTextView Address;
    @BindView(R.id.activity_cart_editText_Notes) EditText Notes;
    @BindView(R.id.activity_cart_text_OrdersPrice) TextView OrdersPrice;
    @BindView(R.id.activity_cart_text_DeliveryPrice) TextView DeliveryPrice;
    @BindView(R.id.activity_cart_text_TotalPrice) TextView TotalPrice;
    @BindView(R.id.activity_cart_spinner_SelectRegion) Spinner SelectRegion;
    @BindView(R.id.activity_cart_image_Back) ImageView back;
    @BindView(R.id.activity_cart_layout_Views) LinearLayout Views;
    @BindView(R.id.activity_cart_text_emptyCart) TextView emptyCart;
    @BindView(R.id.activity_cart_layout_Discount) LinearLayout DiscountLayout;
    @BindView(R.id.activity_cart_text_Discount) TextView Discount;
    private View customView;
    private EditText Promo;
    private ArrayList<VendorDetailsModel> vendorDetailsModelList;
    private ArrayList<String> VendorNameList;
    private CartSQLiteAdapter cartSQLiteAdapter;
    private ArrayList<String> ZonesList;
    private ArrayList<String> ZonesIdList;
    private int DeliveryPriceInt=0;
    private float OrdersPriceFloat = 0;
    private ProgressDialog loadingDialog;
    public static LinearLayoutManager layoutManager;
    private int position;
    public static Button PromoCode;
    public static RecyclerView Orders;
    public static TextView RightPromoCode;
    private BottomSheetDialog promoCodeDialog;
    private ArrayList<VendorDetailsModel> items;
    private VendorDetailsModel[] vendorDetailsodelArray;
    private TextView ExpiredPromo;
    private String promoStr;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //change the color of status Bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.green));
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        RightPromoCode = (TextView)findViewById(R.id.activity_cart_text_RightPromoCode);
        Orders = (RecyclerView)findViewById(R.id.activity_cart_recycler_Orders);
        PromoCode = (Button)findViewById(R.id.activity_cart_button_PromoCode);

        ButterKnife.bind(this);

        //initialize the FirebaseAnalytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        //the dialog which show till the data is loaded.
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);

        PromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //after clicking on promoCode the BottomSheetDialog is created and inflate the custom promo code view
                promoCodeDialog = new BottomSheetDialog(CartActivity.this);
                LayoutInflater myLayout = LayoutInflater.from(CartActivity.this);
                customView = myLayout.inflate(R.layout.custom_promo_code, null);
                promoCodeDialog.setContentView(customView);

                //get the elements of custom promo code view
                Promo = customView.findViewById(R.id.custom_promo_code_editText_EnterPromo);
                ExpiredPromo = customView.findViewById(R.id.custom_promo_code_text_ExpiredPromo);

                //handling the onEditorAction(the action of keyboard)
                Promo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        //when the action of keyboard is Enter/Done >> get the promo code entered in editText and call the promoApi
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            promoStr = Promo.getText().toString();
                            if(promoStr!=null && !promoStr.matches(""))
                            {
                                SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                                String api_token = sharedPrefUser.getString("API_TOKEN", "");
                                promoApi(api_token,promoStr,vendorDetailsodelArray);
                            }
                            else
                            {
                                promoCodeDialog.dismiss();
                            }

                        }
                        return false;
                    }
                });

                //show the keyboard and show the bottom Sheet Dialog (promo code editText)
                promoCodeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
                promoCodeDialog.show();
            }
        });

        position = getIntent().getExtras().getInt("POSITION");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(CartActivity.this,HomeActivity.class));
            }
        });




        ZonesList= new ArrayList<String>();
        ZonesIdList = new ArrayList<String>();
        ZonesList.add("اختر موقعك");
        RegionsApi();
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
        SelectRegion.setAdapter(locationAdapter);
        SelectRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    Region.setText(ZonesList.get(position));
                    SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                    SharedPreferences.Editor editUser = sharedPrefUser.edit();
                    editUser.putString("REGION_ID",ZonesIdList.get(position-1)).commit();
                    editUser.putString("REGION_NAME",ZonesList.get(position)).commit();
                    SelectRegion.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layoutManager = new LinearLayoutManager(CartActivity.this);
        Orders.setLayoutManager(layoutManager);
        Orders.scrollToPosition(position);

        VendorNameList = new ArrayList<String>();
        cartSQLiteAdapter = new CartSQLiteAdapter(this);
        if(cartSQLiteAdapter.get() != null && !cartSQLiteAdapter.get().equals(""))
        {
            vendorDetailsModelList=cartSQLiteAdapter.get();
            items = vendorDetailsModelList;
            vendorDetailsodelArray = items.toArray(new VendorDetailsModel[vendorDetailsModelList.size()]);
        }
        if(vendorDetailsModelList.size()==0)
        {  Views.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
        }
        else {
            final RecyclerView.Adapter adapter = new VendorDetailsAdapter(vendorDetailsModelList, CartActivity.this);
            Orders.setAdapter(adapter);

            String OrdersPriceStr = "", QuantityStr = "";
            float QuantityFloat = 0;
            for (int i = 0; i < vendorDetailsModelList.size(); i++) {

                if (!VendorNameList.contains(vendorDetailsModelList.get(i).getVendorName())) {
                    VendorNameList.add(vendorDetailsModelList.get(i).getVendorName());
                }

                if (vendorDetailsModelList.get(i).getPrice().equals("0")) {
                    OrdersPriceStr = "0";
                } else {
                    char[] OrdersPriceChars = vendorDetailsModelList.get(i).getPrice().toCharArray();
                    OrdersPriceStr = "";
                    for (int j = 3; j < OrdersPriceChars.length; j++) {
                        OrdersPriceStr = OrdersPriceStr + OrdersPriceChars[j];
                        vendorDetailsModelList.get(i).setPrice(OrdersPriceStr);
                    }
                }

                QuantityStr = vendorDetailsModelList.get(i).getQuantity();

                if (!QuantityStr.matches("") && QuantityStr != null) {

                    QuantityFloat = Float.parseFloat(QuantityStr);

                    OrdersPriceFloat = OrdersPriceFloat + (Float.parseFloat(OrdersPriceStr) * QuantityFloat);
                }


            }

            for (int s = 0; s < VendorNameList.size(); s++) {
                if (s == 0)
                    DeliveryPriceInt = 8;
                else if (s == 1)
                    DeliveryPriceInt = 14;
                else if (s == 2)
                    DeliveryPriceInt = 16;
                else {
                    DeliveryPriceInt = DeliveryPriceInt + 8;
                }
            }


            if (vendorDetailsModelList.size() == 0)
                DeliveryPriceInt = 0;
            DeliveryPrice.setText("EGP " + String.valueOf(DeliveryPriceInt));
            OrdersPrice.setText("EGP " + String.valueOf(OrdersPriceFloat));
            TotalPrice.setText("EGP " + String.valueOf(OrdersPriceFloat + DeliveryPriceInt));


            SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
            Region.setText(sharedPrefUser.getString("REGION_NAME", ""));
            Address.setText(sharedPrefUser.getString("ADDRESS", ""));
            AddressHistoryApi(sharedPrefUser.getString("API_TOKEN", ""));

        }

        //listener for handling the text change , while the user change the text the action is performed
        Address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() != null || s.toString().matches("")) {
                    SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                    SharedPreferences.Editor editUser = sharedPrefUser.edit();
                    editUser.putString("ADDRESS", s.toString()).commit();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //listener for handling The editText actions (in this case the listener is used for handling ACTION_DONE)
        Address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    //setCursorVisible function make the vertical line of editText visible or not-visible
                    Address.setCursorVisible(false);

                }
                return false;
            }
        });


        Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address.setFocusable(true);
                showSoftKeyboard(Address);
                Address.setCursorVisible(true);

            }
        });
    }

    @OnClick(R.id.activity_cart_button_CheckOrder)
    public void CheckOrderClick(){
                SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                String api_token = sharedPrefUser.getString("API_TOKEN", "");
                int delivery_fees = DeliveryPriceInt;
                String region_id = sharedPrefUser.getString("REGION_ID", "");
                String address = sharedPrefUser.getString("ADDRESS", "");
                String notes = Notes.getText().toString();

                promoStr = RightPromoCode.getText().toString();
                if(promoStr != null && !promoStr.matches(""))
                {
                    char[] promoArray = promoStr.toCharArray();
                    promoStr = "";
                    for (int j = 2; j < promoArray.length; j++) {
                        promoStr = promoStr + promoArray[j];
                    }
                    AddOrderApi(api_token, delivery_fees, region_id, address, notes, vendorDetailsodelArray,promoStr);
                }
                else
                    AddOrderApi(api_token, delivery_fees, region_id, address, notes, vendorDetailsodelArray,null);

    }




    public void regionChangeClick(View v){
        SelectRegion.setVisibility(View.VISIBLE);
    }



    //make the address editText is focusable and show the softKeyPad
    public void addressChangeClick(View v){
        Address.setFocusable(true);
        showSoftKeyboard(Address);
        Address.setCursorVisible(true);

    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void AddOrderApi(String api_token, final int delivery_fees, String region_id, String address, String notes, VendorDetailsModel[] items, String promo_code) {
        loadingDialog.show();
        RetrofitWebService.getService().ADD_ORDER_RESPONSE_CALL(new AddOrderRequest(api_token,delivery_fees,region_id,address,notes,items,promo_code))
                .enqueue(new Callback<AddOrderResponse>() {
                    @Override
                    public void onResponse(Call<AddOrderResponse> call, Response<AddOrderResponse> response) {
                        AddOrderResponse addOrderResponse = response.body();

                        if(addOrderResponse != null) {
                            String code = addOrderResponse.getCode();
                            switch (code) {
                                case "422":
                                    Toast.makeText(CartActivity.this,addOrderResponse.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();

                                    break;
                                case "200":
                                    cartSQLiteAdapter.deleteAll();
                                    Toast.makeText(CartActivity.this,getResources().getString(R.string.order_checked), Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(CartActivity.this,StatusActivity.class);
                                    intent.putExtra("ORDER_ID",addOrderResponse.getResponse().getOrder().getId());
                                    intent.putExtra("COME_FROM","cart");
                                    startActivity(intent);
                                    loadingDialog.dismiss();
                                    //using FirebaseAnalytics for sending the data of each order(total price of order)
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Param.CURRENCY,"EGP");
                                    bundle.putDouble(FirebaseAnalytics.Param.VALUE,OrdersPriceFloat + DeliveryPriceInt);
                                    mFirebaseAnalytics.logEvent("ecommerce_purchase",bundle);
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
                                    default:
                                        Toast.makeText(CartActivity.this,getResources().getString(R.string.order_failed), Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                        break;
                            }}
                    }

                    @Override
                    public void onFailure(Call<AddOrderResponse> call, Throwable t) {

                    }
                });
    }

    public void promoApi(String api_token, final String promo_code, VendorDetailsModel[] items){
        loadingDialog.show();
        RetrofitWebService.getService().PROMO_CODE_RESPONSE_CALL(new PromoCodeRequest(api_token,promo_code,items))
                .enqueue(new Callback<PromoCodeResponse>() {
                    @Override
                    public void onResponse(Call<PromoCodeResponse> call, Response<PromoCodeResponse> response) {
                        PromoCodeResponse promoCodeResponse = response.body();
                        if(promoCodeResponse != null)
                        {
                            String code = promoCodeResponse.getCode();
                            switch (code){
                                case "200":
                                    loadingDialog.dismiss();
                                    promoCodeDialog.dismiss();
                                    ExpiredPromo.setVisibility(View.GONE);
                                    DiscountLayout.setVisibility(View.VISIBLE);
                                    Discount.setText("EGP "+String.valueOf(promoCodeResponse.getResponse().getDiscount()));
                                    TotalPrice.setText("EGP "+String.valueOf(promoCodeResponse.getResponse().getNew_total()));
                                    RightPromoCode.setVisibility(View.VISIBLE);
                                    PromoCode.setVisibility(View.GONE);
                                    RightPromoCode.setText("✓ "+promo_code);
                                    break;
                                case "422":
                                    ExpiredPromo.setVisibility(View.VISIBLE);
                                    loadingDialog.dismiss();
                                    break;
                                case "401":
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PromoCodeResponse> call, Throwable t) {

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
                                    Toast.makeText(CartActivity.this, regionsResponse.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                                case "500":
                                    Toast.makeText(CartActivity.this,getResources().getString(R.string.toast_500), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }}
                    @Override
                    public void onFailure(Call<RegionsResponse> call, Throwable t) {

                    }
                });

        return ZonesList;
    }



    public void AddressHistoryApi(String api_token){
        RetrofitWebService.getService().ADDRESS_HISTORY_RESPONSE_CALL(new AddressHistoryRequest(api_token))
                .enqueue(new Callback<AddressHistoryResponse>() {
                    @Override
                    public void onResponse(Call<AddressHistoryResponse> call, Response<AddressHistoryResponse> response) {
                        AddressHistoryResponse addressHistoryResponse = response.body();

                        if(addressHistoryResponse != null) {
                            String code = addressHistoryResponse.getCode();
                            switch (code) {
                                case "200":
                                    if(addressHistoryResponse.getResponse().getAddresses().size()!=0)
                                    {
                                        ArrayList<String> addressesHistoryList = addressHistoryResponse.getResponse().getAddresses();
                                        ArrayAdapter<String> addressesHistoryAdapter = new ArrayAdapter<String>
                                                (CartActivity.this,R.layout.address_history_item,R.id.address_history_TV,addressesHistoryList);
                                        Address.setThreshold(1);
                                        Address.setAdapter(addressesHistoryAdapter);
                                    }

                                    break;

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressHistoryResponse> call, Throwable t) {

                    }
                });
    }

    public void GoogleSignOut() {
            FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
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
