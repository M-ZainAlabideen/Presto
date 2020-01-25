package co.prestoapp.www.WebServices;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import co.prestoapp.www.TLSSocketFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



public class RetrofitWebService {
    private static final String TAG = RetrofitWebService.class.getSimpleName();
    private static final Map<String, RetrofitService> mServices = new HashMap<>();
    //http://prestoapp.co/beta/api/
    //https://prestoapp.co/api/
    //http://142.93.106.34/api
    private static final String url = "https://prestoapp.co/api/";
    OkHttpClient client;

    private RetrofitWebService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        try {
             client = new OkHttpClient.Builder()
                    .writeTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .addInterceptor(interceptor).
                            sslSocketFactory(new TLSSocketFactory()).
                            build();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Adapter factory for supporting service method return types, add instance of RxJava2CallAdapterFactory for Rxjava 2 support.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mServices.put(url, retrofit.create(RetrofitService.class));
    }

    public static RetrofitService getService() {
        if (null == mServices.get(url)) {
            new RetrofitWebService();
        }
        return mServices.get(url);
    }



}
