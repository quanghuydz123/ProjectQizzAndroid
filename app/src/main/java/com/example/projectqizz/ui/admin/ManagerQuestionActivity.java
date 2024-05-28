package com.example.projectqizz.ui.admin;

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

import com.example.projectqizz.Adapter.QuestionManagerAdapter;
import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.R;
import com.example.projectqizz.StartTestActivity;

public class ManagerQuestionActivity extends AppCompatActivity {
    private RecyclerView questionView;
    private Toolbar toolbar;
    private Dialog progressDialog;
    private TextView dialogText;
    private Button btnCreateQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_question);
        btnCreateQuestion = findViewById(R.id.btn_create_question);
        questionView = findViewById(R.id.bm_recyler_view);
        //Khỏi tạo tool bar - Nguyễn Quang Huy
        toolbar = findViewById(R.id.bm_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Quản lý câu hỏi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiện thị thanh quay lại

        progressDialog  = new Dialog(ManagerQuestionActivity.this);//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");
        progressDialog.show();
        if(DbQuery.myProfile.getAdmin() == true){
            btnCreateQuestion.setVisibility(View.VISIBLE);
        }


        //cấu hình recylerView - Nguyễn Quang Huy
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        questionView.setLayoutManager(layoutManager);

        //set adpater cho questionView - Nguyễn Quang Huy
        QuestionManagerAdapter adapter = new QuestionManagerAdapter(DbQuery.g_quesList);
        questionView.setAdapter(adapter);
        progressDialog.dismiss();

        btnCreateQuestion.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng ấn vào nút "Thêm câu hỏi" - Nguyễn Quang Huy
            @Override
            public void onClick(View v) {
                createQuestion(adapter);
            }
        });
    }

    private void createQuestion(QuestionManagerAdapter adapter)
    {//Hàm xử lý thêm câu hỏi - Nguyễn Quang Huy
        AlertDialog.Builder builder = new AlertDialog.Builder(ManagerQuestionActivity.this);//xây dựng ra 1 thông báo
        builder.setCancelable(true);
        View view = LayoutInflater.from(ManagerQuestionActivity.this).inflate(R.layout.form_create_question, null);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
        EditText edtNameQues,edtA,edtB,edtC,edtD,edtAnswer;
        edtNameQues = view.findViewById(R.id.edt_name_question);
        edtA = view.findViewById(R.id.edt_A);
        edtB = view.findViewById(R.id.edt_B);
        edtC = view.findViewById(R.id.edt_C);
        edtD = view.findViewById(R.id.edt_D);
        edtAnswer = view.findViewById(R.id.edt_answer);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();//tạo ra thông báo
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng ấn nút "Hủy" trong thông báo
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_cofirm.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng ấn nút "Thêm" trong thông báo
            @Override
            public void onClick(View v) {
                if(validate()){
                    //Gọi hàm xử lý để thêm thông tin câu hỏi xuống database
                    DbQuery.createQuestion(edtNameQues.getText().toString(), edtA.getText().toString(), edtB.getText().toString(), edtC.getText().toString(),
                            edtD.getText().toString(), Integer.parseInt(edtAnswer.getText().toString()), new MyCompleteListener() {
                                @Override
                                public void onSucces() {
                                    alertDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(ManagerQuestionActivity.this,"Thêm thành công",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure() {
                                    alertDialog.dismiss();
                                    Toast.makeText(ManagerQuestionActivity.this,"Lỗi rồi",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            private boolean validate()
            {//Kiểm tra dữ liệu người dùng nhập vào có bỏ trống không - Nguyễn Quang Huy
                if(edtNameQues.getText().toString().isEmpty()){
                    edtNameQues.setError("Hãy nhập câu hỏi !!");
                    return false;
                }
                if(edtA.getText().toString().isEmpty()){
                    edtA.setError("Hãy nhập câu trả lời A !!");
                    return false;
                }
                if(edtB.getText().toString().isEmpty()){
                    edtB.setError("Hãy nhập câu trả lời B !!");
                    return false;
                }
                if(edtC.getText().toString().isEmpty()){
                    edtC.setError("Hãy nhập câu trả lời C !!");
                    return false;
                }
                if(edtD.getText().toString().isEmpty()){
                    edtD.setError("Hãy nhập câu trả lời D !!");
                    return false;
                }
                if(edtAnswer.getText().toString().isEmpty()){
                    edtAnswer.setError("Hãy nhập đán án đúng cho câu trả lời !!");
                }
                return true;
            }
        });

        alertDialog.show();//Hiện thị ra dialog để người dùng nhập tin câu hỏi cần thêm
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {//Ghi dè hàm này xử lý khi người click vào icon "<--" trên toolbar (để quay lại giao diện trước đó) - Nguyễn Quang Huy
        if(item.getItemId() == android.R.id.home){
            ManagerQuestionActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}