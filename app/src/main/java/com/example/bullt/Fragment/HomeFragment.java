package com.example.bullt.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import com.example.bullt.Banners.BannerData;
import com.example.bullt.Banners.BannerPagerAdapter;
import com.example.bullt.Data.ItemData;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.R;
import com.example.bullt.Cart.CartActivity;
import com.example.bullt.Search.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private View view;
    private FirebaseDatabase database;
    //배너 이미지를 보여주는 변수
    ViewPager2 v_pager;

    //배너 이미지 정보 담은 리스트
    static ArrayList<BannerData> Bannerlist;
    static ArrayList<ItemData> list_item;
    static ArrayList<ItemData> list_item2;
    private BannerPagerAdapter pagerAdapter;

    //자동 슬라이드 변수
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 0;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 5000; // time in milliseconds between successive task executions.


    //HotItem
    RecyclerView recyclerView1;
    private RecyclerAdapter adapter;
    //ListItem_view
    RecyclerView recyclerView2;
    private RecyclerAdapter adapter2;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//  현재 유저 정보
    FirebaseUser user = mAuth.getCurrentUser();

    //돋보기 이미지,장바구니 이미지
    ImageView search_iv,cart_iv;

    //예시
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        Bannerlist = new ArrayList<>();
        list_item = new ArrayList<>();
        list_item2 = new ArrayList<>();
//      여기 부분에는 firebase 스토리지와 연결 되어 있어야 함
        Setinit(view);
        FirebaseInit();
        getData();

        return view;
    }
    //아이디 연결하는 함수
    public void Setinit(View view){
        //배너 이미지
        v_pager = view.findViewById(R.id.image_slide);
        //돋보기 버튼
        search_iv = view.findViewById(R.id.search_iv);
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity()로 MainActivity의 replaceFragment를 불러온다
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        //장바구니 버튼
        cart_iv = view.findViewById(R.id.cart_iv);
        cart_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        //HotItem
        recyclerView1 = (RecyclerView) view.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        //list_item
        recyclerView2 =(RecyclerView)view.findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new GridLayoutManager(getContext(),2));


    }



    //Firebase
    public void FirebaseInit(){
        //파이어베이스 스토리지 변수
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //파이어베이스 realtime 변수
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        pagerAdapter = new BannerPagerAdapter(getContext(),Bannerlist);
        v_pager.setAdapter(pagerAdapter);

        myRef.child("banner").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(!task.isSuccessful()){
                        Log.e("aaa","실패");
                    }
                    else{
                        Log.d("FireBase", String.valueOf(task.getResult().getValue()));
                        Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                        for (String keys : map.keySet()) {
                            Map<String, String> child = (Map<String, String>) map.get(keys);
                            Log.d("image", String.valueOf(child.get("imagePath")));
                            Log.d("image", String.valueOf(child.get("ref")));
                            StorageReference storageRef = storage.getReference(child.get("ImagePath"));
    //                            Log.d("DownloadUrl", storageRef.getPath().toString());
                            BannerData item = new BannerData(storageRef.getPath(), child.get("ref"));
                            Bannerlist.add(item);
                            Log.e("Bannerlist",String.valueOf(Bannerlist.size()));
                        }
                        pagerAdapter.notifyDataSetChanged();
                    }
                }

            });
//                        Data Listitem = new Data();
        adapter = new RecyclerAdapter(getContext(),list_item,1);
        recyclerView1.setAdapter(adapter);

        adapter2 = new RecyclerAdapter(getContext(),list_item2,2);
        recyclerView2.setAdapter(adapter2);

    }

    // 이미지 슬라이더 구현 메서드
    public void autoSlide(){
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if(currentPage == 7) {
                    currentPage = 0;
                }
                Log.d("currentPage",String.valueOf(currentPage));
                v_pager.setCurrentItem(currentPage++, true);
            }
        };

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);

            }
        },DELAY_MS,PERIOD_MS);

    }

//오류 1개 다른페이지로 넘어가고 다시 돌아가면 그림 1개가 점프해버림...
    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        autoSlide();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

//  리스트아이템 업데이트
    private void getData(){

        database.getReference().child("ListItem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_item2.clear();
                list_item.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ItemData itemData = ds.getValue(ItemData.class);
                    list_item.add(itemData);
                    list_item2.add(itemData);
                }
                //내림차순정렬
                //hotitem

                Collections.sort(list_item,Collections.reverseOrder());
                ArrayList<ItemData> list_hot = new ArrayList<>();
                for(int i =0;i<3;i++){
                    list_hot.add(list_item.get(i));
                }
                list_item.clear();
                for(ItemData d : list_hot){
                    list_item.add(d);
                }
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //에러가 날때 작동

            }
        });
    }


}
