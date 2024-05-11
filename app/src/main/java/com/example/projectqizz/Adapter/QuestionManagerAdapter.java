package com.example.projectqizz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.QuestionModel;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.R;

import java.util.List;

public class QuestionManagerAdapter extends RecyclerView.Adapter<QuestionManagerAdapter.ViewHolder>{
    private List<QuestionModel> questionModelList;

    public QuestionManagerAdapter(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }

    @NonNull
    @Override
    public QuestionManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_question_item_layout,parent,false);//tạo view
        return new QuestionManagerAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull QuestionManagerAdapter.ViewHolder holder, int position) {
        String qId = questionModelList.get(position).getqID();
        String ques = questionModelList.get(position).getQuestion();
        String A = questionModelList.get(position).getOptionA();
        String B = questionModelList.get(position).getOptionB();
        String C = questionModelList.get(position).getOptionC();
        String D = questionModelList.get(position).getOptionD();
//        int selected = questionModelList.get(position).getSelectedAns();
        int result = questionModelList.get(position).getCorrectAns();
        holder.setData(position,ques,A,B,C,D,result,qId);

    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quesNo, question, optionA, optionB, optionC, optionD, result;
        private Button btnUpdateQuestion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quesNo = itemView.findViewById(R.id.txt_quesNo);
            question = itemView.findViewById(R.id.question);
            optionA = itemView.findViewById(R.id.txt_option_A);
            optionB = itemView.findViewById(R.id.txt_option_B);
            optionC = itemView.findViewById(R.id.txt_option_C);
            optionD = itemView.findViewById(R.id.txt_option_D);
            result = itemView.findViewById(R.id.txtResult);
            btnUpdateQuestion = itemView.findViewById(R.id.btn_update_question);
        }

        private void setData(int pos, String ques, String A, String B, String C, String D, int correctAns,String qId) {
            if(DbQuery.myProfile.getAdmin() == true){
                btnUpdateQuestion.setVisibility(View.VISIBLE);
            }
            quesNo.setText("Câu hổi số: " + String.valueOf(pos + 1));
            question.setText(ques);
            optionA.setText("A. " + A);
            optionB.setText("B. " + B);
            optionC.setText("C. " + C);
            optionD.setText("D. " + D);
            if(correctAns == 1 ){
                result.setText("Câu trả lời: "+A);
            }else if(correctAns == 2 ){
                result.setText("Câu trả lời: "+B);
            }else if(correctAns == 3 ){
                result.setText("Câu trả lời: "+C);
            }else if(correctAns == 4 ){
                result.setText("Câu trả lời: "+D);
            }
            btnUpdateQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCancelBm(pos,qId);
                }
            });

        }

        private void handleCancelBm(int pos,String qId) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());//xây dựng ra 1 thông báo
            builder.setCancelable(true);
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.form_update_question, null);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
            EditText edtNameQues,edtA,edtB,edtC,edtD,edtAnswer;
            edtNameQues = view.findViewById(R.id.edt_name_question);
            edtA = view.findViewById(R.id.edt_A);
            edtB = view.findViewById(R.id.edt_B);
            edtC = view.findViewById(R.id.edt_C);
            edtD = view.findViewById(R.id.edt_D);
            edtAnswer = view.findViewById(R.id.edt_answer);
            edtNameQues.setText(questionModelList.get(pos).getQuestion().toString());
            edtA.setText(questionModelList.get(pos).getOptionA().toString());
            edtB.setText(questionModelList.get(pos).getOptionB().toString());
            edtC.setText(questionModelList.get(pos).getOptionC().toString());
            edtD.setText(questionModelList.get(pos).getOptionD().toString());
            edtAnswer.setText(String.valueOf(questionModelList.get(pos).getCorrectAns()));
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
                    DbQuery.updateQuestion(pos, edtNameQues.getText().toString(), edtA.getText().toString(), edtB.getText().toString(), edtC.getText().toString(),
                            edtD.getText().toString(), Integer.parseInt(edtAnswer.getText().toString()), new MyCompleteListener() {
                                @Override
                                public void onSucces() {
                                    alertDialog.dismiss();
                                    notifyDataSetChanged();
                                    Toast.makeText(itemView.getContext(),"Cập nhập thành công",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure() {
                                    alertDialog.dismiss();
                                    Toast.makeText(itemView.getContext(),"Lỗi rồi",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
            alertDialog.show();
        }
    }


}
