package co.prestoapp.www.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.prestoapp.www.R;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {

    private EditText PhoneNumber;
    private Button  Continuation;
    private String PhoneNumberStr;
    private ProgressDialog loadingDialog;
    SharedPreferences sharedPrefUser;
    SharedPreferences.Editor editUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        //make a keyPad below editText (editText not hidden by keyPad)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //change the color of status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
         loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getResources().getString(R.string.please_wait));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(true);

         PhoneNumber = (EditText)findViewById(R.id.activity_phone_edit_PhoneNumber);
         Continuation = (Button)findViewById(R.id.activity_phone_button_Continuation);

         Continuation.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 PhoneNumberStr = PhoneNumber.getText().toString();
                 if(PhoneNumberStr.matches("")||PhoneNumberStr==null)
                     Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_enter_phone), Toast.LENGTH_SHORT).show();
                 else
                 {
                         loadingDialog.show();
                         PhoneAuthProvider.getInstance().verifyPhoneNumber("+20"+PhoneNumberStr,
                                 60,
                                 TimeUnit.SECONDS,
                                 PhoneActivity.this,
                                 new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                             @Override
                             public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                             }

                             @Override
                             public void onVerificationFailed(FirebaseException e) {
                                 loadingDialog.dismiss();
                                 if(e.getMessage().contains(getResources().getString(R.string.phone_format_error)))
                                     Toast.makeText(PhoneActivity.this,getResources().getString(R.string.toast_phone_format_error), Toast.LENGTH_SHORT).show();
                                 else
                                     Toast.makeText(PhoneActivity.this,getResources().getString(R.string.toast_500), Toast.LENGTH_SHORT).show();

                             }

                             @Override
                             public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                                 finish();
                                 Intent intent = new Intent(PhoneActivity.this,ValidationCodeActivity.class);
                                 intent.putExtra("VERIFICATION_ID",verificationId);
                                 intent.putExtra("PHONE",PhoneNumberStr);
                                 startActivity(intent);
                                 loadingDialog.dismiss();
                                     }
                         });
                     }
             }
         });

    }

}
