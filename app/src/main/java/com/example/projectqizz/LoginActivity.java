package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail,edtPassword;
    private Button btnLogin;
    private TextView btnForgotPassword,btnSignUp;
    private FirebaseAuth mAuth;
    private Dialog progressDialog;
    private TextView dialogText;
    private LinearLayout btnSignInGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 104;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPassword = findViewById(R.id.txtForgotPassword);
        btnSignUp = findViewById(R.id.btnSIgnUp);
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        mAuth = FirebaseAuth.getInstance();

        progressDialog  = new Dialog(LoginActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText(" Signing in...");

        //thiết lập cấu hình cho đăng nhập Google và tạo một đối tượng khách hàng GoogleSignInClient để bắt đầu quá trình đăng nhập
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Đăng nhập"
            @Override
            public void onClick(View v) {
                if(validateData()){
                    login();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Đăng ký"
            @Override
            public void onClick(View v) {
                //Chuyển sang giao diện đăng ký
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Đăng nhập bằng google"
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "SIGN UP"
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void googleSignIn()
    {//khởi động quá trình đăng nhập bằng Google
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) //di kem dang nhap bang gooogle
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode  == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e){
                Toast.makeText(LoginActivity.this,e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void firebaseAuthWithGoogle(String idToken)
    {//Hàm xử lý login bằng google
        progressDialog.show();
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser())//lần đầu đăng nhập thì tạo
                            {
                                Toast.makeText(LoginActivity.this,"Đăng nhập google thành công",
                                        Toast.LENGTH_SHORT).show();
                                //Sau khi login mà thành công thì sẽ gọi hàm sử lý lương thông tin người dùng vào database
                                DbQuery.createUserData(user.getEmail(), user.getDisplayName(), new MyCompleteListener() {
                                    @Override
                                    public void onSucces() {
                                        //Gọi hàm xử lý lấy những thông tin cần thiết để load app
                                        DbQuery.loadData(new MyCompleteListener() {
                                            @Override
                                            public void onSucces() {
                                                progressDialog.dismiss();
                                                //Chuyển đến giao diện chính của app
                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                LoginActivity.this.finish();
                                            }

                                            @Override
                                            public void onFailure() {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this,"Đăng nhập thất bại",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        progressDialog.dismiss();

                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();

                                    }

                                    @Override
                                    public void onFailure() {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,"Đăng nhập thất bại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else
                            {//Xử lý nếu như người dùng đã từng đăng nhập bằng tài khoản google trước đó
                                //Gọi hàm xử lý lấy những thông tin cần thiết để load app
                                DbQuery.loadData(new MyCompleteListener() {
                                    @Override
                                    public void onSucces() {
                                        progressDialog.dismiss();

                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();

                                    }

                                    @Override
                                    public void onFailure() {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,"Tải thất bại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void login()
    {//Hàm xử lý login bằng tài khoản mật khẩu
        progressDialog.show();//hiện thị họp thoai vòng vòng
        //Check tài khoản mật khẩu bằng hàm signInWithEmailAndPassword
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công",
                                    Toast.LENGTH_SHORT).show();
                            //Nếu như đăng nhập thành công thì gọi hàm xử lý lấy thông tin cần thiết để load app
                            DbQuery.loadData(new MyCompleteListener() { //load tất cả category lên main
                                @Override
                                public void onSucces() {
                                    progressDialog.dismiss();
                                    //Chuyển đến giao diện chính của app
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure() {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this,"Tải thất bại",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateData()
    {//Hàm này xử lý check dữ liệu đầu vào xem coi người dùng có bỏ trống không
        if(edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("Enter E-Mail ID");
            return false;
        }
        if(edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("Enter Password");
            return false;
        }
        return true;
    }
}