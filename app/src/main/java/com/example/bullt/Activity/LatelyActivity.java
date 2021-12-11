package com.example.bullt.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.example.bullt.Data.ItemData;
import com.example.bullt.Fragment.MyFragment;
import com.example.bullt.ListItems.RecyclerAdapter;
import com.example.bullt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


//최근 본 페이지

public class LatelyActivity extends AppCompatActivity {
    private ImageButton back_btn,GridView3_ib1,GridView2_ib1,LinearV_ib1;
    private RecyclerView lately_RecyclerView;
    private ArrayList<ItemData> list_item;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lately);
        list_item = new ArrayList<>();
        ToggleButton bb = findViewById(R.id.favorite_btn);

        getData();
        Setinit();

    }

    private void Setinit(){

        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LatelyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        lately_RecyclerView = findViewById(R.id.Lately_RecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
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
        lately_RecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerAdapter(getApplicationContext(),list_item,1);
        lately_RecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        GridView3_ib1 = findViewById(R.id.GridView3_ib1);

        GridView3_ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
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
                lately_RecyclerView.setLayoutManager(gridLayoutManager);
                adapter = new RecyclerAdapter(getApplicationContext(),list_item,1);
                lately_RecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        GridView2_ib1 = findViewById(R.id.GridView2_ib1);
        GridView2_ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int gridPosition = position % 2;
                        switch (gridPosition) {
                            case 0:
                            case 1:
                                return 1;
                        }
                        return 0;
                    }
                });
                lately_RecyclerView.setLayoutManager(gridLayoutManager);
                adapter = new RecyclerAdapter(getApplicationContext(),list_item,2);
                lately_RecyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });
        LinearV_ib1 = findViewById(R.id.LinearV_ib1);
        LinearV_ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lately_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                adapter = new RecyclerAdapter(getApplicationContext(),list_item,3);
                lately_RecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //  리스트아이템 업데이트
    private void getData(){
        try{
            database.getReference().child("Lately").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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