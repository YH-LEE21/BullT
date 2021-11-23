package com.example.bullt.HotItems;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Data> mData = null;

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
            scaleAnimation.setDuration(5000);
            bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);
        }
    }

    public RecyclerAdapter(Context context,ArrayList<Data> list){
        this.context = context;
        mData = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        View view = inflater.inflate(R.layout.listitem,parent,false);

        //크기 조절
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = 300;
        layoutParams.height = 550;
        view.setLayoutParams(layoutParams);
        RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String title = mData.get(position).getTitle();
        String context = mData.get(position).getContent();
        String price = mData.get(position).getPrice();
        String path = mData.get(position).getPath();
        boolean likes = mData.get(position).getLike();
        int resId = mData.get(position).getResId();

        //viewHolder.imageView.setImageResource(resId);
        viewHolder.title.setText(title);
        viewHolder.content.setText(context);
        viewHolder.price.setText(price);

        int count =0;
        //하트를 눌렀을 때
        viewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(viewHolder.scaleAnimation);
                if(isChecked){
                    viewHolder.like.setBackgroundResource(R.drawable.checked_heart);
                }
                else{
                    viewHolder.like.setBackgroundResource(R.drawable.unchecked_heart);
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
        return 3;
    }


}