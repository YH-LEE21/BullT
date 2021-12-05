package com.example.bullt.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullt.Data.ItemData;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteFragment extends Fragment{
    private View view;
    private ImageButton GridView3_ib,GridView2_ib,LinearV_ib;
    private RecyclerView favoriteRecyclerView;
    private ArrayList<ItemData> list_item;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_favorite_fragment, container, false);
        list_item = new ArrayList<>();
        getData();
        Setinit();

        return view;
    }

    private void Setinit(){

        favoriteRecyclerView = view.findViewById(R.id.favorite_RecyclerView);
        favoriteRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new RecyclerAdapter(getContext(),list_item,1);
        favoriteRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        GridView3_ib = view.findViewById(R.id.GridView3_ib);

        GridView3_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                adapter = new RecyclerAdapter(getContext(),list_item,1);
                favoriteRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        GridView2_ib = view.findViewById(R.id.GridView2_ib);
        GridView2_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                adapter = new RecyclerAdapter(getContext(),list_item,2);
                favoriteRecyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });
        LinearV_ib = view.findViewById(R.id.LinearV_ib);
        LinearV_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                adapter = new RecyclerAdapter(getContext(),list_item,3);
                favoriteRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //  리스트아이템 업데이트
    private void getData(){
        try{
            database.getReference().child("Favorite").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list_item.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                        ItemData select_item = ds.getValue(ItemData.class);
                        list_item.add(select_item);
                    }
                    adapter.notifyDataSetChanged();
                    //내림차순정렬
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { //에러가 날때 작동

                }
            });
        }catch (Exception e){
            Log.e("error",e.getMessage());
        }
    }
}