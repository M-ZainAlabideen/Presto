package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import co.prestoapp.www.R;
import co.prestoapp.www.WebServices.Requests.SocialLoginRequest;
import co.prestoapp.www.WebServices.Responses.SocialLoginResponse;
import co.prestoapp.www.WebServices.RetrofitWebService;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String GOOGLE_PLATFORM="google";
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount account;
    private FirebaseAuth mAuth;
    private String googleUserName;
    private String googleId;
    private String googleEmail;

    private static final String FACEBOOK_PLATFORM="facebook";
    private LoginButton FaceBook;
    private CallbackManager callbackManager;

    private ProgressDialog loadingDialog;
    private SharedPreferences sharedPrefUser;
    private SharedPreferences.Editor editUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);
        //Making activity full screen with status bar on top of it
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        ,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



        FaceBook = (LoginButton)findViewById(R.id.activity_main_loginBtn_Facebook);
        callbackManager = CallbackManager.Factory.create();
        FaceBook.setReadPermissions(Arrays.asList("email","public_profile"));
        FaceBook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        googleConfig();
    }


    private void googleConfig(){
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
    }
    private void signIn() {
        loadingDialog.show();
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            googleUserName = account.getDisplayName();
                            googleEmail  = account.getEmail();
                            googleId = account.getIdToken();
                            SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                            SharedPreferences.Editor editUser = sharedPrefUser.edit();
                            editUser.putString("LOGIN_WITH", GOOGLE_PLATFORM).commit();
                            sharedPrefUser.edit().putString("NAME", googleUserName).commit();
                            sharedPrefUser.edit().putString("EMAIL",googleEmail).commit();
                            SocialLoginApi(googleUserName,null,googleEmail,googleId,GOOGLE_PLATFORM);

                        } else {

                            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_google_sign), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void MainClick(View view) {

        switch (view.getId())
        {
            case R.id.activity_main_image_Google:
                signIn();
                break;
            case R.id.activity_main_text_Phone:
                finish();
                startActivity(new Intent(this, PhoneActivity.class));
                SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                SharedPreferences.Editor editUser = sharedPrefUser.edit();
                editUser.putString("LOGIN_WITH","phone").commit();
                break;
        }
    }

    public void SocialLoginApi(String Name,String phone,String email,String SocialToken,String SocialPlatform){
        loadingDialog.show();
        RetrofitWebService.getService().SOCIAL_LOGIN_RESPONSE_CALL(new SocialLoginRequest(Name,phone,email,SocialToken,SocialPlatform))
                .enqueue(new Callback<SocialLoginResponse>() {
                    @Override
                    public void onResponse(Call<SocialLoginResponse> call, Response<SocialLoginResponse> response) {
                        SocialLoginResponse socialLoginResponse = response.body();
                        if(socialLoginResponse != null) {
                            String code = socialLoginResponse.getCode();
                            co.prestoapp.www.WebServices.Models.Response myResponse=socialLoginResponse.getResponse();
                            switch (code) {
                                case "200":
                                    sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
                                    editUser = sharedPrefUser.edit();
                                    editUser.putString("API_TOKEN",myResponse.getApi_token()).commit();
                                    editUser.putString("NAME",myResponse.getUser().getName()).commit();
                                    editUser.putString("PHONE",myResponse.getUser().getPhone()).commit();
                                    editUser.putString("SOCIAL_PLATFORM",myResponse.getUser().getSocial_platform()).commit();
                                    editUser.putString("REGION_ID",myResponse.getUser().getRegion_id()).commit();
                                    if(myResponse.getUser().getRegion()!=null)
                                        editUser.putString("REGION_NAME",myResponse.getUser().getRegion().getName()).commit();
                                    editUser.putString("ACCOUNT_TYPE",myResponse.getUser().getAccount_type()).commit();
                                    editUser.putString("ADDRESS",myResponse.getUser().getAddress()).commit();

                                            if(myResponse.getUser().getPhone()==null || myResponse.getUser().getPhone().matches(""))
                                            {
                                                finish();
                                                Intent intent = new Intent(MainActivity.this,PhoneActivity.class);
                                                intent.putExtra("COME_FROM","main");
                                                startActivity(intent);
                                            }
                                            else if(!myResponse.getUser().getPhone_verified())
                                            {
                                                //SocialCheckPhoneApi(myResponse.getApi_token(),myResponse.getUser().getPhone());
                                                FirebasePhoneAuth(myResponse.getUser().getPhone());
                                            }
                                            else if(myResponse.getUser().getAddress()==null  || myResponse.getUser().getAddress().matches(""))
                                            {

                                                finish();
                                                Intent intent = new Intent(MainActivity.this,CreateAccount2Activity.class);
                                                intent.putExtra("COME_FROM","main");
                                                startActivity(intent);
                                            }

                                            else
                                            {
                                                finish();
                                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
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
                                    loadingDialog.dismiss();
                                    break;
                                default:
                                    loadingDialog.dismiss();
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_500), Toast.LENGTH_SHORT).show();
                                    break;
                            }}
                    }

                    @Override
                    public void onFailure(Call<SocialLoginResponse> call, Throwable t) {

                    }
                });
    }

    private void FirebasePhoneAuth(final String phone){
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+20"+phone,
                60,
                TimeUnit.SECONDS,
                MainActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        finish();
                        Intent intent = new Intent(MainActivity.this,ValidationCodeActivity.class);
                        intent.putExtra("VERIFICATION_ID",verificationId);
                        intent.putExtra("PHONE",phone);
                        startActivity(intent);
                        loadingDialog.dismiss();
                    }
                });
    }


        AccessTokenTracker tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if(currentAccessToken!=null)
                    getProfileInfo(currentAccessToken);
            }
        };

    private void getProfileInfo(AccessToken token) {

        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    SharedPreferences sharedPrefUser = getSharedPreferences("USER", MODE_PRIVATE);
                    sharedPrefUser.edit().putString("LOGIN_WITH", FACEBOOK_PLATFORM).commit();
                    sharedPrefUser.edit().putString("NAME", first_name+" "+last_name).commit();
                    sharedPrefUser.edit().putString("EMAIL",email).commit();

                    SocialLoginApi(first_name+" "+last_name,null,email,id,FACEBOOK_PLATFORM);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void GoogleSignOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public void FacebookSignOut() {
        LoginManager.getInstance().logOut();
    }

    public void PhoneSignOut() {
        SharedPreferences sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);
        sharedPrefUser.edit().putBoolean("CHECK_PHONE_LOGIN",false).commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}