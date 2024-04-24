package com.example.projectqizz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.QuestionModel;
import com.example.projectqizz.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<QuestionModel> questionModelList;

    public QuestionAdapter(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_layuot,parent,false);//tạo view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);//gọi hàm setdata

    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ques;
        private Button optionA,optionB,optionC,optionD,prevSelectedB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //gắn id
            ques = itemView.findViewById(R.id.txt_question);
            optionA = itemView.findViewById(R.id.btn_optionA);
            optionB = itemView.findViewById(R.id.btn_optionB);
            optionC = itemView.findViewById(R.id.btn_optionC);
            optionD = itemView.findViewById(R.id.btn_optionD);
            prevSelectedB = null;

        }

        public void setData(final int pos){
            ques.setText(questionModelList.get(pos).getQuestion());
            optionA.setText(questionModelList.get(pos).getOptionA());
            optionB.setText(questionModelList.get(pos).getOptionB());
            optionC.setText(questionModelList.get(pos).getOptionC());
            optionD.setText(questionModelList.get(pos).getOptionD());

            setOptions(optionA,1,pos);
            setOptions(optionB,2,pos);
            setOptions(optionC,3,pos);
            setOptions(optionD,4,pos);

            optionA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionA,1,pos);
                }
            });

            optionB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionB,2,pos);

                }
            });

            optionC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionC,3,pos);

                }
            });

            optionD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(optionD,4,pos);

                }
            });



        }

        private void setOptions(Button btn, int option_num, int quesId) {
            if(DbQuery.g_quesList.get(quesId).getSelectedAns() == option_num){
                btn.setBackgroundResource(R.drawable.selected_btn);
            }else{
                btn.setBackgroundResource(R.drawable.unselected_btn);
                prevSelectedB=null;
            }
        }

        private void selectOption(Button btn, int option_num, int quesId) {
            if(prevSelectedB == null){
                btn.setBackgroundResource(R.drawable.selected_btn);
                DbQuery.g_quesList.get(quesId).setSelectedAns(option_num);

                changeStatus(quesId,DbQuery.ANSWERED);//thay đổi trạng thái câu trả lời
                prevSelectedB = btn;
            }else{
                if(prevSelectedB.getId() == btn.getId()){
                    btn.setBackgroundResource(R.drawable.unselected_btn);
                    DbQuery.g_quesList.get(quesId).setSelectedAns(-1);
                    changeStatus(quesId,DbQuery.UNANSWERED);//thay đổi trạng thái câu trả lời

                    prevSelectedB = null;
                }else{
                    prevSelectedB.setBackgroundResource(R.drawable.unselected_btn);
                    btn.setBackgroundResource(R.drawable.selected_btn);

                    changeStatus(quesId,DbQuery.ANSWERED);//thay đổi trạng thái câu trả lời
                    DbQuery.g_quesList.get(quesId).setSelectedAns(option_num);
                    prevSelectedB = btn;
                }
            }
        }

        private void changeStatus(int quesId, int answered) {
            if (DbQuery.g_quesList.get(quesId).getStatus() != DbQuery.REVIEW){
                DbQuery.g_quesList.get(quesId).setStatus(answered);
            }
        }
    }


}
