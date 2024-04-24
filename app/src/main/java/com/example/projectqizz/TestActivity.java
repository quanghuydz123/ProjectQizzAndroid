package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.Adapter.TestAdapter;
import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Fragment.CategoryFragment;
import com.example.projectqizz.Model.TestModel;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private RecyclerView testView;
    private Toolbar toolbar;
    private TestAdapter testAdapter;
    private Dialog progressDialog;
    private TextView dialogText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testView = findViewById(R.id.test_recyler_view);
        toolbar = findViewById(R.id.toolbar);

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
        //loadData
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