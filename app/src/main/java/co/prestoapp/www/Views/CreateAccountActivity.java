package co.prestoapp.www.Views;

import android.content.Intent;
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

public class CreateAccountActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Mail;
    private Button Continuation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //make a keyPad below editText (editText not hidden by keyPad)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //change the color of status bar
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                View view = getWindow().getDecorView();
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        Name = (EditText)findViewById(R.id.activity_create_edit_Name);
        Mail = (EditText)findViewById(R.id.activity_create_edit_Mail);
        Continuation = (Button)findViewById(R.id.activity_create_button_Continuation);

        Continuation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String NameStr = Name.getText().toString();
               String MailStr = Mail.getText().toString();

               if(NameStr.matches("")||NameStr==null)
                   Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_name),Toast.LENGTH_LONG).show();
               else if(MailStr.matches("")||MailStr==null)
                   Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_enter_mail),Toast.LENGTH_LONG).show();
               else if(!MailStr.contains("@") || !MailStr.contains("."))
                   Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_correct_mail),Toast.LENGTH_LONG).show();
               else
                {
                    finish();
                    Intent intent = new Intent(CreateAccountActivity.this,CreateAccount2Activity.class);
                    intent.putExtra("NAME_KEY",NameStr);
                    intent.putExtra("MAIL_KEY",MailStr);
                    intent.putExtra("COME_FROM","create_account");
                    startActivity(intent);
                }



            }
        });
    }
}
