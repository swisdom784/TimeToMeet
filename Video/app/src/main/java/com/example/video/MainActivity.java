package com.example.video;


import static androidx.fragment.app.FragmentManager.TAG;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.ComponentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;         //파이어 베이스 인증
    DatabaseReference mDatabaseRef;     //실시간 데이터 베이스
    EditText mEtEmail,mEtPwd;
    Button mEtnRegister;
    Button mEtnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.horizontal_enter, R.anim.none);

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
                            overridePendingTransition(R.anim.horizontal_enter, R.anim.none);
                        } else {
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
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
                overridePendingTransition(R.anim.horizontal_enter, R.anim.none);

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
        }

    }

}