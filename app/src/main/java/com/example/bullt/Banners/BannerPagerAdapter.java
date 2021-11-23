package com.example.bullt.Banners;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.bullt.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class BannerPagerAdapter extends PagerAdapter {

    private Context context = null;
    ImageView banner_iv;
    //이미지 주소와 url가 담긴 map
    private ArrayList<BannerData> list;
    // Context 를 전달받아 context 에 저장하는 생성자 추가.
    public BannerPagerAdapter(Context context,ArrayList<BannerData> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // position 값을 받아 주어진 위치에 페이지를 생성한다
        View view = null;

        //position값 0부터 시작
        if(context != null) {
            // LayoutInflater 를 통해 "/res/layout/page.xml" 을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.page, container, false);
            banner_iv = view.findViewById(R.id.banner_iv);

//            Uri uri = Uri.parse(list.get(position).getRef());
            //이미지 주소 저장
//            String ImagePath = list.get(position).getImagePath();
//            banner_iv.setImageURI(uri);
            //이미지 웹 주소 저장
//            String url = list.get(position).getRef();
            banner_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e("ImagePath",ImagePath);
//                    Log.e("url",url);
                }
            });
        }
        // 뷰페이저에 추가
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        //사용 가능한 뷰의 개수를 return 6개
        return 6;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //position 값을 받아 주어진 위치의 페이지를 삭제한다
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
