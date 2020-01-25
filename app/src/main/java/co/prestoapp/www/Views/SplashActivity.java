package co.prestoapp.www.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.login.LoginManager;

import co.prestoapp.www.Presenters.SplashPresenter;
import co.prestoapp.www.R;

public class SplashActivity extends AppCompatActivity {

    //the display time of screen is 3 seconds
    private final int SPLASH_DISPLAY_LENGTH=3000;
    SplashPresenter splashPresenter;
    SharedPreferences sharedPrefUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this two lines make the splash full screen without actionBar or title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splashPresenter = new SplashPresenter(this);
        sharedPrefUser = getSharedPreferences("USER",MODE_PRIVATE);


        //This code makes a delay of 3 seconds and then executes the code inside the run function
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (splashPresenter.configGoogle() != null) {
                    splashPresenter.googleOnLoggedIn();
                }
                else if(splashPresenter.checkFaceBookLoginState())
                {
                    if(!sharedPrefUser.getString("REGION_ID","").matches("") && sharedPrefUser.getString("REGION_ID","")!=null)
                    {
                        SplashActivity.this.finish();
                        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    }
                    else
                    {
                        splashPresenter.cleanSharedPreference();
                        LoginManager.getInstance().logOut();
                        finish();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
                else if(splashPresenter.checkPhoneLogin)
                {
                    if(sharedPrefUser.getString("REGION_ID","")==null || sharedPrefUser.getString("REGION_ID","").matches(""))
                    {
                        splashPresenter.cleanSharedPreference();
                        SplashActivity.this.finish();
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    }
                    else
                    {
                        SplashActivity.this.finish();
                        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    }

                }
                else
                {
                    splashPresenter.cleanSharedPreference();
                    SplashActivity.this.finish();
                    Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        },SPLASH_DISPLAY_LENGTH);


    }


}
