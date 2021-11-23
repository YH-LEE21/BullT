package com.example.bullt.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.HotItems.Data;
import com.example.bullt.HotItems.RecyclerAdapter;
import com.example.bullt.HotItems.RecyclerAdapter2;
import com.example.bullt.HotItems.RecyclerViewDecoration;
import com.example.bullt.R;
import com.example.bullt.Recycler.MainActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View view;

    //배너 이미지를 보여주는 변수
    ViewFlipper v_fllipper;
    //돋보기 이미지,장바구니 이미지
    ImageView search_iv,cart_iv;
    //파이어베이스 스토리지 변수
    FirebaseStorage storage = FirebaseStorage.getInstance();

    //파이어 베이스 스토리지 변수 주소나옴
    StorageReference storageRef = storage.getReference();
    StorageReference mountainImagesRef = storageRef.child("Banner");
    //예시
    int[] images = {
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
    };
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_fragment, container, false);

//      여기 부분에는 firebase 스토리지와 연결 되어 있어야 함
        Log.e("storageRef",mountainImagesRef.toString());
        Setinit();
        return view;
    }
    //아이디 연결하는 함수
    public void Setinit(){
        v_fllipper = view.findViewById(R.id.image_slide);
        for(int image:images){
            fllipperImages(image);
        }
        search_iv = view.findViewById(R.id.search_iv);
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity()로 MainActivity의 replaceFragment를 불러온다
                ((MainActivity)getActivity()).replaceFragment(SearchFragment.newInstance());
            }
        });
        cart_iv = view.findViewById(R.id.cart_iv);
        RecyclerView recyclerView1 = (RecyclerView) view.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

        //test
        ArrayList<Data> list = new ArrayList<>();
        Data d = new Data("회사명","상품명","27000",1,"",true);
        list.add(d);
        list.add(d);
        list.add(d);
        RecyclerAdapter adapter = new RecyclerAdapter(getContext(), list);
        recyclerView1.setAdapter(adapter);
        recyclerView1.addItemDecoration(new RecyclerViewDecoration(30));


        RecyclerView recyclerView2 =(RecyclerView)view.findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new GridLayoutManager(getContext(),2));

        ArrayList<Data> list2 = new ArrayList<>();
        Data d1 = new Data("회사명","상품명","27000",1,"",true);
        list2.add(d1);
        list2.add(d1);
        list2.add(d1);
        list2.add(d1);
        list2.add(d1);
        RecyclerAdapter2 adapter2 = new RecyclerAdapter2(getContext(), list2);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.addItemDecoration(new RecyclerViewDecoration(20));
    }

    // 이미지 슬라이더 구현 메서드
    public void fllipperImages(int image) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(image);

        v_fllipper.addView(imageView);      // 이미지 추가
        v_fllipper.setFlipInterval(4000);       // 자동 이미지 슬라이드 딜레이시간(1000 당 1초)
        v_fllipper.setAutoStart(true);          // 자동 시작 유무 설정

        // animation
        v_fllipper.setInAnimation(getContext(),android.R.anim.slide_in_left);
        v_fllipper.setOutAnimation(getContext(),android.R.anim.slide_out_right);
    }
}
