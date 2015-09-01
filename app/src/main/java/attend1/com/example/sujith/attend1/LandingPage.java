package attend1.com.example.sujith.attend1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;

public class LandingPage extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button wfhbtn, offbtn, punchbtn;
    GoogleApiClient mclient;
    Location mlastlocation;
    double lon, lat;
    String tim, eid;
    boolean bool;
    TextView tview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        punchbtn = (Button) findViewById(R.id.punchbtn);
        wfhbtn = (Button) findViewById(R.id.wfhbtn);
        offbtn = (Button) findViewById(R.id.offbtn);
        tview= (TextView) findViewById(R.id.msg);

        Typeface san= Typeface.createFromAsset(getAssets(), getString(R.string.fon));
        tview.setTypeface(san);

        punchbtn.setOnClickListener(this);
        wfhbtn.setOnClickListener(this);
        offbtn.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences("list", Context.MODE_PRIVATE);
        eid= pref.getString("emid","");

        mclient= new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mclient.connect();

        SharedPreferences pref1= getSharedPreferences("list1", Context.MODE_PRIVATE);

            if(pref1.contains("cbool")){
                bool= false;
                punchbtn.setText("Punch Out");
                wfhbtn.setEnabled(false);
                offbtn.setEnabled(false);
            }
        else{
                bool= true;
            }
            }

    @Override
    public void onConnected(Bundle bundle) {

        mlastlocation= LocationServices.FusedLocationApi.getLastLocation(mclient);
        lon= mlastlocation.getLongitude();
        lat= mlastlocation.getLatitude();
                     }

    @Override
    public void onConnectionSuspended(int i) {
        mclient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        AlertDialog.Builder err = new AlertDialog.Builder(this);
        err.setTitle("Error");
        err.setMessage("Error code- " + connectionResult.getErrorCode());
        err.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog errdialog= err.create();
        errdialog.show();
    }

    @Override
    public void onClick(View v) {

                tim = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy").format(new java.util.Date());
                ParseObject testobj = new ParseObject("LocationTracker");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                //TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                //String mPhoneNumber = tMgr.getLine1Number();
                String deviceId = Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                switch (v.getId()) {

                    case (R.id.punchbtn):

                        if (bool) {
                            punchbtn.setText("Punch Out");
                            bool = false;

                            wfhbtn.setEnabled(false);
                            offbtn.setEnabled(false);


                            builder.setTitle("Punch In");
                            builder.setMessage("You've punched in at longitude- " + lon + ", latitude- " + lat + ", time- " + tim);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            Intent intent = new Intent(this, LocUpdate.class);
                            startService(intent);

                            SharedPreferences pref1 = getSharedPreferences("list1", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edi1 = pref1.edit();
                            edi1.putString("cbool", "false");
                            edi1.commit();

                            testobj.put("Longitude", lon);
                            testobj.put("Latitude", lat);
                            testobj.put("Time", tim);
                            testobj.put("EmpID", eid);
                            testobj.put("Comments", "Punch In");
                            // testobj.put("Phone_Number", mPhoneNumber);
                            testobj.put("Device_ID", deviceId);
                            testobj.saveEventually();

                            if (mclient.isConnected()) {
                                mclient.disconnect();
                            }

                        } else {

                            punchbtn.setText("Punch In");
                            bool = true;

                            wfhbtn.setEnabled(true);
                            offbtn.setEnabled(true);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                            builder1.setTitle("Punch Out");
                            builder1.setMessage("You've punched out at- " + lon + ", latitude- " + lat + ", time- " + tim);
                            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog1 = builder1.create();
                            dialog1.show();
                            if (mclient.isConnected()) {
                                mclient.disconnect();
                            }
                            Intent intent = new Intent(this, LocUpdate.class);
                            stopService(intent);

                            SharedPreferences pref1 = getSharedPreferences("list1", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edi1 = pref1.edit();
                            edi1.remove("cbool");
                            edi1.commit();

                            testobj.put("Longitude", lon);
                            testobj.put("Latitude", lat);
                            testobj.put("Time", tim);
                            testobj.put("EmpID", eid);
                            testobj.put("Comments", "Punch Out");
                            // testobj.put("Phone_Number", mPhoneNumber);
                            testobj.put("Device_ID", deviceId);
                            testobj.saveEventually();

                        }
                        break;
                    case (R.id.wfhbtn):

                        AlertDialog.Builder wfh = new AlertDialog.Builder(this);
                        wfh.setTitle("Work from home");
                        wfh.setMessage("You have chosen to work from home at- " + tim);
                        wfh.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog wfhdialog = wfh.create();
                        wfhdialog.show();

                        testobj.put("Longitude", lon);
                        testobj.put("Latitude", lat);
                        testobj.put("Time", tim);
                        testobj.put("EmpID", eid);
                        testobj.put("Comments", "Work from home.");
                        // testobj.put("Phone_Number", mPhoneNumber);
                        testobj.put("Device_ID", deviceId);
                        testobj.saveEventually();

                        if (mclient.isConnected()) {
                            mclient.disconnect();
                        }
                        break;
                    case (R.id.offbtn):

                        AlertDialog.Builder offs = new AlertDialog.Builder(this);
                        offs.setTitle("Off-site");
                        offs.setMessage("You're off site- " + tim);
                        offs.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog offsdialog = offs.create();
                        offsdialog.show();

                        testobj.put("Longitude", lon);
                        testobj.put("Latitude", lat);
                        testobj.put("Time", tim);
                        testobj.put("EmpID", eid);
                        testobj.put("Comments", "Off-site");
                        // testobj.put("Phone_Number", mPhoneNumber);
                        testobj.put("Device_ID", deviceId);
                        testobj.saveEventually();

                        if (mclient.isConnected()) {
                            mclient.disconnect();
                        }
                        break;

                }

    }
}