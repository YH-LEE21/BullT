package com.example.bullt.ListItems;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    //아이템 정보를 담은 리스트
    private ArrayList<ItemData> item;

    //파이어베이스데이터베이스, 파이어베이스 유저,auth,스토리지,실시간데이터베이스
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    DatabaseReference myRef;

    //레이아웃크기를 상황에 맞게 바꿔주기위한 변수
    int i;
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
        else if(i == 3){
            view = inflater.inflate(R.layout.listitem3,parent,false);
            layoutParams = view.getLayoutParams();
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


        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              최근에 본 아이템
                HashMap<String,Object> favorite = new HashMap<>();
                favorite.put("title",title);
                favorite.put("content",content);
                favorite.put("price",Integer.parseInt(price.substring(0,price.length()-1)));
                favorite.put("ref",ref);
                favorite.put("id",imageID);
                favorite.put("like",true);
                favorite.put("imagePath",ImagePath);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(ref));
                context.startActivity(intent);
                try{//firebaseUser.getUid가 없을수 있기때문에 예외처리를 해준다.
                    myRef.child("Lately").child(firebaseUser.getUid()).child(imageID).setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context,"찜목록에 추가되었습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context,"Lately",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){}
            }
        });




//      일단은 실행해서 메인페이지를 띄운다 유저에게 로그인을 강요하지 않기위해서
        try {
            if(item.get(position).getHearts().containsKey(firebaseAuth.getCurrentUser().getUid())){
                viewHolder.like.setBackgroundResource(R.drawable.checked_heart);

            }
            else{
                viewHolder.like.setBackgroundResource(R.drawable.unchecked_heart);
            }
        }catch (Exception e){}

        viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(viewHolder.scaleAnimation);

                try{//유저의 email이 없으면 catch문으로 가서 예외처리를 한다.
                    String email = firebaseUser.getEmail();
                    onStarClicked(database.getReference("ListItem").child(imageID));

                }catch (Exception e){
//                  로그인이 안되 있다면 AlertDialog를 사용해 로그인 할지 안할지 결정한다
                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    dlg.setTitle("BullT");
                    dlg.setMessage("로그인이 필요한 서비스 입니다.\n로그인 하시겠습니까??");
                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context,LoginActivity.class);
                            context.startActivity(intent);
                            Toast.makeText(context,"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dlg.show();
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

                    myRef.child("Favorite").child(firebaseUser.getUid()).child(p.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context,"찜목록에 제외 되었습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                            }
                        }
                    });
                } else {
                    // Star the post and add self to stars
                    p.setCount(p.getCount() + 1);
                    p.getHearts().put(firebaseUser.getUid(),true);
                    myRef.child("Favorite").child(firebaseUser.getUid()).child(p.getId()).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context,"찜목록에 추가 되었습니다.",Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(context,"Favorite",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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