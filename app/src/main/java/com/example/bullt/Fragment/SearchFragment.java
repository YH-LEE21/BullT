package com.example.bullt.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.ListItems.Data;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.ListItems.RecyclerAdapter2;
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SearchFragment extends Fragment {
    private View view;

    RecyclerView recyclerView3;
    private RecyclerAdapter2 adapter3;

    SearchView searchView;
    ArrayList<Data> list;
    public static SearchFragment newInstance() {

        return new SearchFragment();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_search_fragment, container, false);
        searchView = view.findViewById(R.id.SearchView1);

        list = new ArrayList<>();
        recyclerView3 = view.findViewById(R.id.SearchResult_recycelrView);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        FireBaseDataInit();
        return view;
    }

    private void FireBaseDataInit(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //파이어베이스 realtime 변수
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter3 = new RecyclerAdapter2(getContext(),list);
                recyclerView3.setAdapter(adapter3);
                myRef.child("ListItem").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(!task.isSuccessful()){
                            Log.e("aaa","실패");
                        }else{
                            //이전 검색 초기화
                            list.clear();
                            Map<String, Object> map = (Map<String, Object>) task.getResult().getValue();
                            for (String keys : map.keySet()) {
                                Map<String, String> child = (Map<String, String>) map.get(keys);

                                //상표명,내용,가격,주소,이미지 주소,데이터베이스 이미지 이름,count
                                String title = String.valueOf(child.get("title"));
                                String content = String.valueOf(child.get("content"));
                                String price =String.valueOf(child.get("price"))+"원";
                                String ref = String.valueOf(child.get("ref"));
                                String image_id = String.valueOf(child.get("id"));
                                int count = Integer.parseInt(String.valueOf(child.get("count")));
                                StorageReference storageRef = storage.getReference(child.get("ImagePath"));
//                              원하는 아이템찾기....
                                String[] titles = title.split(" ");
                                String[] contents = content.split(" ");
                                if(Arrays.asList(titles).contains(query)||Arrays.asList(contents).contains(query)){
                                    Data item = new Data(title,content,price,ref,storageRef.getPath(),image_id,false,count);
                                    Log.e("ccc",title);
                                    Log.e("ccc",content);
                                    list.add(item);
                                }
                                else{
                                    Log.e("ccc",query.contains(title)+"");
                                    Log.e("ccc",content);
                                }
                            }
                            Toast.makeText(getContext(), list.size()+"개 검색결과가 있습니다.", Toast.LENGTH_SHORT).show();
                            adapter3.notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }
}
