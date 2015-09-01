package attend1.com.example.sujith.attend1;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class LocUpdate extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

Location mcurrentlocation;
GoogleApiClient gclient;
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
        gclient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        gclient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service is started.", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

      @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Service is stopped.", Toast.LENGTH_SHORT).show();
            if(gclient.isConnected()) {
                timer.cancel();
            gclient.disconnect();}
    }

    @Override
    public void onConnected(Bundle bundle) {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            synchronized public void run() {

                tim = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy").format(new java.util.Date());

                mcurrentlocation = LocationServices.FusedLocationApi.getLastLocation(gclient);

                    lon = mcurrentlocation.getLongitude();
                    lat = mcurrentlocation.getLatitude();

                SharedPreferences pref = getSharedPreferences("list", Context.MODE_PRIVATE);
                 ParseObject testobj = new ParseObject("LocationTracker");
                    testobj.put("Longitude", lon);
                    testobj.put("Latitude", lat);
                    testobj.put("Time", tim);
                    testobj.put("EmpID", pref.getString("emid", ""));
                    testobj.saveEventually();
            }
        }, 10000, 10000);
   }

    @Override
    public void onConnectionSuspended(int i) {gclient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        gclient.connect();
        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }
}
