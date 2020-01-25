package co.prestoapp.www.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.Toast;

public class StatusLoadingService extends Service {

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
