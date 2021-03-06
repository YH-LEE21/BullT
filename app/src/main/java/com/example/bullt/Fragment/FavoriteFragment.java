package com.example.bullt.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.bullt.Data.ItemData;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.R;
import com.example.bullt.Cart.CartActivity;
import com.example.bullt.Search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment{
    private View view;
    private ImageView cart_iv,search_iv;
//  작은바둑판,큰바둑판,수평정렬
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int gridPosition = position % 3;
                switch (gridPosition) {
                    case 0:
                    case 1:
                    case 2:
                        return 1;
                }
                return 0;
            }
        });
        favoriteRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerAdapter(getContext(),list_item,1);
        favoriteRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        GridView3_ib = view.findViewById(R.id.GridView3_ib);

        GridView3_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int gridPosition = position % 3;
                        switch (gridPosition) {
                            case 0:
                            case 1:
                            case 2:
                                return 1;
                        }
                        return 0;
                    }
                });

                favoriteRecyclerView.setLayoutManager(gridLayoutManager);

                adapter = new RecyclerAdapter(getContext(),list_item,1);
                favoriteRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        GridView2_ib = view.findViewById(R.id.GridView2_ib);
        GridView2_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        Log.e("GETsPANsIZE",String.valueOf(position));
                        int gridPosition = position % 2;
                        switch (gridPosition) {
                            case 0:
                            case 1:
                                return 1;
                        }
                        return 0;
                    }
                });
                favoriteRecyclerView.setLayoutManager(gridLayoutManager);
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

        cart_iv = view.findViewById(R.id.cart_iv);
        cart_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        search_iv = view.findViewById(R.id.search_iv);
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
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