package com.example.bullt.Recycler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


// 기능 정리
//email,password 입력하면 로그인
//        email 입력양식으로 입력하지 않으면 에러
//        password 8자리 앞자리 숫자 x,특수문자 포함
//로그인 버튼 성공하면 메인 페이지로 이동 실패시 alertDialog창
//아이디 비밀번호 찾기는 천천히 구현


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

//  아이디,패스워드,
    EditText login_email_et,login_pwd_et;
//  패스워드 보이기
    ToggleButton pwd_view_tg;
//  로그인 버튼
    Button loginSubmit_btn;
//  아이디/비밀번호 , 회원가입
    TextView research_idPwd_tv,register_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        SetInit();
    }
//  아이디 연결
    public void SetInit(){
        login_email_et = findViewById(R.id.login_email);
        login_email_et.requestFocus();
//      키보드 자동으로 올려주기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        login_pwd_et = findViewById(R.id.login_pwd);

//      비밀번호 보여주는 버튼
        pwd_view_tg = findViewById(R.id.pwd_view);
        pwd_view_tg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    pwd_view_tg.setBackgroundResource(R.drawable.eye);
                    login_pwd_et.setInputType(InputType.TYPE_CLASS_TEXT);
                }else{
                    pwd_view_tg.setBackgroundResource(R.drawable.close_eyes);
                    login_pwd_et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);

                }
            }
        });
        loginSubmit_btn = findViewById(R.id.loginSubmit);
        loginSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email_et.getText().toString();
                String pwd = login_pwd_et.getText().toString();
                mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }   else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        research_idPwd_tv = findViewById(R.id.research_idPwd);
        register_tv = findViewById(R.id.register_txt);
    }
}