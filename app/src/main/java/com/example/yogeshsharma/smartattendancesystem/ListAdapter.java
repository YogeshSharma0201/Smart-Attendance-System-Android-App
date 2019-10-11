package com.example.yogeshsharma.smartattendancesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListAdapter extends  RecyclerView.Adapter<ListAdapter.MyViewHolder>{
    ArrayList<Pair<String, String>> personNames;
    Context context;

    public ListAdapter(Context context, ArrayList<Pair<String, String>> personNames) {
        this.context = context;
        this.personNames = personNames;
        System.out.println("from list" + personNames);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.name.setText(personNames.get(i).second);
        myViewHolder.rollno.setText(personNames.get(i).first);
        System.out.println("l adapter" + personNames.get(i).first);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, personNames.get(i).second, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return personNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, rollno;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            rollno = itemView.findViewById(R.id.rollno);
        }
    }
}
