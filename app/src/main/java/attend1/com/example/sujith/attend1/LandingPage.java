package attend1.com.example.sujith.attend1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.parse.ParseObject;
import java.text.SimpleDateFormat;


public class LandingPage extends AppCompatActivity implements View.OnClickListener {

    Button wfhbtn, offbtn, punchbtn;
    Location mlastlocation;
    double lon, lat;
    String tim, eid;
    boolean bool;
    TextView tview;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        punchbtn = (Button) findViewById(R.id.punchbtn);
        wfhbtn = (Button) findViewById(R.id.wfhbtn);
        offbtn = (Button) findViewById(R.id.offbtn);
        tview = (TextView) findViewById(R.id.msg);

        Typeface san = Typeface.createFromAsset(getAssets(), getString(R.string.fon));
        tview.setTypeface(san);

        punchbtn.setOnClickListener(this);
        wfhbtn.setOnClickListener(this);
        offbtn.setOnClickListener(this);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(R.string.warn);
            dialog.setCancelable(false);
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    finish();
                    //get gps
                }
            });
            dialog.show();
        } else {

            progress= new ProgressDialog(this);
            progress.setMessage("Working...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);

            SharedPreferences pref = getSharedPreferences("list", Context.MODE_PRIVATE);
            eid = pref.getString("emid", "");

                mlastlocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(mlastlocation !=null){
                    lon= mlastlocation.getLongitude();
                    lat= mlastlocation.getLatitude();
                }


            SharedPreferences pref1 = getSharedPreferences("list1", Context.MODE_PRIVATE);

            if (pref1.contains("cbool")) {
                bool = false;
                punchbtn.setText("Punch Out");
                wfhbtn.setEnabled(false);
                offbtn.setEnabled(false);
                wfhbtn.setVisibility(View.INVISIBLE);
                offbtn.setVisibility(View.INVISIBLE);
            } else {
                bool = true;
            }
        }
    }

    @Override
    public void onClick(View v) {

        progress.show();

                tim = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new java.util.Date());
               // ParseObject testobj = new ParseObject("LocationTracker");
        ParseObject testobj= new ParseObject("TestTracker");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                if(mlastlocation == null){
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if(lm != null){
                        mlastlocation= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(mlastlocation !=null){
                            lon= mlastlocation.getLongitude();
                            lat= mlastlocation.getLatitude();
                        }
                    }
                }

                String deviceId = Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                switch (v.getId()) {

                    case (R.id.punchbtn):

                        if (bool) {
                            punchbtn.setText("Punch Out");
                            bool = false;

                            wfhbtn.setEnabled(false);
                            offbtn.setEnabled(false);
                            wfhbtn.setVisibility(View.INVISIBLE);
                            offbtn.setVisibility(View.INVISIBLE);

                            builder.setTitle("Punch In");
                            builder.setMessage("You've punched in at longitude- " + lon + ", latitude- " + lat + ", time- " + tim);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            progress.dismiss();

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
                            testobj.put("Device_ID", deviceId);
                            testobj.saveEventually();

                        } else {

                            punchbtn.setText("Punch In");
                            bool = true;

                            wfhbtn.setEnabled(true);
                            offbtn.setEnabled(true);
                            wfhbtn.setVisibility(View.VISIBLE);
                            offbtn.setVisibility(View.VISIBLE);

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
                            Intent intent = new Intent(this, LocUpdate.class);
                            stopService(intent);
                            progress.dismiss();

                            SharedPreferences pref1 = getSharedPreferences("list1", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edi1 = pref1.edit();
                            edi1.remove("cbool");
                            edi1.commit();

                            testobj.put("Longitude", lon);
                            testobj.put("Latitude", lat);
                            testobj.put("Time", tim);
                            testobj.put("EmpID", eid);
                            testobj.put("Comments", "Punch Out");
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
                        testobj.put("Device_ID", deviceId);
                        testobj.saveEventually();
                        progress.dismiss();
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
                        testobj.put("Device_ID", deviceId);
                        testobj.saveEventually();
                        progress.dismiss();
                        break;
                }
    }
}