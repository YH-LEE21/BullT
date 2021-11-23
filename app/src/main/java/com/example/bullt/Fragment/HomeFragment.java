package com.example.bullt.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.bullt.Banners.BannerData;
import com.example.bullt.Banners.BannerPagerAdapter;
import com.example.bullt.HotItems.Data;
import com.example.bullt.HotItems.RecyclerAdapter;
import com.example.bullt.HotItems.RecyclerAdapter2;
import com.example.bullt.HotItems.RecyclerViewDecoration;
import com.example.bullt.R;
import com.example.bullt.Recycler.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private View view;

    //배너 이미지를 보여주는 변수
    ViewPager v_pager;

    //배너 이미지 정보 담은 리스트
    static ArrayList<BannerData> Bannerlist;

    private BannerPagerAdapter pagerAdapter;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 2000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 2000; // time in milliseconds between successive task executions.

    //돋보기 이미지,장바구니 이미지
    ImageView search_iv,cart_iv;

    //예시
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        Bannerlist = new ArrayList<>();
//      여기 부분에는 firebase 스토리지와 연결 되어 있어야 함
        Setinit();
        FirebaseInit();
        autoSlide();
        return view;
    }
    //아이디 연결하는 함수
    public void Setinit(){
        //배너 이미지
        v_pager = view.findViewById(R.id.image_slide);
        //돋보기 버튼
        search_iv = view.findViewById(R.id.search_iv);
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity()로 MainActivity의 replaceFragment를 불러온다
                ((MainActivity)getActivity()).replaceFragment(SearchFragment.newInstance());
            }
        });
        //장바구니 버튼
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



    //Firebase
    public void FirebaseInit(){
        //파이어베이스 스토리지 변수
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //파이어베이스 realtime 변수
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

            myRef.child("banner").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "로딩 오류", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("FireBase", String.valueOf(task.getResult().getValue()));
                        Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                        for (String keys : map.keySet()) {
                            Map<String, String> child = (Map<String, String>) map.get(keys);
//                            Log.d("image", String.valueOf(child.get("ImagePath")));
//                            Log.d("image", String.valueOf(child.get("ref")));
                            StorageReference storageRef = storage.getReference(child.get("ImagePath"));
//                            Log.d("DownloadUrl", storageRef.getPath().toString());
                            BannerData item = new BannerData(storageRef.getPath(), child.get("ref"));
                            Bannerlist.add(item);
                            Log.e("Bannerlist",String.valueOf(Bannerlist.size()));
                        }
                    }
                }
            });


        //파이어 베이스 스토리지 변수 주소나옴
//        StorageReference storageRef = storage.getReference();
//        StorageReference mountainImagesRef = storageRef.child("Banner");
        }

    // 이미지 슬라이더 구현 메서드
    public void autoSlide(){
        pagerAdapter = new BannerPagerAdapter(getContext(),Bannerlist);
        v_pager.setAdapter(pagerAdapter);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if(currentPage == 6) {
                    currentPage = 0;
                }
                v_pager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

    }
}
