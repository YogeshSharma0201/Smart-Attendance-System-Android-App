package com.example.yogeshsharma.smartattendancesystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.android.dx.stock.ProxyBuilder;
import com.fitc.wifihotspot.MyOnStartTetheringCallback;
import com.fitc.wifihotspot.MyOreoWifiManager;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    Button button, button2;
    private WifiManager mWifiManager;
    private Context context;
    WifiApManager wifiApManager;
    private ConnectivityManager mConnectivityManager;
    private MyOreoWifiManager myOreoWifiManager;


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
                Context context = getApplicationContext();

//                CharSequence text = "Permission granted";


//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//
//                    text = "Permission Not granted";
//                }

//                wifiApManager.setWifiApEnabled(null, true);

//                System.out.println("AP state: " + wifiApManager.getWifiApState());

//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, text, duration);

//                System.out.println(wifiApManager.isWifiApEnabled());

//                if(wifiApManager.isWifiApEnabled()) {
//                    myOreoWifiManager.stopTethering();
//                } else {
//                    myOreoWifiManager.startTethering(new MyOnStartTetheringCallback() {
//                        @Override
//                        public void onTetheringStarted() {
//                            System.out.println("Started");
//                        }
//
//                        @Override
//                        public void onTetheringFailed() {
//                            System.out.println("Failed");
//                        }
//                    });
//                }

                startActivity(new Intent(MainActivity.this, TeacherActivity.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//                wifi.setWifiEnabled(true);

//                wifiApManager.setWifiApEnabled(null, false);
//                ActivityCompat.requestPermissions(that, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                startActivity(new Intent(MainActivity.this, StudentActivity.class));
            }
        });
    }

    private void init(Context context) {
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        this.context = context;
        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
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
    }

    public void configureHotspot(String name, String password) {
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = name;
        apConfig.preSharedKey = password;
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try {
            Method setConfigMethod = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            boolean status = (boolean) setConfigMethod.invoke(mWifiManager, apConfig);
            Log.d(TAG, "setWifiApConfiguration - success? " + status);
        } catch (Exception e) {
            Log.e(TAG, "Error in configureHotspot");
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("Perms: "+grantResults[0] + " "+ PackageManager.PERMISSION_GRANTED+permissions[0]);
    }
}
