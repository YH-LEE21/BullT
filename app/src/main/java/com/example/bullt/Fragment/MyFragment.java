package com.example.bullt.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bullt.Login_Register.LoginActivity;
import com.example.bullt.R;
import com.example.bullt.Cart.CartActivity;
import com.example.bullt.Activity.LatelyActivity;
import com.example.bullt.Activity.MyProfileActivity;
import com.example.bullt.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyFragment extends Fragment {
    private View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    ImageView cart_iv;
    Button profile_btn,myInquiry_btn,myLately_btn,myFAQ_btn,myNotice_btn,myLab_btn,mySetting_btn,appVersion_btn;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_fragment, container, false);
        firebaseUser = firebaseAuth.getCurrentUser();
        Setinit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            // TODO: 2021-12-09  
            String uid = firebaseUser.getUid();
            database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        User select_item = ds.getValue(User.class);
                        if(select_item.getUid().equals(firebaseAuth.getCurrentUser().getUid())){
                            Log.e("profile_btn","성공");
                            profile_btn.setText("   "+select_item.getName()+"님 환영합니다.\n\n   "+select_item.getEmail());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){

        }
    }

    private void Setinit(){
        cart_iv = view.findViewById(R.id.cart_iv);
        cart_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        profile_btn = view.findViewById(R.id.profile_btn);

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //로그인이 되어있다면
                    String uid = firebaseUser.getUid();
                    Intent intent = new Intent(getContext(),MyProfileActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    //로그인이 안되있다면
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        myInquiry_btn = view.findViewById(R.id.myInquiry_btn);
        myInquiry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"문의 내역",Toast.LENGTH_SHORT).show();
            }
        });
        
        
        
        myLately_btn = view.findViewById(R.id.myLately_btn);
        myLately_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LatelyActivity.class);
                startActivity(intent);
            }
        });
        
        myFAQ_btn = view.findViewById(R.id.myFAQ_btn);
        myFAQ_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"FAQ",Toast.LENGTH_SHORT).show();
            }
        });
        
        myNotice_btn = view.findViewById(R.id.myNotice_btn);
        myNotice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"공지사항",Toast.LENGTH_SHORT).show();
            }
        });
        
        myLab_btn = view.findViewById(R.id.myLab_btn);
        myLab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"실험실",Toast.LENGTH_SHORT).show();
            }
        });
        mySetting_btn = view.findViewById(R.id.mySetting_btn);
        mySetting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"설정",Toast.LENGTH_SHORT).show();
            }
        });
        appVersion_btn = view.findViewById(R.id.appVersion_btn);
        appVersion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"앱버전",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
