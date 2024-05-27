package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {
    private EditText edtFullName,edtEmail,edtPassword,edtComfirmPassword;
    private Button btnSignUp;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private String emailStr,passStr,comfirmPassStr,nameStr;
    private Dialog progressDialog;
    private TextView dialogText,btnLoginNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtFullName = findViewById(R.id.edtUserNameSignup);
        edtEmail = findViewById(R.id.edtEmailSignup);
        edtPassword = findViewById(R.id.edtPasswordSignup);
        edtComfirmPassword = findViewById(R.id.edtPasswordComfirm);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);
        btnLoginNow = findViewById(R.id.btnLoginNow);
        mAuth = FirebaseAuth.getInstance();

        progressDialog  = new Dialog(SignUpActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Registering user...");

        btnBack.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào icon "<--" (quay lai giao diện trước đó)
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click "Đăng ký"
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    signNewUser();
                }
            }
        });
        btnLoginNow.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click "Login Now"
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean validate()
    {//Kiểm tra dữ liệu đầu xem sao người dùng có bỏ trống không,mật khẩu nhập lại có đúng không
        nameStr = edtFullName.getText().toString().trim();
        passStr = edtPassword.getText().toString().trim();
        emailStr = edtEmail.getText().toString().trim();
        comfirmPassStr = edtComfirmPassword.getText().toString().trim();
        if(nameStr.isEmpty()){
            edtFullName.setError("Enter Your Name");
            return false;
        }if(passStr.isEmpty()){
            edtPassword.setError("Enter Password");
            return false;
        }if(emailStr.isEmpty()){
            edtEmail.setError("Enter Email ID");
            return false;
        }if(comfirmPassStr.isEmpty()){
            edtComfirmPassword.setError("Enter Password");
            return false;
        }else if(passStr.compareTo(comfirmPassStr) != 0){
            Toast.makeText(SignUpActivity.this,"Password and comfirmPassword should be same",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void signNewUser()
    {//Xử lý thêm user đăng ký vào database
        progressDialog.show();//hiêện thi vòng quay
        //dùng Firebase Authentication trong Android và được sử dụng để đăng ký một người dùng mới
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                            //Nếu đăng ký thành công gọi tạo xử lý thêm thông tin đăng ký vào trong dabatase
                            DbQuery.createUserData(emailStr,nameStr,new MyCompleteListener(){
                                @Override
                                public void onSucces() {
                                    progressDialog.dismiss();//ẩn đi
                                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    SignUpActivity.this.finish();
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(SignUpActivity.this,"Đăng ký thất bại",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();//ẩn đi
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this,"Đăng ký thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}