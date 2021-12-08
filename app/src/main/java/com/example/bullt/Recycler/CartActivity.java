package com.example.bullt.Recycler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.bullt.Data.CartData;
import com.example.bullt.Data.ItemData;
import com.example.bullt.ListItems.CartRecyclerAdapter;
import com.example.bullt.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;

public class CartActivity extends AppCompatActivity {


    private RecyclerView cartRecyclerView;
    private ArrayList<CartData> cart_item;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private CartRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cart_item = new ArrayList<>();
        getData();
        Setinit();
    }

    private void Setinit(){
        cartRecyclerView = findViewById(R.id.cart_recyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        adapter = new CartRecyclerAdapter(getApplicationContext(),cart_item);
        cartRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getData(){
        try{
            database.getReference().child("Cart").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cart_item.clear();
                    for(DataSnapshot ds: snapshot.getChildren()){
                        CartData select_item = ds.getValue(CartData.class);
                        Log.e("sss","존재함");
                        cart_item.add(select_item);
                        Log.e("cart_item",cart_item.toString());
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