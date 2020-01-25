package co.prestoapp.www.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.prestoapp.www.Adapters.StatusProductsAdapter;
import co.prestoapp.www.Models.StatusProductsModel;
import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Models.Order;
import co.prestoapp.www.WebServices.Requests.SingleOrderRequest;
import co.prestoapp.www.WebServices.Responses.SingleOrderResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

public class StatusActivity extends AppCompatActivity {

    private TextView Title;
    private TextView StatusText1;
    private TextView StatusText2;
    private TextView StatusText3;
    private TextView StatusText4;
    private ImageView Status1;
    private ImageView Status2;
    private ImageView Status3;
    private ImageView Status4;
    private View StatusLine1;
    private View StatusLine2;
    private View StatusLine3;
    private View LongLine;
    private TextView Cancelled;
    private TextView DeliveredTime;
    private TextView Address;
    private RecyclerView Products;
    private TextView Notes;
    private TextView OrdersPrice;
    private TextView DeliveryPrice;
    private TextView TotalPrice;
    private ArrayList<StatusProductsModel> statusProductsList;
    ImageView Back;

    private String StatusColor;
    String StatusStr;
    private  Order details;
    private LinearLayout DiscountLayout;
    private TextView Discount;
    private TextView Report;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //change the color of status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Title = (TextView)findViewById(R.id.activity_status_text_Title);
        StatusText1 = (TextView)findViewById(R.id.activity_status_text_Status1);
        StatusText2 = (TextView)findViewById(R.id.activity_status_text_Status2);
        StatusText3 = (TextView)findViewById(R.id.activity_status_text_Status3);
        StatusText4 = (TextView)findViewById(R.id.activity_status_text_Status4);
        Status1 = (ImageView) findViewById(R.id.activity_status_image_Status1);
        Status2 = (ImageView) findViewById(R.id.activity_status_image_Status2);
        Status3 = (ImageView) findViewById(R.id.activity_status_image_Status3);
        Status4 = (ImageView) findViewById(R.id.activity_status_image_Status4);
        StatusLine1 = (View)findViewById(R.id.activity_status_view_StatusLine1);
        StatusLine2 = (View)findViewById(R.id.activity_status_view_StatusLine2);
        StatusLine3 = (View)findViewById(R.id.activity_status_view_StatusLine3);
        LongLine = (View)findViewById(R.id.activity_status_view_longLine);

        Cancelled = (TextView)findViewById(R.id.activity_status_text_Cancelled);
        DeliveredTime = (TextView)findViewById(R.id.activity_status_text_DeliveredTime);
        Address = (TextView)findViewById(R.id.activity_status_text_Address);
        Notes = (TextView)findViewById(R.id.activity_status_text_Notes);
        OrdersPrice = (TextView)findViewById(R.id.activity_status_text_OrdersPrice);
        DeliveryPrice = (TextView)findViewById(R.id.activity_status_text_DeliveryPrice);
        TotalPrice = (TextView)findViewById(R.id.activity_status_text_TotalPrice);
        Products = (RecyclerView)findViewById(R.id.activity_status_recyclerView);

        Report = (TextView)findViewById(R.id.activity_status_button_Report);

