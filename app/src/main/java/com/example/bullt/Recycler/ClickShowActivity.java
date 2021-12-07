package com.example.bullt.Recycler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ClickShowActivity extends AppCompatActivity {
    ImageView imageView;
    TextView contentTop_tv,content_tv,price_tv,title_tv;
    ImageButton countUp_ib,countDown_ib;
    EditText count_et;
    RadioButton r1,r2,r3,r4,r5;

    Button website_btn,cart_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_show);
        setInit();
    }

    private void setInit(){

        imageView = findViewById(R.id.imageView);
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



        contentTop_tv = findViewById(R.id.contentTop_tv);
        contentTop_tv.setText(getIntent().getStringExtra("content"));

        content_tv = findViewById(R.id.content_tv);
        content_tv.setText(getIntent().getStringExtra("content"));

        price_tv = findViewById(R.id.price_tv);
        price_tv.setText(getIntent().getIntExtra("price",1)+"원");

        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(getIntent().getStringExtra("title"));

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

            }
        });
        count_et = findViewById(R.id.count_et);

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
            public void onClick(View v) {
                //여기서 trycatch를 사용하여 로그인한 유저만 장바구니 사용가능
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), "장바구니에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


    }
}