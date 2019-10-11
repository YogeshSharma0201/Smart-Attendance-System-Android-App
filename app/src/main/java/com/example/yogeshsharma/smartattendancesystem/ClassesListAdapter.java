package com.example.yogeshsharma.smartattendancesystem;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassesListAdapter extends  RecyclerView.Adapter<ClassesListAdapter.MyViewHolder>{
    ArrayList<Pair<String, Pair<String, String>>> classesNames;
    Context context;
    WifiManager mWifiManager;
    HashMap<String, Pair<String, String>> dataBase;

    public ClassesListAdapter(Context context,
                              ArrayList<Pair<String, Pair<String, String>>> classesNames,
                              WifiManager mWifiManager,
                              HashMap<String, Pair<String, String>> dataBase) {
        this.context = context;
        this.classesNames = classesNames;
        this.mWifiManager = mWifiManager;
        this.dataBase = dataBase;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_classes_layout, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.className.setText(classesNames.get(i).first);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mWifiManager.enableNetwork();
                String networkSSID = classesNames.get(i).second.first;
                String networkBSSID = classesNames.get(i).second.second;
                String networkPass = "87654321";

                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", networkSSID);
                wifiConfig.BSSID = networkBSSID;
                wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

                int netId = mWifiManager.addNetwork(wifiConfig);
                System.out.println("netId" + netId);
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(netId, true);
                mWifiManager.reconnect();

                Toast.makeText(context, classesNames.get(i).first, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return classesNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView className;

        public MyViewHolder(View itemView) {
            super(itemView);

            className = itemView.findViewById(R.id.className);
        }
    }
}