        DiscountLayout = (LinearLayout)findViewById(R.id.activity_status_layout_Discount);
        Discount = (TextView)findViewById(R.id.activity_status_text_Discount);
        String comeFrom = getIntent().getExtras().getString("COME_FROM","");
        if(comeFrom.equals("cart") || comeFrom.equals("fireBaseNotification"))
        {
            SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
            String api_token = sharedPrefUser.getString("API_TOKEN","");
            SingleOrder(api_token,getIntent().getExtras().getString("ORDER_ID"));

        }
        else if(comeFrom.equals("my_orders"))
        {
            Title.setText("طلب: "+getIntent().getExtras().getString("DATE"));
            details= (Order) getIntent().getSerializableExtra("ORDER_DETAILS");
            if(details.getDiscount()!=0)
            {
                DiscountLayout.setVisibility(View.VISIBLE);
                Discount.setText("EGP "+String.valueOf(details.getDiscount()));
            }
            setOrderData();

        }


        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this,ReportActivity.class);
                intent.putExtra("ORDER_ID",details.getId());
                startActivity(intent);
            }
        });

        Back = (ImageView)findViewById(R.id.activity_status_image_back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void setOrderData(){
        StatusStr = details.getStatus();
        switch (StatusStr)
        {
            case "pending approval":
                StatusText1.setTextColor(Color.BLACK);
                Status1.setImageResource(R.mipmap.loading);
                break;
            case "preparing":
            case "prepared":
                StatusText1.setTextColor(Color.parseColor("#3AE168"));
                StatusText1.setText(getResources().getString(R.string.confirmed));
                Status1.setImageResource(R.mipmap.success);
                StatusText2.setTextColor(Color.BLACK);
                Status2.setImageResource(R.mipmap.loading);
                StatusLine1.setBackgroundResource(R.color.green);
                break;
            case "handed to courier":
                StatusText1.setTextColor(Color.parseColor("#3AE168"));
                StatusText1.setText(getResources().getString(R.string.confirmed));
                Status1.setImageResource(R.mipmap.success);
                StatusText2.setTextColor(Color.parseColor("#3AE168"));
                Status2.setImageResource(R.mipmap.success);
                StatusText2.setText(getResources().getString(R.string.Preparation));
                StatusText3.setTextColor(Color.BLACK);
                Status3.setImageResource(R.mipmap.loading);
                StatusLine1.setBackgroundResource(R.color.green);
                StatusLine2.setBackgroundResource(R.color.green);

                break;
            case "successful":
                StatusText1.setTextColor(Color.parseColor("#3AE168"));
                StatusText1.setText(getResources().getString(R.string.confirmed));
                Status1.setImageResource(R.mipmap.success);
                StatusText2.setTextColor(Color.parseColor("#3AE168"));
                Status2.setImageResource(R.mipmap.success);
                StatusText2.setText(getResources().getString(R.string.Preparation));
                StatusText3.setTextColor(Color.parseColor("#3AE168"));
                Status3.setImageResource(R.mipmap.success);
                StatusText4.setTextColor(Color.parseColor("#3AE168"));
                StatusText4.setText(getResources().getString(R.string.successful));
                Status4.setImageResource(R.mipmap.success);
                DeliveredTime.setBackgroundColor(Color.parseColor("#3AE168"));
                DeliveredTime.setText(getResources().getString(R.string.successful));
                StatusLine1.setBackgroundResource(R.color.green);
                StatusLine2.setBackgroundResource(R.color.green);
                StatusLine3.setBackgroundResource(R.color.green);
                break;
            case "canceled":
                Cancelled.setVisibility(View.VISIBLE);
                StatusText1.setVisibility(View.INVISIBLE);
                StatusText2.setVisibility(View.INVISIBLE);
                StatusText3.setVisibility(View.INVISIBLE);
                StatusText4.setVisibility(View.INVISIBLE);
                Status1.setVisibility(View.INVISIBLE);
                Status2.setVisibility(View.INVISIBLE);
                Status3.setVisibility(View.INVISIBLE);
                Status4.setVisibility(View.INVISIBLE);
                StatusLine1.setVisibility(View.INVISIBLE);
                StatusLine2.setVisibility(View.INVISIBLE);
                StatusLine3.setVisibility(View.INVISIBLE);
                LongLine.setVisibility(View.INVISIBLE);
                break;
        }

        if(!StatusStr.equals("successful"))
        {
            String DateTime = DateTimeFormat(details.getCreated_at());
            DeliveredTime.setText(getResources().getString(R.string.expected_time)+"  "+DateTime);
        }

        Address.setText(details.getAddress());

        String Note = details.getNotes();
        if(Note==null || Note.matches(""))
        {
            Notes.setText(getResources().getString(R.string.no_notes));
            Notes.setTextColor(Color.parseColor("#3AE168"));
        }
        else
            Notes.setText(details.getNotes());
        if(details.getTotal()!=null && details.getDelivery_fees()!=null)
        {
            float totalFloat = Float.valueOf(details.getTotal());
            float DeliveryFloat = Float.valueOf(details.getDelivery_fees());
            float OrdersPriceFloat = totalFloat - DeliveryFloat;
            OrdersPrice.setText("EGP "+String.valueOf(OrdersPriceFloat));
            DeliveryPrice.setText("EGP "+details.getDelivery_fees());
            TotalPrice.setText("EGP "+details.getSubtotal());
        }



        statusProductsList = new ArrayList<>();
        for(int j=0;j<details.getItems().size();j++) {
            statusProductsList.add(new StatusProductsModel(details.getItems().get(j).getProduct_name(),
                    details.getItems().get(j).getPrice(), details.getItems().get(j).getQty()));
        }


        Products.setLayoutManager(new LinearLayoutManager(StatusActivity.this));
        StatusProductsAdapter statusProductsAdapter = new StatusProductsAdapter(statusProductsList,StatusActivity.this);
        Products.setAdapter(statusProductsAdapter);



        Observable statusLoadingObservable = Observable.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {

                return null;
            }
        });

        statusLoadingObservable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public String DateTimeFormat(String dateTime){
        StringTokenizer tk = new StringTokenizer(dateTime);
        String date = tk.nextToken();
        String time = tk.nextToken();
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat formate2 = new SimpleDateFormat("hh:mm a");

        Date dt=null;
        try {
            dt = format1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String[] splitResult = formate2.format(dt).split(" ");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(splitResult[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, 45);
        String newTime = df.format(cal.getTime());
        if(newTime.split(":")[0].equals("13"))
            newTime="01:"+newTime.split(":")[1];

        return newTime+" "+splitResult[1];


    }

    public String DateTimeFormatTitle(String dateTime){
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


    private void SingleOrder(String api_token, String order_id){
        RetrofitWebService.getService().SINGLE_ORDER_RESPONSE_CALL(new SingleOrderRequest(api_token,order_id))
                .enqueue(new Callback<SingleOrderResponse>() {
                    @Override
                    public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {
                        SingleOrderResponse myOrderResponse = response.body();
                        if(myOrderResponse != null)
                        {
                            String code = myOrderResponse.getCode();
                            switch (code) {
                                case "200":
                                    details = myOrderResponse.getResponse().getOrder();
                                    Title.setText(DateTimeFormatTitle(details.getCreated_at()));
                                    if(details.getDiscount()!=0)
                                    {
                                        DiscountLayout.setVisibility(View.VISIBLE);
                                        Discount.setText("EGP "+String.valueOf(details.getDiscount()));
                                    }
                                    setOrderData();
                                    break;
                                case "422":
                                    Toast.makeText(StatusActivity.this,myOrderResponse.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                            }}
                    }

                    @Override
                    public void onFailure(Call<SingleOrderResponse> call, Throwable t) {

                    }
                });
    }

}
