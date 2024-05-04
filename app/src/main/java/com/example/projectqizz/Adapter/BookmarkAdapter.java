package com.example.projectqizz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.Model.QuestionModel;
import com.example.projectqizz.R;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private List<QuestionModel> questionModelList;

    public BookmarkAdapter(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item_layout,parent,false);//tạo view
        return new BookmarkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, int position) {
        String ques = questionModelList.get(position).getQuestion();
        String A = questionModelList.get(position).getOptionA();
        String B = questionModelList.get(position).getOptionB();
        String C = questionModelList.get(position).getOptionC();
        String D = questionModelList.get(position).getOptionD();
//        int selected = questionModelList.get(position).getSelectedAns();
        int result = questionModelList.get(position).getCorrectAns();
        holder.setData(position,ques,A,B,C,D,result);

    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quesNo, question, optionA, optionB, optionC, optionD, result;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quesNo = itemView.findViewById(R.id.txt_quesNo);
            question = itemView.findViewById(R.id.question);
            optionA = itemView.findViewById(R.id.txt_option_A);
            optionB = itemView.findViewById(R.id.txt_option_B);
            optionC = itemView.findViewById(R.id.txt_option_C);
            optionD = itemView.findViewById(R.id.txt_option_D);
            result = itemView.findViewById(R.id.txtResult);

        }

        private void setData(int pos, String ques, String A, String B, String C, String D, int correctAns) {
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

        }
    }


}
