package com.example.video;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;         //파이어 베이스 인증
    DatabaseReference mDatabaseRef;     //실시간 데이터 베이스
    EditText mEtEmail,mEtPwd;
    Button mEtnRegister;
    Button mEtnLogin;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.vertical_enter, R.anim.none);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mEtEmail = findViewById(R.id.username);
        mEtPwd = findViewById(R.id.password);
        mEtnLogin = findViewById(R.id.loginbtn);
        mEtnRegister = findViewById(R.id.signupbtn);

       FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {

            // User is logged in, navigate to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);

//            finish();  // Close the current activity
        }
        mEtnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                String email = mEtEmail.getText().toString().trim();
                String password = mEtPwd.getText().toString().trim();

                if (email.isEmpty()) {
                    mEtEmail.setError("Email is required");
                    mEtEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    mEtPwd.setError("Password is required");
                    mEtPwd.requestFocus();
                    return;
                }
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            showCustomToast("로그인에 실패했어요\n아이디나 비밀번호를 확인해주세요");
                        }
                    }
                });
            }
        });
        mEtnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        ImageView image = layout.findViewById(R.id.toast_image);
        image.setImageResource(R.drawable.logo01); // 원하는 아이콘 리소스 설정

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}