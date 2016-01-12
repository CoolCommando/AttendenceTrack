package attend1.com.example.sujith.attend1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class LocUpdate extends Service {

Location mcurrentlocation;
double lon, lat;
String tim;
private Timer timer;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service is created.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service is started.", Toast.LENGTH_SHORT).show();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            synchronized public void run() {

                tim = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new java.util.Date());
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if(lm != null){
                    mcurrentlocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(mcurrentlocation !=null){
                        lon= mcurrentlocation.getLongitude();
                        lat= mcurrentlocation.getLatitude();
                    }
                    else{
                        mcurrentlocation= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(mcurrentlocation !=null){
                            lon= mcurrentlocation.getLongitude();
                            lat= mcurrentlocation.getLatitude();
                        }
                    }
                }
                SharedPreferences pref = getSharedPreferences("list", Context.MODE_PRIVATE);
               // ParseObject testobj = new ParseObject("LocationTracker");
                ParseObject testobj= new ParseObject("TestTracker");
                testobj.put("Longitude", lon);
                testobj.put("Latitude", lat);
                testobj.put("Time", tim);
                testobj.put("EmpID", pref.getString("emid", ""));
                testobj.saveEventually();
            }
        }, 10000, 10000);
        return START_STICKY;
    }

      @Override
    public void onDestroy() {
        super.onDestroy();
          timer.cancel();
        Toast.makeText(this, "Service is stopped.", Toast.LENGTH_SHORT).show();
    }
}
