package co.prestoapp.www.Presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import co.prestoapp.www.R;
import co.prestoapp.www.Views.HomeActivity;
import co.prestoapp.www.Views.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class SplashPresenter {

    public GoogleSignInClient googleSignInClient;
    public GoogleSignInAccount alreadyloggedAccount;
    public SharedPreferences sharedPrefUser;
    public boolean checkPhoneLogin;
    Context context;
    public SplashPresenter(Context context){
        this.context = context;
    }

    public GoogleSignInAccount configGoogle(){
        alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(context);
        sharedPrefUser = context.getSharedPreferences("USER",MODE_PRIVATE);
        checkPhoneLogin = sharedPrefUser.getBoolean("CHECK_PHONE_LOGIN",false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);

        return alreadyloggedAccount;
    }

    public void googleOnLoggedIn(){
        SharedPreferences sharedPrefUser = context.getSharedPreferences("USER",MODE_PRIVATE);
        if(!sharedPrefUser.getString("REGION_ID","").matches("") && sharedPrefUser.getString("REGION_ID","")!=null)
        {
            ((Activity)context).finish();
            context.startActivity(new Intent(context, HomeActivity.class));
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            ((Activity)context).finish();
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            cleanSharedPreference();
        }
    }

    public boolean checkFaceBookLoginState(){
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            SharedPreferences sharedPrefUser = context.getSharedPreferences("USER",MODE_PRIVATE);
            String name = sharedPrefUser.getString("NAME","");
            if(!name.matches("") && name!=null) {
                return true;
            }
        }
        return false;
    }

    public void cleanSharedPreference(){
        SharedPreferences sharedPrefUser = context.getSharedPreferences("USER",MODE_PRIVATE);
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
    }

}
