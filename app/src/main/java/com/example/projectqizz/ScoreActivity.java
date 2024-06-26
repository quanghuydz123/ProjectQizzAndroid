package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;

import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {
    private TextView txtScore,txtTime,txtTotalQ,txtCorrectQ,txtWrongQ,txtUnAttempedQ;
    private Button btnLeader,btnReAttempt,btnViewAns;
    private long timeTaken;
    private Dialog progressDialog;
    private TextView dialogText;
    private Toolbar toolbar;
    private int finalScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        //Khởi tạo toolbar - Bành Viết Hung
        toolbar = findViewById(R.id.toolbarScore);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Kết quả");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiện thị thanh quay lại

        progressDialog  = new Dialog(ScoreActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");
        progressDialog.show();

        init();

        loadData();//Tải các thông tin hiện cần thiết để hiện thị trong app - Nguyễn Quang Huy

        setBookMarks();//Xử lý thêm những câu hỏi người dùng đã muốn lưu trong lúc làm bài vào danh sách - Nguyễn Quang Huy

        btnViewAns.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click "Xem lại bài làm" (hiện thị danh sách câu hỏi và đáp án mà người dùng đã chọn) - Bành Viết Hùng
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this,AnswersActivity.class);
                startActivity(intent);
            }
        });

        btnReAttempt.setOnClickListener(new View.OnClickListener()
        {//Hàm này xử lý khi người dùng click "Làm lại" (Chuyển đến giao diện làm lại bài làm) - Bành Viết Hùng
            @Override
            public void onClick(View v) {
                for(int i = 0 ;i<DbQuery.g_quesList.size(); i++){
                    DbQuery.g_quesList.get(i).setSelectedAns(-1);
                    DbQuery.g_quesList.get(i).setStatus(DbQuery.NOT_VISITED);
                }
                Intent intent = new Intent(ScoreActivity.this,StartTestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        saveResult();
    }

    private void setBookMarks()
    {//Hàm này xử lý thêm câu hỏi người dùng muốn lưu vào danh sách g_bmIdList - Nguyễn Quang Huy
        for (int i = 0 ; i < DbQuery.g_quesList.size() ; i++){
            if(DbQuery.g_quesList.get(i).isBookmarked()){
                if(!DbQuery.g_bmIdList.contains(DbQuery.g_quesList.get(i).getqID())){
                    DbQuery.g_bmIdList.add(DbQuery.g_quesList.get(i).getqID());
                    DbQuery.myProfile.setBookMarksCount(DbQuery.g_bmIdList.size());
                }
            }else{
                if(DbQuery.g_bmIdList.contains(DbQuery.g_quesList.get(i).getqID())){
                    DbQuery.g_bmIdList.remove(DbQuery.g_quesList.get(i).getqID());
                    DbQuery.myProfile.setBookMarksCount(DbQuery.g_bmIdList.size());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    { //Ghi dè hàm này xử lý khi người click vào icon "<--" trên toolbar (để quay lại giao diện trước đó) - Bành Viết Hùng
        if(item.getItemId() == android.R.id.home){
            ScoreActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveResult()
    {//Hàm này xử lý cập nhập điểm của bài kiểm tra này vào database (Chỉ lấy điểm cao nhất)
        //Gọi hàm xử lý cập nhập điểm bài kiểm tra vào database - Nguyễn Quang Huy
        DbQuery.saveResult(finalScore, new MyCompleteListener() {
            @Override
            public void onSucces() {
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(ScoreActivity.this,"Lưu thất bại",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void init()
    {//Đoạn mã này tìm và gán các thành phần giao diện từ tệp XML layout vào các biến Java - Nguyễn Quang Huy
        txtScore = findViewById(R.id.txt_score);
        txtTime = findViewById(R.id.txt_time);
        txtTotalQ = findViewById(R.id.txt_totalQ);
        txtCorrectQ = findViewById(R.id.txt_correct);
        txtWrongQ = findViewById(R.id.txt_wrong);
        txtUnAttempedQ = findViewById(R.id.txt_unAttepted0);
        btnLeader = findViewById(R.id.btn_leader);
        btnReAttempt = findViewById(R.id.btn_reattemp);
        btnViewAns = findViewById(R.id.btn_view_answers);

    }
    public void loadData()
    {//Hàm này xử lý load các thông tin liên quan hiện thi lên layout (điểm số,số câu làm sai,số câu làm đúng,...) - Nguyễn Quang Huy
        int correctQ = 0,wrongQ = 0,unanttempQ = 0;
        for(int i = 0 ; i < DbQuery.g_quesList.size() ; i++){
            if(DbQuery.g_quesList.get(i).getSelectedAns() == -1){
                unanttempQ++;
            }else{
                if(DbQuery.g_quesList.get(i).getSelectedAns() == DbQuery.g_quesList.get(i).getCorrectAns()){
                    correctQ++;
                }else{
                    wrongQ++;
                }
            }
        }
        txtCorrectQ.setText(String.valueOf(correctQ));
        txtWrongQ.setText(String.valueOf(wrongQ));
        txtUnAttempedQ.setText(String.valueOf(unanttempQ));
        txtTotalQ.setText(String.valueOf(DbQuery.g_quesList.size()));

        finalScore = (correctQ*100)/DbQuery.g_quesList.size();
        txtScore.setText(String.valueOf(finalScore));

        timeTaken = getIntent().getLongExtra("TIME_TAKEN",0);
        String time = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        );
        txtTime.setText(time);
    }
}