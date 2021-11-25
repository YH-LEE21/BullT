package com.example.bullt.HotItems;

import android.app.Activity;
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
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerAdapter2.ViewHolder>{
    private Context context;
    private ArrayList<Data> item;
    FirebaseStorage storage;


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

    public RecyclerAdapter2(Context context, ArrayList<Data> list){
        this.context = context;
        item = list;
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        View view = inflater.inflate(R.layout.listitem2,parent,false);

        //크기 조절
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = 500;
        layoutParams.height = 800;
        view.setLayoutParams(layoutParams);
        RecyclerAdapter2.ViewHolder vh = new RecyclerAdapter2.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String title = item.get(position).getTitle();
        String content = item.get(position).getContent();
        String price = item.get(position).getPrice();
        boolean likes = item.get(position).getLike();
        String resId = item.get(position).getResId();

        //viewHolder.imageView.setImageResource(resId);
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
                        .placeholder(R.drawable.round)
                            .into(viewHolder.imageView);
                }
            }
        });

        //Internet 이동동
       viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 누르면 그사진의 웹주소로 이동
            }
        });
        //장바구니 담기 버튼 나오게 하기
        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        int count =0;
        //하트를 눌렀을 때
        viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(viewHolder.scaleAnimation);
                if(isChecked){
                    viewHolder.like.setBackgroundResource(R.drawable.checked_heart);
                    Toast.makeText(context, "찜목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    viewHolder.like.setBackgroundResource(R.drawable.unchecked_heart);
                    Toast.makeText(context, "찜목록에 제외 되었습니다.", Toast.LENGTH_SHORT).show();
                }
//                onStarClicked(firebaseDatabase.getReference().child("Postdata").child(postIdList.get(holder.getLayoutPosition()))); //찾고자 하는 게시글 id로 접근
                // holder.getLayoutPosition() = 지금 화면에 떠있는 아이템의 번호(몇번째 아이템인지) //가장 위에 있는 게시글 부터 holder.getLayoutPosition() = 0
                /*지금 하트 누르면 sns 피드가 다시 재 갱신되서 애니메이션이 씹힌다. 그래서 갱신 방식을 새로고침을 원할때 새로고침 되는 방식으로 바꿔야 될것 같다. */
            }
        });
        //하트를 눌렀을 때
//                onStarClicked(firebaseDatabase.getReference().child("Postdata").child(postIdList.get(holder.getLayoutPosition()))); //찾고자 하는 게시글 id로 접근
                /*지금 하트 누르면 sns 피드가 다시 재 갱신되서 애니메이션이 씹힌다. 그래서 갱신 방식을 새로고침을 원할때 새로고침 되는 방식으로 바꿔야 될것 같다. */
        //연결하기
        //viewHolder.
    }

    @Override
    public int getItemCount() {
        return item.size();
    }


}