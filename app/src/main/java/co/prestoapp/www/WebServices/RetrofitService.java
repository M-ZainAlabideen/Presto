package co.prestoapp.www.WebServices;

import java.util.List;

import co.prestoapp.www.WebServices.Models.Region;
import co.prestoapp.www.WebServices.Requests.AddOrderRequest;
import co.prestoapp.www.WebServices.Requests.AddressHistoryRequest;
import co.prestoapp.www.WebServices.Requests.CheckPhoneRequest;
import co.prestoapp.www.WebServices.Requests.FirebasePhoneAuthRequest;
import co.prestoapp.www.WebServices.Requests.FirebasePhoneInfoRequest;
import co.prestoapp.www.WebServices.Requests.LoginRequest;
import co.prestoapp.www.WebServices.Requests.MyOrderRequest;
import co.prestoapp.www.WebServices.Requests.NPSAddRequest;
import co.prestoapp.www.WebServices.Requests.NPSCheckRequest;
import co.prestoapp.www.WebServices.Requests.PromoCodeRequest;
import co.prestoapp.www.WebServices.Requests.RegionRequest;
import co.prestoapp.www.WebServices.Requests.ReportRequest;
import co.prestoapp.www.WebServices.Requests.SingleOrderRequest;
import co.prestoapp.www.WebServices.Requests.SliderRequest;
import co.prestoapp.www.WebServices.Requests.SocialLoginRequest;
import co.prestoapp.www.WebServices.Requests.SocialPhoneRequest;
import co.prestoapp.www.WebServices.Requests.SocialValidationRequest;
import co.prestoapp.www.WebServices.Requests.UpdateRequest;
import co.prestoapp.www.WebServices.Requests.VendorDetailsRequest;
import co.prestoapp.www.WebServices.Requests.VendorsRequest;
import co.prestoapp.www.WebServices.Responses.AddOrderResponse;
import co.prestoapp.www.WebServices.Responses.AddressHistoryResponse;
import co.prestoapp.www.WebServices.Responses.CheckPhoneResponse;
import co.prestoapp.www.WebServices.Responses.FirebasePhoneAuthResponse;
import co.prestoapp.www.WebServices.Responses.FirebasePhoneInfoResponse;
import co.prestoapp.www.WebServices.Responses.LoginResponse;
import co.prestoapp.www.WebServices.Responses.MyOrderResponse;
import co.prestoapp.www.WebServices.Responses.NPSAddResponse;
import co.prestoapp.www.WebServices.Responses.NPSCheckResponse;
import co.prestoapp.www.WebServices.Responses.PromoCodeResponse;
import co.prestoapp.www.WebServices.Responses.RegionsResponse;
import co.prestoapp.www.WebServices.Responses.ReportResponse;
import co.prestoapp.www.WebServices.Responses.SingleOrderResponse;
import co.prestoapp.www.WebServices.Responses.SliderResponse;
import co.prestoapp.www.WebServices.Responses.SocialLoginResponse;
import co.prestoapp.www.WebServices.Responses.SocialPhoneResponse;
import co.prestoapp.www.WebServices.Responses.SocialValidationResponse;
import co.prestoapp.www.WebServices.Responses.UpdateResponse;
import co.prestoapp.www.WebServices.Responses.VendorDetailsResponse;
import co.prestoapp.www.WebServices.Responses.VendorsResponse;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface RetrofitService {

    @POST("check-phone")
    Call<CheckPhoneResponse> CHECK_PHONE_RESPONSE_CALL(@Body CheckPhoneRequest checkPhoneRequest);


    @POST("verify/phone/send-code")
    Call<SocialPhoneResponse> SOCIAL_PHONE_RESPONSE_CALL(@Body SocialPhoneRequest socialPhoneRequest);

    @POST("verify/phone")
    Call<SocialValidationResponse> SOCIAL_VALIDATION_RESPONSE_CALL(@Body SocialValidationRequest socialValidationRequest);


    @POST("login")
    Call<LoginResponse> LOGIN_RESPONSE_CALL(@Body LoginRequest loginRequest);


    @POST("regions")
    Call<RegionsResponse> REGIONS_RESPONSE_CALL(@Body RegionRequest regionRequest);


    @POST("user/update")
    Call<UpdateResponse> UPDATE_RESPONSE_CALL(@Body UpdateRequest updateRequest);


    @POST("social-login")
    Call<SocialLoginResponse> SOCIAL_LOGIN_RESPONSE_CALL(@Body SocialLoginRequest socialLoginRequest);

    @POST("check-firebase-phone-authcode")
    Call<FirebasePhoneAuthResponse> FIREBASE_PHONE_AUTH_RESPONSE_CALL(@Body FirebasePhoneAuthRequest firebasePhoneAuthRequest);


    @POST("firebase-phone-getUserInfo")
    Call<FirebasePhoneInfoResponse> FIREBASE_PHONE_INFO_RESPONSE_CALL(@Body FirebasePhoneInfoRequest firebasePhoneInfoRequest);



    //-------------------------------------------------------------------------

    @POST("vendors")
    Call<VendorsResponse> VENDORS_RESPONSE_CALL(@Body VendorsRequest vendorsRequest);


    @POST("slider")
    Call<SliderResponse> SLIDER_RESPONSE_CALL(@Body SliderRequest sliderRequest);


    @POST("vendor")
    Call<VendorDetailsResponse> VENDOR_DETAILS_RESPONSE_CALL(@Body VendorDetailsRequest vendorDetailsRequest);


    @POST("orders/add")
    Call<AddOrderResponse> ADD_ORDER_RESPONSE_CALL(@Body AddOrderRequest addOrderRequest);

    @POST("orders")
    Call<MyOrderResponse> MY_ORDER_RESPONSE_CALL(@Body MyOrderRequest myOrderRequest);

    @POST("orders/single")
    Call<SingleOrderResponse> SINGLE_ORDER_RESPONSE_CALL(@Body SingleOrderRequest singleOrderRequest);


    @POST("orders/promo/apply")
    Call<PromoCodeResponse> PROMO_CODE_RESPONSE_CALL(@Body PromoCodeRequest promoCodeRequest);



    @POST("nps/check")
    Call<NPSCheckResponse> NPS_CHECK_RESPONSE_CALL(@Body NPSCheckRequest npsCheckRequest);

    @POST("nps/add")
    Call<NPSAddResponse> NPS_ADD_RESPONSE_CALL(@Body NPSAddRequest npsAddRequest);


    @POST("user/address-history")
    Call<AddressHistoryResponse> ADDRESS_HISTORY_RESPONSE_CALL(@Body AddressHistoryRequest addressHistoryRequest);


    @POST("orders/add-report")
    Call<ReportResponse> REPORT_RESPONSE_CALL(@Body ReportRequest reportRequest);

}
