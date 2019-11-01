package com.example.yogeshsharma.smartattendancesystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.fitc.wifihotspot.MyOreoWifiManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    Button button, button2, button3;
    private WifiManager mWifiManager;
    private Context context;
    WifiApManager wifiApManager;
    private ConnectivityManager mConnectivityManager;
    private MyOreoWifiManager myOreoWifiManager;
    private String TypeOfUser = "";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    TextView welcomeText;
    JSONObject user = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Context context = getApplicationContext();

        init(context);

        final MainActivity that = this;

        wifiApManager = new WifiApManager(this);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(TypeOfUser.equals("student")) {
                    startActivity(new Intent(MainActivity.this, StudentActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, TeacherActivity.class));
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, StudentActivity.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void init(Context context) {
        try {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String userString = sharedpreferences.getString("User", "{\"id\":1,\"firstName\":\"Yogesh\"}");

            user = new JSONObject(userString);
            user = user.getJSONObject("user");
            TypeOfUser = user.getString("typeOfUser");

            welcomeText = findViewById(R.id.textView3);
            button = findViewById(R.id.button);
            button2 = findViewById(R.id.button2);
            button3 = findViewById(R.id.button3);

            if(TypeOfUser.equals("student")) {
                button.setText("Register Your Attendacne");
            } else {
                button.setText("Start taking attendance");
            }

            welcomeText.setText("Hi " + user.getString("firstName") + " " + user.getString("lastName") + "!");

            this.context = context;
            mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
            mConnectivityManager = context.getSystemService(ConnectivityManager.class);
            myOreoWifiManager = new MyOreoWifiManager(context);


            final String CoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
            final String AccessWifi = Manifest.permission.ACCESS_WIFI_STATE;
            final String ChangeWifi = Manifest.permission.CHANGE_WIFI_STATE;


            if (checkSelfPermission(CoarseLocation) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }

            if (checkSelfPermission(AccessWifi) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE}, 123);
            }

            if (checkSelfPermission(ChangeWifi) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE}, 123);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("init: ", e.getMessage());
//            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("Perms: "+grantResults[0] + " "+ PackageManager.PERMISSION_GRANTED+permissions[0]);
    }
}
