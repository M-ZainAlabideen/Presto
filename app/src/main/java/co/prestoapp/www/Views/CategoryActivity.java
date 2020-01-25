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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.prestoapp.www.Adapters.CartSQLiteAdapter;
import co.prestoapp.www.Adapters.CategoryAdapter;
import co.prestoapp.www.Models.CategoryModel;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Models.Vendor;
import co.prestoapp.www.WebServices.Requests.VendorsRequest;
import co.prestoapp.www.WebServices.Responses.VendorsResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.activity_category_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_category_editText_enterOrder) EditText enterOrder;
    @BindView(R.id.activity_category_button_AddProduct) Button AddProduct;
    @BindView(R.id.activity_category_button_CheckOrder) Button CheckOrder;
    @BindView(R.id.activity_category_button_Others) Button Others;

    public static RecyclerView Category;
    public static TextView Title;
    public static ImageView Badge;
    public static TextView OrdersCounter;
    private ArrayList<CategoryModel> categoryModelList;
    private ArrayList<String> BannerUrlList;
    private ArrayList<String> nameList;
    private ArrayList<Integer> IdsList;
    private String ApiToken;
    private String vendorCategory;
    private LinearLayoutManager layoutManager;
    private ProgressDialog loadingDialog;
    int currentFirstVisible, visibleItemCount, totalItemCount,pageNumber,firstVisible;
    private CartSQLiteAdapter cartSQLiteAdapter;
    private String VendorName;
    private String VendorCategory;
    private String VendorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Category = (RecyclerView)findViewById(R.id.activity_category_recycler_Category);
        Title = (TextView)findViewById(R.id.activity_category_text_Title);
        Badge = (ImageView)findViewById(R.id.activity_category_image_badge);
        OrdersCounter = (TextView)findViewById(R.id.activity_category_text_OrdersCounter);

        ButterKnife.bind(this);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);

        vendorCategory = getIntent().getExtras().getString("VENDOR_CATEGORY");

        cartSQLiteAdapter = new CartSQLiteAdapter(this);
        if(cartSQLiteAdapter.get().size()>0)
        {
            Badge.setVisibility(View.VISIBLE);
            OrdersCounter.setVisibility(View.VISIBLE);
            OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
        }
        else
        {
            Badge.setVisibility(View.GONE);
            OrdersCounter.setVisibility(View.GONE);
        }


        layoutManager=new LinearLayoutManager(this);
        Category.setLayoutManager(layoutManager);
        SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
        ApiToken = sharedPrefUser.getString("API_TOKEN","");
        VendorsApi(ApiToken,vendorCategory,1);
        Category.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                firstVisible = layoutManager.findFirstVisibleItemPosition();
                return false;
            }
        });

        Category.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            VendorsApi(ApiToken,vendorCategory,pageNumber);
                        }}
                }
            }
        });
        switch (vendorCategory)
        {
            case "restaurant":
                Title.setText(getResources().getString(R.string.restaurant));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.food_drinks));
                }
                toolbar.setBackgroundResource(R.color.food_drinks);

                break;
            case "bakery":
                Title.setText(getResources().getString(R.string.bakery));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bread));
                }
                toolbar.setBackgroundResource(R.color.bread);


                break;
            case "supermarket":
                Title.setText(getResources().getString(R.string.supermarket));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.groceries));
                }
                toolbar.setBackgroundResource(R.color.groceries);


                break;


            case "stationary":
                Title.setText(getResources().getString(R.string.stationary_tools));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.stationary_tools));
                }
                toolbar.setBackgroundResource(R.color.stationary_tools);


                break;


            case "pharma":
                Title.setText(getResources().getString(R.string.pharma));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.pharma));
                }
                toolbar.setBackgroundResource(R.color.pharma);


                break;
        }


        categoryModelList = new ArrayList<CategoryModel>();
        BannerUrlList = new ArrayList<String>();
        nameList = new ArrayList<String>();
        IdsList = new ArrayList<Integer>();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(cartSQLiteAdapter.get().size()>0)
        {
            Badge.setVisibility(View.VISIBLE);
            OrdersCounter.setVisibility(View.VISIBLE);
            OrdersCounter.setText(String.valueOf(cartSQLiteAdapter.counter()));
        }

        else
        {
            Badge.setVisibility(View.GONE);
            OrdersCounter.setVisibility(View.GONE);
        }
    }


    public void CategoryClick(View view) {
        switch (view.getId())
        {
            case R.id.activity_category_image_Cart:
                finish();
                Intent intent = new Intent(this,CartActivity.class);
                intent.putExtra("POSITION",0);
                startActivity(intent);
                break;
            case R.id.activity_category_image_Back:
                finish();
                break;
        }
    }


    public void VendorsApi(final String ApiToken, final String VendorCategory,int pageNumber){
        loadingDialog.show();
        this.VendorCategory = VendorCategory;
        RetrofitWebService.getService().VENDORS_RESPONSE_CALL(new VendorsRequest(ApiToken,VendorCategory,pageNumber))
                .enqueue(new Callback<VendorsResponse>() {
                    @Override
                    public void onResponse(Call<VendorsResponse> call, Response<VendorsResponse> response) {
                        VendorsResponse vendorsResponse = response.body();
                        if(vendorsResponse != null)
                        {
                            String code = vendorsResponse.getCode();
                            ArrayList<Vendor> myResponseVendors = vendorsResponse.getResponse().getVendors();
                            switch (code) {
                                case "200":
                                    if(myResponseVendors.size()==0)
                                    {
                                        FreeOrder();
                                    }
                                    else
                                    {
                                        Others.setVisibility(View.VISIBLE);

                                        for (int i=0;i<myResponseVendors.size();i++)
                                    {
                                        Category.setVisibility(View.VISIBLE);
                                        Others.setVisibility(View.VISIBLE);

                                        IdsList.add(myResponseVendors.get(i).getId());
                                        nameList.add(myResponseVendors.get(i).getName());

                                        if(myResponseVendors.get(i).getImage()==null ||
                                                myResponseVendors.get(i).getImage().matches("")
                                        || myResponseVendors.get(i).getImage().equals(""))
                                            BannerUrlList.add("");
                                        else
                                            BannerUrlList.add(myResponseVendors.get(i).getImage());
                                    }

                                        //iteration to fill the data of restaurants (or any another category) >> BannerUrl and name
                                        for(int j=0;j<myResponseVendors.size();j++)
                                        {
                                            categoryModelList.add(new CategoryModel(BannerUrlList.get(j),nameList.get(j),IdsList.get(j)));

                                        }
                                        final RecyclerView.Adapter adapter=new CategoryAdapter(categoryModelList, CategoryActivity.this);
                                        Category.setAdapter(adapter);

                                                loadingDialog.dismiss();
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

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<VendorsResponse> call, Throwable t) {

                    }
                });
    }

    public  void FreeOrder(){

        loadingDialog.dismiss();
        enterOrder.setVisibility(View.VISIBLE);

        enterOrder.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(!s.toString().matches("") && s.toString()!=null)
                {
                    AddProduct.setVisibility(View.VISIBLE);
                    CheckOrder.setVisibility(View.VISIBLE);
                }
                else if(s.toString().matches("") || s.toString()==null)
                {
                    AddProduct.setVisibility(View.GONE);
                    CheckOrder.setVisibility(View.GONE);
                }
            }
        });

    }

    @OnClick(R.id.activity_category_button_AddProduct)
    public void addProductClick(){
        addFreeOrderCart();

    }


    @OnClick(R.id.activity_category_button_CheckOrder)
    public void checkOrderClick(){
        addFreeOrderCart();
        Intent intent =  new Intent(CategoryActivity.this,CartActivity.class);
        intent.putExtra("POSITION",0);
        startActivity(intent);
    }


    @OnClick(R.id.activity_category_button_Others)
    public void othersClick(){
                Category.setVisibility(View.INVISIBLE);
                Others.setVisibility(View.INVISIBLE);
                FreeOrder();
    }


    public void addFreeOrderCart(){
        if(VendorCategory.equals("restaurant"))
        {
            VendorName="genericRestaurant";
            VendorId="4";

        }

        else if(VendorCategory.equals("bakery"))
        {
            VendorName="genericBakery";
            VendorId="6";

        }
        else if(VendorCategory.equals("supermarket"))
        {
            VendorName="genericSuperMarket";
            VendorId="5";

        }
        else if(VendorCategory.equals("stationary"))
        {
            VendorName="genericStationary";
            VendorId="11";

        }
        else if(VendorCategory.equals("pharma"))
        {
            VendorName="genericPharma";
            VendorId="7";

        }


        cartSQLiteAdapter = new CartSQLiteAdapter(CategoryActivity.this);
        cartSQLiteAdapter.insert(0,VendorId,VendorName,
                "",enterOrder.getText().toString(),"0","",
                "1","سيتم اضافة ثمن الطلب عند التوصيل","true");

        enterOrder.setText("");
        finish();

    }

    public void GoogleSignOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
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
