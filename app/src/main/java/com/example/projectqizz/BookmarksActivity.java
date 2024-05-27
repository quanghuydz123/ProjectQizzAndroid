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

import com.example.projectqizz.Adapter.BookmarkAdapter;
import com.example.projectqizz.DB.DbQuery;

public class BookmarksActivity extends AppCompatActivity {
    private RecyclerView questionView;
    private Toolbar toolbar;
    private Dialog progressDialog;
    private TextView dialogText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        questionView = findViewById(R.id.bm_recyler_view);
        //Khởi tạo toolbar
        toolbar = findViewById(R.id.bm_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Câu hỏi đã lưu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiện thị thanh quay lại

        progressDialog  = new Dialog(BookmarksActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");
        progressDialog.show();

        //cấu hình recylerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        questionView.setLayoutManager(layoutManager);
        ////Gọi hàm xử lý lấy danh sách những câu hỏi đã lưu của người dùng trong database
        DbQuery.loadBookmarks(new MyCompleteListener() {
            @Override
            public void onSucces() {
                //Khởi tạo adapter và truyền adapter cho questionView
                BookmarkAdapter adapter = new BookmarkAdapter(DbQuery.g_bookmarksList,DbQuery.g_bmIdList);
                questionView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(BookmarksActivity.this,"Tải thất bại",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }
    //nút mũi tên trên toolbar ấn thoát
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {//Ghi dè hàm này xử lý khi người click vào icon "<--" trên toolbar (để quay lại giao diện trước đó)
        if(item.getItemId() == android.R.id.home){
            BookmarksActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}