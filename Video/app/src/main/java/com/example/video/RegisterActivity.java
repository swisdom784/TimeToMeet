package com.example.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;         //파이어 베이스 인증
    DatabaseReference mDatabaseRef;     //실시간 데이터 베이스
    EditText mEtEmail,mEtPwd,mEtName;
    Button mEtnRegister;
    TextView pwCheck,emCheck,nameCheck;
    ImageView iv_main;
    List<String> nickname;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none, R.anim.horizontal_exit);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(R.anim.fade_in, R.anim.none);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mEtEmail = findViewById(R.id.username);
        mEtPwd = findViewById(R.id.password);
        mEtnRegister = findViewById(R.id.signupbtn);
        mEtName = findViewById(R.id.name);
        pwCheck = findViewById(R.id.pwcheckText);
        emCheck = findViewById(R.id.pwcheckText2);
        nameCheck = findViewById(R.id.pwcheckText3);

        iv_main = findViewById(R.id.back_to_main1);
        iv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emCheck.setVisibility(View.INVISIBLE);
                } else {
                    emCheck.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    pwCheck.setVisibility(View.INVISIBLE);
                } else {
                    pwCheck.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDatabaseRef.child("userNickname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickname = (List<String>) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mEtnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String strEmail = mEtEmail.getText().toString().trim();
                String strPwd = mEtPwd.getText().toString().trim();
                String strName = mEtName.getText().toString().trim();

                String email = mEtEmail.getText().toString().trim();
                String password = mEtPwd.getText().toString().trim();
                String name = mEtName.getText().toString().trim();

                for(String k: nickname){
                    if(strName.equals(k)){
                        nameCheck.setText("이미 존재하는 닉네임입니다");
                        return;
                    }
                }
             nameCheck.setVisibility(View.INVISIBLE);

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

                if (name.isEmpty()) {
                    mEtName.setError("Name is required");
                    mEtName.requestFocus();
                    return;
                }
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            nickname.add(strName);
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            account.setUsername(strName);

                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                            mDatabaseRef.child("userNickname").setValue(nickname);
                            showCustomToast("회원가입에 성공했어요");
                            finish();
                        } else {
                            // Check the exception for specific error details
                            String errorMessage;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                errorMessage = "이미 존재하는 이메일입니다";
                            } catch (FirebaseAuthWeakPasswordException e) {
                                errorMessage = "비밀번호를 6자리 이상 입력해주세요";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = "이메일 형식이 올바르지 않습니다";
                            } catch (Exception e) {
                                errorMessage = "회원가입에 실패했습니다";
                            }
                            showCustomToast(errorMessage);
                        }
                    }
                });
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