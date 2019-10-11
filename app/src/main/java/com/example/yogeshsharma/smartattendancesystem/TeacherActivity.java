package com.example.yogeshsharma.smartattendancesystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fitc.wifihotspot.MyOnStartTetheringCallback;
import com.fitc.wifihotspot.MyOreoWifiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TeacherActivity extends AppCompatActivity {
    private Button startHotspot;
    private Context context;
    private WifiApManager wifiApManager;
    private EditText editText;
    private WifiManager mWifiManager;
    private Thread checkConnections;
    private HashSet<String> attendance;
    private TextView TotalAttendance, TableRoll, TableName;
    private RecyclerView listOfStudents;
    private LinearLayoutManager linearLayoutManager;
    private ListAdapter listAdapter;
    ArrayList<Pair<String, String>> personNames = new ArrayList<>();
    HashMap<String, Pair<String, String>> dataBase;
    String[] country = { "English", "Maths", "Science", "History"};
    private Spinner spin;
    private int state;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private MyOreoWifiManager myOreoWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        context = getApplicationContext();
        init(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStartHotspot(View view) {
        // TODO: Check sdk version and decide how to turn on hotspot

        if(mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(false);


        if(state == 1) {
            myOreoWifiManager.stopTethering();
            checkConnections.interrupt();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayToast("Saving Attendance...");
                    hideInfo();
                    startHotspot.setText("Take Attendance");
                }
            });
            state = 0;
        } else {
            if(wifiApManager.isWifiApEnabled()) {
                checkConnections.start();
                state = 1;
                startHotspot.setText("Save Attendance");
                showInfo();
            } else {
                myOreoWifiManager.startTethering(new MyOnStartTetheringCallback() {
                    @Override
                    public void onTetheringStarted() {
                        checkConnections.start();
                        state = 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startHotspot.setText("Save Attendance");
                                showInfo();
                            }
                        });
                    }

                    @Override
                    public void onTetheringFailed() {
                        System.out.println("Failed");
                    }
                });
            }
        }
    }

    public void getConnected() {

        wifiApManager.getClientList(true, new FinishScanListener() {
            @Override
            public void onFinishScan(ArrayList<ClientScanResult> clients) {
                for(ClientScanResult client : clients) {
                    attendance.add(client.getHWAddr());
                }
//                System.out.println(attendance);
                personNames.clear();
                for(String macAddress : attendance) {
                    personNames.add(dataBase.get(macAddress));
                    System.out.println(dataBase.get(macAddress));
                }
                listAdapter = new ListAdapter(context, personNames);
                listOfStudents.setAdapter(listAdapter);

                System.out.println("persons" + personNames);
                if(personNames.size() != 0) progressBar.setVisibility(View.GONE);
                else progressBar.setVisibility(View.VISIBLE);

                TotalAttendance.setText("Total attendance till now: " + attendance.size());
            }
        });
    }

    private void displayToast(String str) {
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, str, duration).show();
    }

    private void getWritePermissions() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        startActivity(intent);
    }

    private void init(Context context) {
        state = 0;
        startHotspot = findViewById(R.id.button3);
        wifiApManager = new WifiApManager(this);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        myOreoWifiManager = new MyOreoWifiManager(context);
        attendance = new HashSet<>();
        TotalAttendance = findViewById(R.id.textView2);
        TableRoll = findViewById(R.id.tableRoll);
        TableName = findViewById(R.id.tableName);
        TotalAttendance.setText("Total attendance till now: 0");
        listOfStudents = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(context);
        listOfStudents.setLayoutManager(linearLayoutManager);
        listAdapter = new ListAdapter(context, personNames);
        listOfStudents.setAdapter(listAdapter);
        dataBase = new HashMap<>();
        dataBase.put("90:21:81:44:ef:08", new Pair<>("049", "Yogesh Sharma"));
        spin = findViewById(R.id.spinner);
        startHotspot.setText("Take Attendance");
        progressBar = findViewById(R.id.progressBar2);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        if (!android.provider.Settings.System.canWrite(this)) {
            getWritePermissions();
        }


        checkConnections = new Thread() {
            public Boolean flag = false;

            @Override
            public void interrupt() {
                super.interrupt();
                flag = true;
            }

            @Override
            public void run() {
                while(!flag) {
                    try {
                        Thread.sleep(1000);
                        getConnected();
                    } catch (InterruptedException e) {
                        System.out.print(e.getStackTrace());
                        System.out.print(e.getMessage());
                        e.printStackTrace();
                    }

                }
            }
        };

        hideInfo();
    }

    public void hideInfo() {
        listOfStudents.setVisibility(View.GONE);
        TotalAttendance.setVisibility(View.GONE);
        TableRoll.setVisibility(View.GONE);
        TableName.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    public void showInfo() {
        listOfStudents.setVisibility(View.VISIBLE);
        TotalAttendance.setVisibility(View.VISIBLE);
        TableRoll.setVisibility(View.VISIBLE);
        TableName.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkConnections.interrupt();
    }
}
