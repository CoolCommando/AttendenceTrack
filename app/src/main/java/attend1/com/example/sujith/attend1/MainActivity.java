package attend1.com.example.sujith.attend1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginbtn;
    EditText empid;
    private TextView welcome;
    String tim;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginbtn= (Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);
        empid= (EditText) findViewById(R.id.empid);
        welcome= (TextView) findViewById(R.id.welcome);

        Typeface san= Typeface.createFromAsset(getAssets(), getString(R.string.fon));
        welcome.setTypeface(san);

        progress= new ProgressDialog(this);
        progress.setMessage("Working...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        SharedPreferences pref= getSharedPreferences("list", Context.MODE_PRIVATE);
        if(pref.contains("emid")){
            Intent intent1= new Intent(this,LandingPage.class);
            startActivity(intent1);
            finish();
        }
   }
           public void unavailable(){
               Toast.makeText(this, R.string.un, Toast.LENGTH_LONG).show();
               progress.dismiss();
           }

    public void success(){
        SharedPreferences pref = getSharedPreferences("list", Context.MODE_PRIVATE);
        SharedPreferences.Editor edi = pref.edit();
        edi.putString("emid", empid.getText().toString());
        edi.commit();

        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        tim = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy").format(new java.util.Date());
        //ParseObject testobj = new ParseObject("LocationTracker");
        ParseObject testobj= new ParseObject("TestTracker");
        testobj.put("Comments", "Registered");
        testobj.put("Time", tim);
        testobj.put("Device_ID", deviceId);
        testobj.put("EmpID", empid.getText().toString());
        testobj.saveInBackground();

        Toast.makeText(this, R.string.reg, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
        progress.dismiss();
        finish();
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.4.4");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (Exception e)          { e.printStackTrace(); }
        return false;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginbtn) {

            progress.show();

            if (!isOnline()) {

                Toast.makeText(this, R.string.con, Toast.LENGTH_LONG).show();
                progress.dismiss();
            }

            else {

                if (empid.getText().toString().matches("")) {

                    Toast.makeText(this, R.string.invalid, Toast.LENGTH_LONG).show();
                    progress.dismiss();

                } else {

                    ParseQuery query = new ParseQuery("Dtable");
                    query.whereEqualTo("EmpID", empid.getText().toString());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {

                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                success();
                            } else {
                                unavailable();
                            }
                        }
                    });
                }
            }
        }
    }    }