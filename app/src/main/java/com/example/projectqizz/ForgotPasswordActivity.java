package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private String emailStr;
    private Dialog progressDialog;
    private TextView dialogText,btnLoginNow;
    private Button btnForgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bành Viết Hùng
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.edtEmailForgot);
        btnBack = findViewById(R.id.btn_fp_Back);
        btnLoginNow = findViewById(R.id.btn_fp_LoginNow);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        mAuth = FirebaseAuth.getInstance();

        progressDialog  = new Dialog(ForgotPasswordActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");

        btnBack.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người click vào icon "<--" trên cùng (để quay lại giao diện trước đó" - Bành Viết Hùng
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLoginNow.setOnClickListener(new View.OnClickListener()
        {//Xử khi người dùng click vào nút "Login Now" - Bành Viết Hùng
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Đổi mật khẩu" - Bành Viết Hùng
            @Override
            public void onClick(View v) {
                emailStr = email.getText().toString();
                //Check dữ liệu người nhập vào nếu có thì gọi hàm xử lý ResetPassword
                if (!TextUtils.isEmpty(emailStr)) {
                    ResetPassword();
                } else {
                    email.setError("Email field can't be empty");
                }
            }
        });
    }
    private void ResetPassword()
    {//Hàm này xử lý đổi mật khẩu người dùng - Bành Viết Hùng
        progressDialog.show();
        btnForgotPassword.setVisibility(View.INVISIBLE);
        //Đoạn mã ngày sẽ gửi 1 Link để đổi mật khẩu về email người dùng là giá trị emailStr người dùng nhập vào
        mAuth.sendPasswordResetEmail(emailStr)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        btnForgotPassword.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgotPasswordActivity.this, "Link đặt lại mật khẩu đã được gửi tới Email đăng ký của bạn", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}