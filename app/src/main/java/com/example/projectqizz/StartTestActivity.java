package com.example.projectqizz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.ui.admin.ManagerQuestionActivity;

public class StartTestActivity extends AppCompatActivity {
    private TextView txtCatName,txtTestNo,txtTotalQ,txtBestScore,txtTime;
    private Button btnStartTest,btnManagerQuestion;
    private ImageView btnBack;
    private Dialog progressDialog;
    private TextView dialogText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        progressDialog  = new Dialog(StartTestActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");
        progressDialog.show();
        init();

        DbQuery.loadQuestions(new MyCompleteListener() {
            @Override
            public void onSucces() {
                setData();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(StartTestActivity.this,"Tải thất bại",
                        Toast.LENGTH_SHORT).show();
            }
        });

        if(DbQuery.myProfile.getAdmin() == true){
            btnManagerQuestion.setVisibility(View.VISIBLE);
        }
    }

    private void setData() {
        txtCatName.setText(DbQuery.g_catList.get(DbQuery.g_selected_cat_index).getName());
        txtTestNo.setText(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getName());
        txtTotalQ.setText(String.valueOf(DbQuery.g_quesList.size()));
        txtBestScore.setText(String.valueOf(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTopScore()));
        txtTime.setText(String.valueOf(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()));
    }

    public void init(){
        txtCatName = findViewById(R.id.txt_st_cat_name);
        txtTestNo = findViewById(R.id.txt_st_test_no);
        txtTotalQ = findViewById(R.id.txt_st_total_question);
        txtBestScore = findViewById(R.id.txt_st_best_score);
        txtTime = findViewById(R.id.txt_st_time);
        btnStartTest = findViewById(R.id.btn_starTest);
        btnBack = findViewById(R.id.btnStBack);
        btnManagerQuestion = findViewById(R.id.btn_managerQuestion);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTestActivity.this.finish();
            }
        });

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTestActivity.this, QuestionsActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btnManagerQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartTestActivity.this, ManagerQuestionActivity.class);
                startActivity(intent);
            }
        });
    }
}