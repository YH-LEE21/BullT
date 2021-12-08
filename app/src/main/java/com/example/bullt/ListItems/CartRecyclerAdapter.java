package com.example.bullt.ListItems;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.bullt.Data.CartData;
import com.example.bullt.Data.ItemData;
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHoler> {
    private Context context;
    //아이템 정보를 담은 리스트
    private ArrayList<CartData> item;
    //파이어베이스데이터베이스, 파이어베이스 유저,auth,스토리지,실시간데이터베이스
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    DatabaseReference myRef;

    DecimalFormat formatter = new DecimalFormat("###,###");
    public class ViewHoler extends RecyclerView.ViewHolder {
//      버튼 선언
        TextView tv_title,tv_content,tv_sizeCount,tv_price1;
        CheckBox checkbox;
        ImageView iv_cart,iv_remove;
        Button buy_btn;
        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            //버튼 연결
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_sizeCount = itemView.findViewById(R.id.tv_size);
            tv_price1 = itemView.findViewById(R.id.tv_price1);
            checkbox = itemView.findViewById(R.id.checkbox);
            iv_cart = itemView.findViewById(R.id.iv_cart);
            iv_remove = itemView.findViewById(R.id.iv_remove);
            buy_btn = itemView.findViewById(R.id.buy_btn);
        }
    }

    public CartRecyclerAdapter(Context context,ArrayList<CartData> item){
        this.context = context;
        this.item = item;
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cartitem,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        view.setLayoutParams(layoutParams);
        CartRecyclerAdapter.ViewHoler vh = new CartRecyclerAdapter.ViewHoler(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        CartData dataInstance = item.get(holder.getLayoutPosition());
        String title = dataInstance.getTitle();
        String content = dataInstance.getContent();
        String imagePath = dataInstance.getImagePath();
        String sizeCount =dataInstance.getSize()+"  "+dataInstance.getCount();
        String total = formatter.format(dataInstance.getTotalprice())+"원";

        Log.e("imagePath",imagePath.toString());
        StorageReference sref = FirebaseStorage.getInstance().getReference(dataInstance.getImagePath());
        sref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(!task.isSuccessful()){
                    Log.d("레퍼런스a", String.valueOf(sref));
                }
                else {
                    Glide.with(context)
                            .load(task.getResult())
                            .fitCenter()
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                            .placeholder(R.drawable.round)
                            .into(holder.iv_cart);
                }
            }
        });
        holder.tv_title.setText(title);
        holder.tv_content.setText(content);
        holder.tv_sizeCount.setText(sizeCount);
        holder.tv_price1.setText(total);
    }

    @Override
    public int getItemCount() {
        return item.size();
    }
}