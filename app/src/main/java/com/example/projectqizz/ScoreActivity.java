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

        loadData();

        setBookMarks();

        btnViewAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this,AnswersActivity.class);
                startActivity(intent);
            }
        });

        btnReAttempt.setOnClickListener(new View.OnClickListener() {
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

    private void setBookMarks() {
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

    //nút mũi tên trên toolbar ấn thoát
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            ScoreActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveResult() {
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

    public void init(){
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
    public void loadData(){
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