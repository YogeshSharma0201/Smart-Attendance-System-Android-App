package com.example.yogeshsharma.smartattendancesystem;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fitc.wifihotspot.MyOreoWifiManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class StudentActivity extends AppCompatActivity {
    private Context context;
    private RecyclerView listOfClasses;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Pair<String, Pair<String, String>>> classesNames;
    private ClassesListAdapter classesListAdapter;
    private ProgressBar spinner;
    private WifiManager mWifiManager;
    private TextView textView;
    private Thread checkClasses;
    private HashMap<String, Pair<String, String>> dataBase;
    private WifiApManager wifiApManager;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private MyOreoWifiManager myOreoWifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        context = this.getApplicationContext();
        init();

//        Toolbar toolbar = findViewById(R.id)
    }

    public void checkHotspots() {
        List<ScanResult> list = mWifiManager.getScanResults();

        if(!mWifiManager.isWifiEnabled()) mWifiManager.setWifiEnabled(true);
        mWifiManager.startScan();

        classesNames.clear();
        for(ScanResult scanResult : list) {
            System.out.println(scanResult.BSSID + scanResult.SSID);
            if(dataBase.containsKey(scanResult.BSSID)) {
                classesNames.add(
                    Pair.create(
                        dataBase.get(scanResult.BSSID).first,
                        Pair.create(
                                dataBase.get(scanResult.BSSID).second,
                                scanResult.BSSID
                        )
                    )
                );
            }
        }
        System.out.println(classesNames);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classesListAdapter = new ClassesListAdapter(context, classesNames, mWifiManager, dataBase);
                listOfClasses.setAdapter(classesListAdapter);
                if(classesNames.size() != 0) {
                    hideLoader();
                    listOfClasses.setVisibility(View.VISIBLE);
                } else {
                    showLoader();
                    listOfClasses.setVisibility(View.GONE);
                }
            }
        });
    }

    public void init() {
        dataBase = new HashMap<>();
        dataBase.put("c8:3a:35:b3:4d:a0", Pair.create("Math class", "Yogesh"));
        dataBase.put("c8:3a:35:09:6b:60", Pair.create("English class", "Harsh"));
        dataBase.put("92:21:81:44:ef:08", Pair.create("Science class", "AndroidAP"));
        dataBase.put("38:e6:0a:c8:45:62", Pair.create("History class", "LazyZ"));

        listOfClasses = findViewById(R.id.classesList);
        spinner = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loadingText);

        listOfClasses.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(context);
        listOfClasses.setLayoutManager(linearLayoutManager);
        classesNames =  new ArrayList<>();
        classesListAdapter = new ClassesListAdapter(context, classesNames, mWifiManager, dataBase);
        listOfClasses.setAdapter(classesListAdapter);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiApManager = new WifiApManager(this);
        myOreoWifiManager = new MyOreoWifiManager(context);

        if(wifiApManager.isWifiApEnabled()) {
            myOreoWifiManager.stopTethering();
        }

        checkClasses = new Thread() {
            @Override
            public void run() {
                while(!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        checkHotspots();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        checkClasses.start();

    }

    public void showLoader() {
        spinner.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        spinner.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
    }
}
