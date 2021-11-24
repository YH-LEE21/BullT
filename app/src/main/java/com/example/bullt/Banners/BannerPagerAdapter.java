package com.example.bullt.Banners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.ViewHolder> {
    Context context;
    ArrayList<BannerData> items;
    FirebaseStorage storage;
    public BannerPagerAdapter(Context context, ArrayList<BannerData> items){
        this.context = context;
        this.items = items;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public BannerPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerPagerAdapter.ViewHolder holder, int position) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(items.get(position).getImagePath());
        Log.d("레퍼런스", String.valueOf(ref));
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context)
                        .load(task.getResult())
                        //.placeholder(R.drawable.ic_loading)
                        .into(holder.iv_ads);
            }
        });
        holder.iv_ads.setTag(items.get(position).getRef());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_ads;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_ads = itemView.findViewById(R.id.banner_iv);
            iv_ads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(context, WebViewActivity.class);
//                    intent.putExtra("url", iv_ads.getTag().toString());
//                    context.startActivity(intent);
                }
            });
        }
    }
}