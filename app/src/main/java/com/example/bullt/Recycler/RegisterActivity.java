package com.example.bullt.Recycler;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bullt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText meditId, meditPassword , meditPasswordOverlap, meditName , meditNumber; //회원가입 입력필드
    private Button mbtnRegister,bac_btn; // 회원가입 버튼
    private RadioButton rd1,rd2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth  = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        meditId = findViewById(R.id.editId);
        meditPassword = findViewById(R.id.editPassword);
        meditPasswordOverlap = findViewById(R.id.editPasswordOverlap);
        meditName = findViewById(R.id.editName);
        meditNumber = findViewById(R.id.editNumber);
        bac_btn = findViewById(R.id.back_btn);
        rd1 = findViewById(R.id.rd1);
        rd2 = findViewById(R.id.rd2);

        //회원가입 버튼 누르면
        mbtnRegister = findViewById(R.id.btnRegister);
        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 시작
                String strID = meditId.getText().toString();
                String strPassword = meditPassword.getText().toString();
                String strPasswordOverlap = meditPasswordOverlap.getText().toString();
                String strName = meditName.getText().toString();
                String strNumber = meditNumber.getText().toString();
                String strGender = null;
            
                if(rd1.isChecked()) strGender = rd1.getText().toString();
                else if(rd2.isChecked()) strGender = rd2.getText().toString();
                
                if (strID.length()!=0 && strPassword.length()!=0 && strPasswordOverlap.length()!=0 && strName.length()!=0 && strNumber.length()!=0){
                    //비밀번호 확인란과 비밀번호가 같지 않다면
                    if(strPassword.equals(strPasswordOverlap)){
                        //비밀번호가 특수문자 포함 8자리 이상이라면
                        if(pwCheck(strPassword)){
                            //firebase Auth 진행
                            if(strGender != null) {
                                String finalStrGender = strGender;
                                mFirebaseAuth.createUserWithEmailAndPassword(strID, strPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // 정보를 담는다
                                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                            HashMap<Object, String> Profile = new HashMap<>();
                                            Profile.put("uid", firebaseUser.getUid());
                                            Profile.put("email", firebaseUser.getEmail());
                                            Profile.put("gender", finalStrGender);
                                            Profile.put("name", strName);
                                            Profile.put("phone", strNumber);
                                            // setVlaue : database에  insert(삽입)행위
                                            mDatabaseRef.child("users").child(firebaseUser.getUid()).setValue(Profile);

                                            Toast.makeText(RegisterActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                            Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(RegisterActivity.this, "성별을 체크해주세요", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(RegisterActivity.this, "비밀번호는 특수문자 포함 8자리 이상으로 해주세요!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(RegisterActivity.this, "비밀번호가 같지 않아요!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "공백없이 전부 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bac_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public boolean pwCheck(String passWord){

        ArrayList<String> specialCharList = new ArrayList<>(Arrays.asList("`", "~", "!", "@" ,"#", "$", "%", "^",
                "&", "*", "(", ")", "_" ,"+" ,"-", "=", "/", "\\", "|", "{", "}", "[", "]", ";", ":", "\'",
                "\"", "<", ">", ".", ",", "?"));
        int i= 0;

        if (passWord.length()>=8){

            while (i!=31){

                if (passWord.contains(specialCharList.get(i))){
                    Log.d("체크","출력1");
                    return true;
                }else {
                    i++;
                }
            }

        }else {
            Log.d("체크","출력2");
            return false;
        }

        //기본값 false 부여
        Log.d("체크","출력3");
        return false;
    }
}