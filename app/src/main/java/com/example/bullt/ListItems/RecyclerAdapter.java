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
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ItemData> item;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    String userEmail;
    FirebaseDatabase database;
    DatabaseReference myRef;
    int i;
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

    public RecyclerAdapter(Context context, ArrayList<ItemData> list,int i){
        this.context = context;
        item = list;
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        this.i = i;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = null;
        ViewGroup.LayoutParams layoutParams=null;
        //크기 조절
        if(i == 1){
            view = inflater.inflate(R.layout.listitem,parent,false);

            //크기 조절
            layoutParams = view.getLayoutParams();
            layoutParams.width = 305;
            layoutParams.height = 550;

        }
        else if(i == 2){
            view = inflater.inflate(R.layout.listitem2,parent,false);
            //크기 조절
            layoutParams = view.getLayoutParams();
            layoutParams.width = 500;
            layoutParams.height = 800;
            view.setLayoutParams(layoutParams);

        }
        view.setLayoutParams(layoutParams);

        RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {

        ItemData dataInstance = item.get(viewHolder.getLayoutPosition());

        String title = dataInstance.getTitle();
        String content = dataInstance.getContent();
        String price = dataInstance.getPrice()+"원";
        int count = dataInstance.getCount();
        String imageID = dataInstance.getId();
        String ImagePath = dataInstance.getImagePath();
        String ref = dataInstance.getRef();
        String search = dataInstance.getSearch();
        viewHolder.title.setText(title);
        viewHolder.content.setText(content);
        viewHolder.price.setText(price);
        StorageReference sref = FirebaseStorage.getInstance().getReference(item.get(position).getImagePath());
        Log.d("레퍼런스", String.valueOf(ref));
        sref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
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
//      이미지뷰에 아이템 웹 주소저장
        viewHolder.imageView.setTag(item.get(position).getRef());

        // TODO: 2021-12-02
//      일단은 실행해서 메인페이지를 띄운다 유저에게 로그인을 강요하지 않기위해서
        try {
            if(item.get(position).getHearts().containsKey(firebaseAuth.getCurrentUser().getUid())){
                viewHolder.like.setBackgroundResource(R.drawable.checked_heart);
                HashMap<String,Object> favorite = new HashMap<>();
                favorite.put("title",title);
                favorite.put("content",content);
                favorite.put("price",Integer.parseInt(price.substring(0,price.length()-1)));
                favorite.put("ref",ref);
                favorite.put("id",imageID);
                favorite.put("like",true);
                favorite.put("imagePath",ImagePath);
                myRef.child("Favorite").child(firebaseUser.getUid()).child(imageID).setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                        }else{
                            Toast.makeText(context,"Favorite",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                viewHolder.like.setBackgroundResource(R.drawable.unchecked_heart);
                try{
                    myRef.child("Favorite").child(firebaseUser.getUid()).child(imageID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            }else{
                                Toast.makeText(context,"Favorite",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){}
            }
        }catch (Exception e){}

        viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(viewHolder.scaleAnimation);
                try{
                    String email = firebaseUser.getEmail();
                    onStarClicked(firebaseDatabase.getReference("ListItem").child(imageID));
//                  최근본 내용 저장 기능
                    if(isChecked){
                         HashMap<String,Object> favorite = new HashMap<>();
                            favorite.put("title",title);
                            favorite.put("content",content);
                            favorite.put("price",Integer.parseInt(price.substring(0,price.length()-1)));
                            favorite.put("ref",ref);
                            favorite.put("id",imageID);
                            favorite.put("like",true);
                            favorite.put("imagePath",ImagePath);
                            myRef.child("Lately").child(firebaseUser.getUid()).child(imageID).setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                    }else{
                                        Toast.makeText(context,"Lately",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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


    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ItemData p = mutableData.getValue(ItemData.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.getHearts().containsKey(firebaseUser.getUid())) {
                    // Unstar the post and remove self from stars
                    p.setCount(p.getCount()-1);
                    p.getHearts().remove(firebaseUser.getUid());
                } else {
                    // Star the post and add self to stars
                    p.setCount(p.getCount() + 1);
                    p.getHearts().put(firebaseUser.getUid(),true);
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
            }
        });
    }

}