package com.example.bullt.Search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.R;

import java.util.ArrayList;

public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerSearchAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;

    public static final String PREFERENCES_NAME = "rebuild_preference";
    private OnItemClick1 mCallback;
    public RecyclerSearchAdapter(Context context, ArrayList<String> list, OnItemClick1 listener){
        this.context = context;
        this.list = list;
        mCallback = listener;
        Log.d("list_size",String.valueOf(list.size()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt;
        Button remove_btn;

        ViewHolder(View itemView){
            super(itemView);
            txt = itemView.findViewById(R.id.seartchTxt);
            remove_btn = itemView.findViewById(R.id.rm_searchTxt_btn);


            remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(txt.getText());
                    notifyDataSetChanged();
                }
            });
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.search_item,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        view.setLayoutParams(layoutParams);
        RecyclerSearchAdapter.ViewHolder vh = new RecyclerSearchAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt.setText(list.get(position));
        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClick(holder.txt.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static void setString(Context context, String key, String value) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);

        editor.apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
