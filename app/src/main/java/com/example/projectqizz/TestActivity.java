package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.Adapter.TestAdapter;
import com.example.projectqizz.DB.DbQuery;

public class TestActivity extends AppCompatActivity {
    private RecyclerView testView;
    private Toolbar toolbar;
    private TestAdapter testAdapter;
    private Dialog progressDialog;
    private TextView dialogText;
    private Button btnCreateTest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testView = findViewById(R.id.test_recyler_view);
        toolbar = findViewById(R.id.toolbar);
        btnCreateTest = findViewById(R.id.btn_create_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(DbQuery.g_catList.get(DbQuery.g_selected_cat_index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiện thị thanh quay lại

        progressDialog  = new Dialog(TestActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");
        progressDialog.show();

        //cấu hình recylerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testView.setLayoutManager(layoutManager);

        DbQuery.loadTestData(new MyCompleteListener() {
            @Override
            public void onSucces() {
                DbQuery.loadMyScore(new MyCompleteListener() {
                    @Override
                    public void onSucces() {
                        // truyền đata và recly view
                        testAdapter = new TestAdapter(DbQuery.g_testList);
                        testView.setAdapter(testAdapter);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(TestActivity.this,"Tải thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(TestActivity.this,"Tải thất bại",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTest(testAdapter);
            }
        });

    }

    private void createTest(TestAdapter testAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);//xây dựng ra 1 thông báo
        builder.setCancelable(true);
        View view = LayoutInflater.from(TestActivity.this).inflate(R.layout.form_create_test, null);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
        EditText edtNameTest = view.findViewById(R.id.edt_name_test_create);
        EditText edtTimeTest = view.findViewById(R.id.edt_time_test_create);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();//tạo ra thông báo
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_cofirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()) {
                    DbQuery.createTest(edtNameTest.getText().toString(), Integer.parseInt(edtTimeTest.getText().toString()), new MyCompleteListener() {
                        @Override
                        public void onSucces() {
                            testAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                            Toast.makeText(TestActivity.this,"Thêm thành công",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            alertDialog.dismiss();
                            Toast.makeText(TestActivity.this,"Lỗi rồi",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            private boolean validateData() {
                if(edtNameTest.getText().toString().isEmpty()){
                    edtNameTest.setError("Hãy nhập tên tên bài kiểm tra !!");
                    return false;
                }
                if(edtTimeTest.getText().toString().isEmpty()){
                    edtTimeTest.setError("Hãy nhập thời gian làm bài kiểm tra !!");
                    return false;
                }
                return true;
            }
        });
        alertDialog.show();
    }


    //nút mũi tên trên toolbar ấn thoát
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            TestActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}