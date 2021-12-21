package com.example.bullt.Cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullt.R;
import com.example.bullt.Activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

//장바구니
public class CartActivity extends AppCompatActivity implements OnItemClick {

    private ImageView cancel_btn;
    private Button allBuy_btn;
    private TextView cart_total_price,cart_total;
    private RecyclerView cartRecyclerView;
    private ArrayList<CartData> cart_item;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private CartRecyclerAdapter adapter;

    DecimalFormat formatter = new DecimalFormat("###,###");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cart_item = new ArrayList<>();
        getData();
        Setinit();
    }

    private void Setinit(){
        //나가기 버튼
        cancel_btn = findViewById(R.id.cancel_btn1);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //모두 구매
        allBuy_btn = findViewById(R.id.allBuy_btn);
        allBuy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),cart_total_price.getText().toString()+"이 결제 되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        //총구매 가격
        cart_total_price = findViewById(R.id.cart_total_price);
        cart_total = findViewById(R.id.cart_total);
        //카트 리사이클뷰
        cartRecyclerView = findViewById(R.id.cart_recyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        adapter = new CartRecyclerAdapter(getApplicationContext(),cart_item,this);
        cartRecyclerView.setAdapter(adapter);

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

    //아이템을 클릭하면 이벤트 발생
    @Override
    public void onClick(int value) {
        cart_total.setText(formatter.format(value)+" 원");
        cart_total_price.setText(formatter.format(value)+" 원");
    }
}