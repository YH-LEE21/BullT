package com.example.bullt.Login_Register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bullt.Activity.MainActivity;
import com.example.bullt.R;
import com.example.bullt.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


// 기능 정리


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
//  아이디,패스워드,
    EditText login_email_et,login_pwd_et;
//  패스워드 보이기
    ToggleButton pwd_view_tg;
//  로그인 버튼
    Button loginSubmit_btn;
//  아이디/비밀번호 , 회원가입
    TextView research_idPwd_tv,register_tv;
// main activity로 돌아가는 버튼
    ImageView comeBackHome_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SetInit();
    }
    public void SetInit(){

        //이메일 입력 EditText,
        login_email_et = findViewById(R.id.login_email);
        login_email_et.requestFocus();

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
        firebaseAuth = FirebaseAuth.getInstance();
        //로그인 버튼튼
        loginSubmit_btn = findViewById(R.id.loginSubmit);
        loginSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login_email_et.getText().toString().equals("") && !login_pwd_et.getText().toString().equals("")) {
                    loginUser(login_email_et.getText().toString().trim(), login_pwd_et.getText().toString().trim());
                } else {
                    Toast.makeText(getApplicationContext(), "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                }
            }
        };
        research_idPwd_tv = findViewById(R.id.research_idPwd);
        
        //회원가입텍스트
        register_tv = findViewById(R.id.register_txt);
        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //홈 이미지
        comeBackHome_iv = findViewById(R.id.comeBackHome_iv);
        comeBackHome_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //키보드 자동으로 올리기
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 키보드 자동으로 내리기
        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void loginUser(String email, String password) {
       firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            database.getReference("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User a = snapshot.getValue(User.class);
                                        Toast.makeText(getApplicationContext(),a.getName()+ "님 환영합니다.",Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // 로그인 실패
                            Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}