package com.example.projectqizz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private TextView app_name;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        app_name = findViewById(R.id.app_name);

//        Typeface typeface = ResourcesCompat.getFont(this,R.font.TheBlacklist);//tạo phone chữ
//        app_name.setTypeface(typeface);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.myamin);
        app_name.setAnimation(animation);

        mAuth = FirebaseAuth.getInstance();

        DbQuery.g_firestore = FirebaseFirestore.getInstance();// kết nối database
//        FirebaseUser user = mAuth.getCurrentUser(); Log.d giống như console.log
//        if (user != null) {
//            String email = user.getEmail();
//            Log.d("MyTag", "Email của người dùng: " + email);
//        } else {
//            Log.d("MyTag", "Người dùng hiện chưa đăng nhập");
//        }



        new Thread(){//sau 3 giay chuyển sang Main activity
            public void run(){
                try {
                    sleep(3000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(mAuth.getCurrentUser() != null)//kiểm tra đã đăng nhập hay chưa
                {
                    DbQuery.loadData(new MyCompleteListener() {
                        @Override
                        public void onSucces() {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(SplashActivity.this,"Tải thất bại",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    SplashActivity.this.finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }



            }
        }.start();
    }
}