package com.example.bullt.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bullt.Cart.CartActivity;
import com.example.bullt.Cart.CartData;
import com.example.bullt.Login_Register.LoginActivity;
import com.example.bullt.R;
import com.example.bullt.Search.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.HashMap;


//아이템 클릭시 보이는 거

public class ClickShowActivity extends AppCompatActivity {
    ImageView imageView,cart_iv,search_iv;
    TextView contentTop_tv,content_tv,price_tv,title_tv, total_price;
    ImageButton countUp_ib,countDown_ib;
    EditText count_et;
    RadioButton r1,r2,r3,r4,r5;
    CartData cartData;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    FirebaseDatabase database;
    DatabaseReference myRef;

    //  현재 유저 정보
    int price;
    DecimalFormat formatter = new DecimalFormat("###,###");
    Button website_btn,cart_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_show);
        cartData = new CartData();
        cartData.setImagePath(getIntent().getStringExtra("imagePath"));
        cartData.setContent(getIntent().getStringExtra("content"));
        cartData.setPrice(getIntent().getIntExtra("price",1));
        cartData.setTitle(getIntent().getStringExtra("title"));
        cartData.setTotalprice(cartData.getPrice()*cartData.getCount());

        total_price = findViewById(R.id.total_price);
        total_price.setText(formatter.format(cartData.getPrice()*cartData.getCount())+"원");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        setInit();

    }

    private void setInit(){

        cart_iv = findViewById(R.id.cart_iv);
        cart_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClickShowActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        search_iv = findViewById(R.id.search_iv);
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClickShowActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        imageView = findViewById(R.id.imageView);
        refFirebase();


        contentTop_tv = findViewById(R.id.contentTop_tv);
        contentTop_tv.setText(getIntent().getStringExtra("content"));

        content_tv = findViewById(R.id.content_tv);
        content_tv.setText(getIntent().getStringExtra("content"));



        price_tv = findViewById(R.id.price_tv);


        price_tv.setText(formatter.format(getIntent().getIntExtra("price",1))+"원");


        title_tv = findViewById(R.id.title_tv);

        title_tv.setText(getIntent().getStringExtra("title"));
        r1 = findViewById(R.id.radioButton);
        r2 = findViewById(R.id.radioButton2);
        r3 = findViewById(R.id.radioButton3);
        r4 = findViewById(R.id.radioButton4);
        r5 = findViewById(R.id.radioButton5);


        countUp_ib = findViewById(R.id.CountUp_ib);

        countUp_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(count_et.getText().toString())+1;
                if(i>100){
                    i = 100;
                    Toast.makeText(getApplicationContext(), "최대 100개까지 주문 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                count_et.setText(String.valueOf(i));
                total_price.setText(formatter.format(cartData.getPrice()*i)+"원");
                cartData.setTotalprice(cartData.getPrice()*i);
            }
        });

        countDown_ib = findViewById(R.id.CountDown_ib);
        countDown_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(count_et.getText().toString())-1;
                if(i<1){
                    i=1;
                    Toast.makeText(getApplicationContext(), "1개 이상은 구매하셔야 합니다.", Toast.LENGTH_SHORT).show();
                }
                count_et.setText(String.valueOf(i));
                total_price.setText(formatter.format(cartData.getPrice()*i)+"원");
                cartData.setTotalprice(cartData.getPrice()*i);
            }
        });
        count_et = findViewById(R.id.count_et);
        //cartData
        website_btn = findViewById(R.id.button);
        website_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ref = getIntent().getStringExtra("ref");
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(ref));
                startActivity(intent);
            }
        });

        cart_btn = findViewById(R.id.button2);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // TODO: 2021-12-08 데이터베이스 cart - uid - cartData.getID - cartDat로 넣어주면 됨 
            public void onClick(View v) {
                //여기서 trycatch를 사용하여 로그인한 유저만 장바구니 사용가능

                try {
                    String email = user.getEmail();
                    cartData.setSize(radioCheck());
                    cartData.setId(getIntent().getStringExtra("id") + cartData.getSize());
                    cartData.setCount(Integer.parseInt(count_et.getText().toString()));
                    myRef.child("Cart").child(user.getUid()).child(cartData.getId()).setValue(value(cartData)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"장바구니에에 추가되었습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(ClickShowActivity.this);
                    dlg.setTitle("BullT");
                    dlg.setMessage("로그인이 필요한 서비스 입니다.\n로그인 하시겠습니까??");
                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ClickShowActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(),"로그인이 필요합니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dlg.show();
                }
            }
        });



    }

    //이미지 불러오는 파이어베이스
    private void refFirebase(){
        StorageReference sref = FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("imagePath"));
        sref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(!task.isSuccessful()){
                    Log.d("레퍼런스a", String.valueOf(sref));
                }
                else {
                    Glide.with(getApplicationContext())
                            .load(task.getResult())
                            .fitCenter()
                            .placeholder(R.drawable.round)
                            .into(imageView);
                }
            }
        });
    }
    private String radioCheck(){
        String check = null;
        if(r1.isChecked()){
            check = r1.getText().toString();
        }else if(r2.isChecked()){
            check = r2.getText().toString();
        }else if(r3.isChecked()){
            check = r3.getText().toString();
        }else if(r4.isChecked()){
            check = r4.getText().toString();
        }else if(r5.isChecked()){
            check = r5.getText().toString();
        }
        return check;
    }
    private HashMap<String,Object> value(CartData cartData){
        HashMap<String,Object> map = new HashMap<>();
        map.put("title",cartData.getTitle());
        map.put("content",cartData.getContent());
        map.put("price",cartData.getPrice());
        map.put("size",cartData.getSize());
        map.put("id",cartData.getId());
        map.put("imagePath",cartData.getImagePath());
        map.put("totalPrice",cartData.getTotalprice());
        map.put("count",cartData.getCount());
        return map;
    }
}