package com.example.bullt.ListItems;

import android.content.Context;
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
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Data> mData = null;
    FirebaseStorage storage;
    String userEmail;
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

            //하트를 누를때 애니메이션
            BounceInterpolator bounceInterpolator;//애니메이션이 일어나는 동안의 회수, 속도를 조절하거나 시작과 종료시의 효과를 추가 할 수 있다
            scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(2000);
            bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);
        }
    }

    public RecyclerAdapter(Context context,ArrayList<Data> list){
        this.context = context;
        mData = list;
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String title = mData.get(position).getTitle();
        String content = mData.get(position).getContent();
        String price = mData.get(position).getPrice();
        String path = mData.get(position).getPath();
        boolean likes = mData.get(position).getLike();
        StorageReference ref = FirebaseStorage.getInstance().getReference(mData.get(position).getPath());

        //viewHolder.imageView.setImageResource(resId);
        viewHolder.title.setText(title);
        viewHolder.content.setText(content);
        viewHolder.price.setText(price);

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
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                            .placeholder(R.drawable.round)
                            .into(viewHolder.imageView);
                }
            }
        });
        int count =0;
        //하트를 눌렀을 때
        viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(viewHolder.scaleAnimation);

                if(isChecked){
                    //로그인이 되있다면
                    if(userEmail!=null){
                        viewHolder.like.setBackgroundResource(R.drawable.checked_heart);
                        Toast.makeText(context, "찜목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    //로그인이 안되있다면
                    else{
                        //로그인 페이지로 넘어감
//                        Intent intent = new Intent(context.getApplicationContext(), LogInActivity.class);
//                        context.startActivity(intent);
                        Toast.makeText(context,"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //로그인이 되있다면
                    if(userEmail!=null){
                        viewHolder.like.setBackgroundResource(R.drawable.unchecked_heart);
                        Toast.makeText(context, "찜목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    //로그인이 안되있다면
                    else{
                        //로그인 페이지로 넘어감
//                      Intent intent = new Intent(context.getApplicationContext(), LogInActivity.class);
//                      context.startActivity(intent);
                        Toast.makeText(context, "로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return 3;
    }


}