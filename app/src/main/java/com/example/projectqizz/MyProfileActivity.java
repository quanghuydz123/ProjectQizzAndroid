package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;

public class MyProfileActivity extends AppCompatActivity {
    private EditText edtName,edtEmail,edtPhone;
    private LinearLayout btn_edit,btn_layout;
    private Button btn_cancel,btn_save;
    private TextView txt_profile;
    private Toolbar toolbar;
    private String nameStr,phoneStr;
    private TextView dialogText;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        //Nguyễn Quang Huy
        edtName = findViewById(R.id.edt_mp_name);
        edtEmail = findViewById(R.id.edt_mp_email);
        edtPhone = findViewById(R.id.edt_mp_phone);
        btn_edit = findViewById(R.id.btn_edit_pf);
        btn_cancel = findViewById(R.id.btn_cancel_pf);
        btn_save = findViewById(R.id.btn_save_pf);
        txt_profile = findViewById(R.id.txt_profile);
        btn_layout = findViewById(R.id.btn_layout);
        //Khởi tạo toolbar - Nguyễn Quang Huy
        toolbar = findViewById(R.id.toolbar_pf);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Hồ sơ của tôi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiện thị thanh quay lại

        progressDialog  = new Dialog(MyProfileActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Updating Data...");

        disableEditing();//Hàm này xử lý load thông tin người và disable input không cho người dùng chỉnh sửa nếu chưa ấn nút "Edit" - Nguyễn Quang Huy

        btn_edit.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Edit" - Nguyễn Quang Huy
            @Override
            public void onClick(View v) {
                enalbleEditing();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Hủy" - Nguyễn Quang Huy
            @Override
            public void onClick(View v) {
                disableEditing();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener()
        {//Sử lý khi người dùng click vào nút "Lưu" - Nguyễn Quang Huy
            @Override
            public void onClick(View v) {
                if(validate()){
                    saveData();
                }
            }
        });
    }
    private boolean validate()
    {//Check dữ liệu đầu vào xem coi người dùng có bỏ trống không - Nguyễn Quang Huy
        nameStr = edtName.getText().toString();
        phoneStr = edtPhone.getText().toString();

        if(nameStr.isEmpty()){
            edtName.setError("Tên không thể bỏ trống !");
            return false;
        }
        if(!phoneStr.isEmpty()){
            if(!((phoneStr.length() == 10) && (TextUtils.isDigitsOnly(phoneStr)))){
                edtPhone.setError("Điện thoại chỉ có 10 số và phải là số");
                return false;
            }
        }
        return true;
    }
    private void saveData()
    {//Hàm này sử lý cập nhật thông tin vừa chỉnh sửa vào database - Nguyễn Quang Huy
        progressDialog.show();

        if(phoneStr.isEmpty()){
            phoneStr=null;
        }
        //Gọi hàm xử lý cập nhập thông tin người dùng vào database - Nguyễn Quang Huy
        DbQuery.saveProfileData(nameStr, phoneStr, new MyCompleteListener() {
            @Override
            public void onSucces() {
                disableEditing();
                progressDialog.dismiss();
                Toast.makeText(MyProfileActivity.this,"Cập nhập thành công",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(MyProfileActivity.this,"Cập nhập thất bại",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void enalbleEditing()
    {//Hàm này xử lý cho phép người dùng chỉnh sửa thông tin trong các input - Nguyễn Quang Huy
        edtName.setEnabled(true);
        edtPhone.setEnabled(true);
        //edtEmail.setEnabled(true);
        btn_layout.setVisibility(View.VISIBLE);
    }

    private void disableEditing()
    {//Hàm này xử lý disable các input không cho người dùng chỉnh sửa và hiện thị thông tin người dùng - Nguyễn Quang Huy
        edtName.setEnabled(false);
        edtPhone.setEnabled(false);
        edtEmail.setEnabled(false);
        btn_layout.setVisibility(View.GONE);

        edtName.setText(DbQuery.myProfile.getName());
        edtEmail.setText(DbQuery.myProfile.getEmail());
        if (DbQuery.myProfile.getPhone() != null){
            edtPhone.setText(DbQuery.myProfile.getPhone());
        }
        //Lấy chữ cái đầu tiên ngoài cùng
        String profileName = DbQuery.myProfile.getName();
        String[] parts = profileName.split(" ");
        String lastName = parts[parts.length - 1]; // Lấy từ cuối cùng trong tên
        String firstLetter = lastName.toUpperCase().substring(0, 1);
        txt_profile.setText(firstLetter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {//Ghi dè hàm này xử lý khi người click vào icon "<--" trên toolbar (để quay lại giao diện trước đó) - Nguyễn Quang Huy
        if(item.getItemId() == android.R.id.home){
            MyProfileActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}