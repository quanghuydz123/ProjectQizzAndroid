package com.example.projectqizz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectqizz.Adapter.QuestionAdapter;
import com.example.projectqizz.Adapter.QuestionGridAdapter;
import com.example.projectqizz.DB.DbQuery;

import java.util.concurrent.TimeUnit;

public class QuestionsActivity extends AppCompatActivity {
    private RecyclerView questionsView;
    private TextView txtQuesID,txtTimer,txtCatName;
    private Button btnSubmit,btnMark,btnClearSel;
    private ImageButton btnPrevQues,btnNextQues,drawerCloseB;
    private ImageView btnQuesList,btnBookMark,markImage;
    private int quesId;
    QuestionAdapter adapter;
    private DrawerLayout drawerLayout;
    private GridView quesListGV;
    private QuestionGridAdapter questionGridAdapter;
    CountDownTimer timer;
    private long timeLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_list_layout);

        init();
        //cấu hình recylerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        questionsView.setLayoutManager(layoutManager);

        adapter = new QuestionAdapter(DbQuery.g_quesList);
        questionsView.setAdapter(adapter);

        questionGridAdapter = new QuestionGridAdapter(this,DbQuery.g_quesList.size());
        quesListGV.setAdapter(questionGridAdapter);
        quesListGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToQuestion(position);
            }
        });
        setSnapHelper(); // xử lý cuộn recylerView
        
        setClickListeners(); // xử lý click qua lại

        setStartTimer(); // bat dau tinh thoi gian
    }
    private void goToQuestion(int position){
        questionsView.smoothScrollToPosition(position);
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }
    private void setStartTimer() {
        long totalTime = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()*60*1000;
        timer = new CountDownTimer(totalTime + 1000,1000) {
            @Override
            public void onTick(long remainingTime) {
                    timeLeft = remainingTime;
                    String time = String.format("%02d:%02d min",
                            TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                            TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
                            );
                txtTimer.setText(time);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                long totalTime = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME_TAKEN",totalTime - timeLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        };
        timer.start();
    }

    private void setClickListeners() {
        btnPrevQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quesId > 0){
                    questionsView.smoothScrollToPosition(quesId - 1);
                }
            }
        });

        btnNextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quesId < DbQuery.g_quesList.size() -1){
                    questionsView.smoothScrollToPosition(quesId + 1);
                }
            }
        });

        btnClearSel.setOnClickListener(new View.OnClickListener() //loại bỏ câu trả lời
        {
            @Override
            public void onClick(View v) {
                DbQuery.g_quesList.get(quesId).setSelectedAns(-1);
                DbQuery.g_quesList.get(quesId).setStatus(DbQuery.UNANSWERED);
                markImage.setVisibility(v.GONE);
                adapter.notifyDataSetChanged();//gọi lài hàm setdata
            }
        });

        btnQuesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawerLayout.isDrawerOpen(GravityCompat.END))//GravityCompat.END kéo từ cuối ra ngoài
                {
                    questionGridAdapter.notifyDataSetChanged();
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        drawerCloseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.END)){
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        });

        btnMark.setOnClickListener(new View.OnClickListener()//đánh dấu câu hỏi
        {
            @Override
            public void onClick(View v) {
                if(markImage.getVisibility() != View.VISIBLE){
                    markImage.setVisibility(View.VISIBLE);
                    DbQuery.g_quesList.get(quesId).setStatus(DbQuery.REVIEW);
                }else{
                    markImage.setVisibility(View.GONE);
                    if(DbQuery.g_quesList.get(quesId).getStatus() != -1){
                        DbQuery.g_quesList.get(quesId).setStatus(DbQuery.ANSWERED);
                    }else{
                        DbQuery.g_quesList.get(quesId).setStatus(DbQuery.UNANSWERED);

                    }
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTest();
            }
        });

        btnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToBookmarks();
            }
        });
    }

    private void addToBookmarks() {
        if(DbQuery.g_quesList.get(quesId).isBookmarked()){
            DbQuery.g_quesList.get(quesId).setBookmarked(false);
            btnBookMark.setImageResource(R.drawable.twotone_bookmark_24);
        }else{
            DbQuery.g_quesList.get(quesId).setBookmarked(true);
            btnBookMark.setImageResource(R.drawable.ic_bookmark_24);
        }
    }

    private void submitTest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);//xây dựng ra 1 thông báo
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.alert_dialog_layuot,null);

        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_cofirm = view.findViewById(R.id.btn_comfirm);

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
                timer.cancel();
                alertDialog.dismiss();

                Intent intent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                long totalTime = DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME_TAKEN",totalTime - timeLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        });
        alertDialog.show();
    }
    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionsView);

        questionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quesId = recyclerView.getLayoutManager().getPosition(view);
                if(DbQuery.g_quesList.get(quesId).getStatus() == DbQuery.NOT_VISITED){
                    DbQuery.g_quesList.get(quesId).setStatus(DbQuery.UNANSWERED);
                }
                if(DbQuery.g_quesList.get(quesId).getStatus() == DbQuery.REVIEW ){
                    markImage.setVisibility(View.VISIBLE);
                }else{
                    markImage.setVisibility(View.GONE);
                }
                txtQuesID.setText(String.valueOf(quesId + 1) + "/" + String.valueOf(DbQuery.g_quesList.size()));

                if(DbQuery.g_quesList.get(quesId).isBookmarked()){
                    btnBookMark.setImageResource(R.drawable.ic_bookmark_24);
                }else{
                    btnBookMark.setImageResource(R.drawable.twotone_bookmark_24);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void init(){
        questionsView = findViewById(R.id.questions_view);
        txtQuesID = findViewById(R.id.txt_quesId);
        txtTimer = findViewById(R.id.txt_timer);
        txtCatName = findViewById(R.id.txt_qa_catName);
        btnSubmit = findViewById(R.id.btn_submitTest);
        btnMark = findViewById(R.id.btn_mark);
        btnClearSel = findViewById(R.id.btn_clear_sel);
        btnPrevQues = findViewById(R.id.btn_prev_ques);
        btnNextQues = findViewById(R.id.btn_next_ques);
        btnQuesList = findViewById(R.id.btn_ques_list_grid);
        drawerLayout = findViewById(R.id.drawer_layout123);
        drawerCloseB = findViewById(R.id.drawer_close);
        markImage = findViewById(R.id.mark_image);
        quesListGV = findViewById(R.id.ques_list_gv);
        btnBookMark = findViewById(R.id.btn_qa_bookmark);
        quesId = 0;
        txtQuesID.setText("1/"+String.valueOf(DbQuery.g_quesList.size()));
        txtCatName.setText(DbQuery.g_catList.get(DbQuery.g_selected_cat_index).getName());

        DbQuery.g_quesList.get(0).setStatus(DbQuery.UNANSWERED);

        if(DbQuery.g_quesList.get(0).isBookmarked()){
            btnBookMark.setImageResource(R.drawable.ic_bookmark_24);
        }else{
            btnBookMark.setImageResource(R.drawable.twotone_bookmark_24);
        }
    }


}