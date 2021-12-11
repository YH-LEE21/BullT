package com.example.bullt.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bullt.Data.ItemData;
import com.example.bullt.Login_Register.LoginActivity;
import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity {

    private Button profile_edit_btn,easyPay_btn,refund_btn,logout_btn,sign_out_btn;
    private ImageButton back_ib;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    ArrayList<ItemData> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Setinit();
    }

    private void Setinit(){
        back_ib = findViewById(R.id.back_ib);
        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //회원 정보 수정
        profile_edit_btn = findViewById(R.id.profile_edit_btn);
        profile_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원 정보 수정
            }
        });

        //간편 결제 관리
        easyPay_btn = findViewById(R.id.easyPay_btn);
        easyPay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"간편 결제 관리",Toast.LENGTH_SHORT).show();
            }
        });

        //환불 계좌 설정
        refund_btn = findViewById(R.id.refund_btn);
        refund_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"환불 계좌 설정",Toast.LENGTH_SHORT).show();
            }
        });

        //로그아웃
        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(),"로그아웃 됨",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //회원탈퇴
        sign_out_btn = findViewById(R.id.sign_out_btn);
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원 탈퇴페이지로 이동해서 탈퇴할지 안할지 결정
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyProfileActivity.this);
                dlg.setTitle("BullT");
                dlg.setMessage("회원 탈퇴\n정말 하시겠습니까??");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //회원관련된 모든기록을 삭제

                        firebaseDatabase.getReference().child("Favorite").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                data.clear();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    ItemData dd = dataSnapshot.getValue(ItemData.class);
                                    onStarClicked(firebaseDatabase.getReference("ListItem").child(dd.getId()));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("asdfasdf","실패");
                            }
                        });
                        //좋아요한 LIST,장바구니 CART,최근조회 목록,유저정보
                        firebaseDatabase.getReference().child("Favorite").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        firebaseDatabase.getReference().child("Cart").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        firebaseDatabase.getReference().child("Lately").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        Intent intent = new Intent(MyProfileActivity.this,LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(MyProfileActivity.this,"회원탈퇴가 완료 되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.show();
            }
        });
    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ItemData p = mutableData.getValue(ItemData.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.getHearts().containsKey(firebaseUser.getUid())) {
                    // Unstar the post and remove self from stars
                    p.setCount(p.getCount()-1);
                    p.getHearts().remove(firebaseUser.getUid());
                }

                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
            }
        });
    }
}