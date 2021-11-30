package com.example.bullt.ListItems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.bullt.Data.ItemData;
import com.example.bullt.R;
import com.example.bullt.Recycler.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ItemData> item;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    String userEmail;
    FirebaseDatabase database;
    DatabaseReference myRef;
    boolean tf = false;
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ToggleButton like;
        TextView title,content,price;

        public ScaleAnimation scaleAnimation;
        ViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_main);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            price = itemView.findViewById(R.id.tv_price);
            like = itemView.findViewById(R.id.favorite_btn);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(imageView.getTag().toString()));
                    context.startActivity(intent);
                }
            });

            //하트를 누를때 애니메이션
            BounceInterpolator bounceInterpolator;//애니메이션이 일어나는 동안의 회수, 속도를 조절하거나 시작과 종료시의 효과를 추가 할 수 있다
            scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(2000);
            bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);

        }
    }

    public RecyclerAdapter(Context context, ArrayList<ItemData> list){
        this.context = context;
        item = list;
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.listitem,parent,false);

        //크기 조절
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = 305;
        layoutParams.height = 550;
        view.setLayoutParams(layoutParams);
        RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        String title = item.get(position).getTitle();
        String content = item.get(position).getContent();
        String price = item.get(position).getPrice();
        int count = item.get(position).getCount();
        String imageID = item.get(position).getImageId();
        String path = item.get(position).getPath();
        String resid = item.get(position).getResId();
        String search = item.get(position).getSearch();
        viewHolder.title.setText(title);
        viewHolder.content.setText(content);
        viewHolder.price.setText(price);
        StorageReference ref = FirebaseStorage.getInstance().getReference(item.get(position).getPath());
        Log.d("레퍼런스", String.valueOf(ref));
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(!task.isSuccessful()){
                    Log.d("레퍼런스a", String.valueOf(ref));
                }
                else {
                    Glide.with(context)
                            .load(task.getResult())
                            .fitCenter()
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                            .placeholder(R.drawable.round)
                            .into(viewHolder.imageView);
                }
            }
        });

        //      imageID저장
        viewHolder.title.setTag(imageID);
        try {
            myRef.child("Favorite").child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                        try{
                            for (String keys : map.keySet()) {
                                Map<String, String> child = (Map<String, String>) map.get(keys);
                                if(child.get("id").equals(imageID)){
                                    viewHolder.like.setChecked(true);
                                    viewHolder.like.setBackgroundResource(R.drawable.checked_heart);
                                }

                            }
                        }catch (Exception e){
                            Log.e("for error",e.getMessage());
                        }
                    }
                }
            });
        }catch (Exception e){
            Log.e("ERROR",e.getMessage());
        }
//      이미지뷰에 아이템 웹 주소저장
        viewHolder.imageView.setTag(item.get(position).getResId());
//      like에 좋아요 수 저장
        viewHolder.like.setTag(count);
        viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(viewHolder.scaleAnimation);
                try{
                    String email = firebaseUser.getEmail();

                    if(isChecked){
                        //로그인이 되있다면
                        if(email!=null){
                            viewHolder.like.setBackgroundResource(R.drawable.checked_heart);
                            Toast.makeText(context, "찜목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                            int cnt = item.get(position).getCount();
                            if(tf) {
                                cnt++;
                                tf = false;
                            }
                                item.get(position).setCount(cnt);
                            Log.d("cnt",String.valueOf(cnt));
                            //cnt++
                            HashMap<String,Object> save = new HashMap<>();
                            save.put("title",title);
                            save.put("content",content);
                            save.put("price",Integer.parseInt(price.substring(0,price.length()-1)));
                            save.put("ref",resid);
                            save.put("id",imageID);
                            save.put("count",cnt);
                            save.put("ImagePath",path);
                            save.put("search",search);
                            myRef.child("ListItem").child(imageID).setValue(save);
//                          좋아요한 그림 추가
                            HashMap<String,Object> favorite = new HashMap<>();
                            favorite.put("title",title);
                            favorite.put("content",content);
                            favorite.put("price",Integer.parseInt(price.substring(0,price.length()-1)));
                            favorite.put("ref",resid);
                            favorite.put("id",imageID);
                            favorite.put("like",true);
                            favorite.put("ImagePath",path);
                            myRef.child("Favorite").child(firebaseUser.getUid()).child(imageID).setValue(favorite);
                        }
                    }
                    else{
                        //로그인이 되있다면
                        if(email!=null){
                            viewHolder.like.setBackgroundResource(R.drawable.unchecked_heart);
                            int cnt = item.get(position).getCount();
                            if(!tf){
                                cnt--;
                                tf=true;
                            }
                            item.get(position).setCount(cnt);
                            Log.d("cnt",String.valueOf(cnt));
                            HashMap<String,Object> save = new HashMap<>();
                            save.put("title",title);
                            save.put("content",content);
                            save.put("price",Integer.parseInt(price.substring(0,price.length()-1)));
                            save.put("ref",item.get(position).getResId());
                            save.put("id",imageID);
                            save.put("count",cnt);
                            save.put("ImagePath",item.get(position).getPath());
                            save.put("search",item.get(position).getSearch());
                            myRef.child("ListItem").child(imageID).setValue(save);
                            myRef.child("Favorite").child(firebaseUser.getUid()).child(imageID).removeValue();
                        }
                    }
                }catch (Exception e){
//                  로그인이 안되 있다면
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(context,"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return item.size();
    }


}