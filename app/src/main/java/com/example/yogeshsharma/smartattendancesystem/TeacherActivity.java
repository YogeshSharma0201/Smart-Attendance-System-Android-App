package com.example.yogeshsharma.smartattendancesystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

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
    ArrayList<String> classes;
    private Spinner spin;
    private int state;
    private ProgressBar progressBar;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    JSONObject user = new JSONObject();
    JSONArray classesTeaching;
    Integer selectedClass = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private MyOreoWifiManager myOreoWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        context = getApplicationContext();
        try {
            init(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStartHotspot(View view) {
        // TODO: Check sdk version and decide how to turn on hotspot

        if(mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(false);

        RequestParams rp = new RequestParams();
        try {
            JSONObject jsonObject = classesTeaching.getJSONObject(selectedClass);
            rp.add("id", jsonObject.getString("id"));

            if(state == 1) {
                rp.add("inSession", "false");
            } else {
                rp.add("inSession", "true");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Api.post("/api/classes/setSession", rp, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println(responseString +" "+ throwable.getCause());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println(responseString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startTakingAttendance();
                    }
                });
            }
        });
//
    }

    public void startTakingAttendance() {
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

    private void onClassSelect(int idx) {
        selectedClass = idx;

    }

    private void displayToast(String str) {
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, str, duration).show();
    }

    private void getWritePermissions() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        startActivity(intent);
    }

    private void init(Context context) throws JSONException {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String userString = sharedpreferences.getString("User", "{\"id\":1,\"firstName\":\"Yogesh\"}");
        user = new JSONObject(userString);
        user = user.getJSONObject("user");
        classesTeaching = user.getJSONArray("classesTeaching");
        classes = new ArrayList<>();
        for(int i=0; i<classesTeaching.length(); i++) {
            classes.add(classesTeaching.getJSONObject(i).getString("name"));
        }
//        System.out.println("classes" + classes);
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
                onClassSelect(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_item,classes);
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
