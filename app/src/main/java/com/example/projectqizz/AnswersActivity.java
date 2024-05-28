package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.projectqizz.Adapter.AnswersAdapter;
import com.example.projectqizz.Adapter.TestAdapter;
import com.example.projectqizz.DB.DbQuery;

public class AnswersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Dialog progressDialog;
    private TextView dialogText;
    private RecyclerView answersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        answersView = findViewById(R.id.test_recyler_view);
        //khởi tạo toolbar - Bành Viết Hùng
        toolbar = findViewById(R.id.aa_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Xem lại câu trả lời");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiện thị thanh quay lại

        //cấu hình - Bành Viết Hùng
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        answersView.setLayoutManager(layoutManager);
        //truyền adapter cho answersView - Bành Viết Hùng
        AnswersAdapter answersAdapter = new AnswersAdapter(DbQuery.g_quesList);
        answersView.setAdapter(answersAdapter);


    }
    //nút mũi tên trên toolbar ấn thoát
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {//Ghi dè hàm này xử lý khi người click vào icon "<--" trên toolbar (để quay lại giao diện trước đó) - Bành Viết Hùng
        if(item.getItemId() == android.R.id.home){
            AnswersActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}