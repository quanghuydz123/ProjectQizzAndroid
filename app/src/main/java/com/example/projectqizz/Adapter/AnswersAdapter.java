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

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder>
    //Tạo một RecyclerView Adapter cho danh sách câu trả lời khi người dùng làm bài xong
{
    private List<QuestionModel> questionModelList;
    //khai báo constructor AnswersAdapter
    public AnswersAdapter(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }
    @NonNull
    @Override//Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới
    public AnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item_layout,parent,false);//tạo view
        return new AnswersAdapter.ViewHolder(view);
    }

    @Override//Gán dữ liệu từ questionModelList vào ViewHolder
    public void onBindViewHolder(@NonNull AnswersAdapter.ViewHolder holder, int position) {
        String ques = questionModelList.get(position).getQuestion();
        String A = questionModelList.get(position).getOptionA();
        String B = questionModelList.get(position).getOptionB();
        String C = questionModelList.get(position).getOptionC();
        String D = questionModelList.get(position).getOptionD();
        int selected = questionModelList.get(position).getSelectedAns();
        int result = questionModelList.get(position).getCorrectAns();
        //Gọi hàm setData của ViewHolder để cập nhật dữ liệu cho view
        holder.setData(position,ques,A,B,C,D,selected,result);

    }

    @Override//Phương thức này trả về số lượng phần tử trong questionModelList tức là số lượng câu hỏi
    public int getItemCount() {
        return questionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
     //ViewHolder chứa các thành phần giao diện của mỗi item trong danh sách, được ánh xạ từ answer_item_layout
    {
        private TextView quesNo,question,optionA,optionB,optionC,optionD,result;
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

        private void setData(int pos,String ques,String A,String B,String C,String D,int selected,int correctAns)
        //Cập nhật dữ liệu và giao diện của một item dựa trên thông tin được truyền vào.
        {
            quesNo.setText("Câu hổi số: " + String.valueOf(pos+1));
            question.setText(ques);
            optionA.setText("A. "+A);
            optionB.setText("B. "+B);
            optionC.setText("C. "+C);
            optionD.setText("D. "+D);

            if (selected == -1){
                result.setText("Không trả lời");
                result.setTextColor(itemView.getContext().getResources().getColor(R.color.black));

                setOptionColor(selected,R.color.text_normal);

            }else{
                if(selected == correctAns){
                    result.setText("Đúng");
                    result.setTextColor(itemView.getContext().getResources().getColor(R.color.green));
                    setOptionColor(selected,R.color.green);
                }else{
                    result.setText("Sai");
                    result.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                    setOptionColor(selected,R.color.red);

                }
            }

        }
        private void setOptionColor(int selected, int color)
        //Đổi màu sắc chữ dựa trên lựa chọn của người dùng (đúng xanh,sai đỏ)
        {
            if(selected == 1){
                optionA.setTextColor(itemView.getContext().getResources().getColor(color));
            }else{
                optionA.setTextColor(itemView.getContext().getResources().getColor(R.color.text_normal));
            }

            if(selected == 2){
                optionB.setTextColor(itemView.getContext().getResources().getColor(color));
            }else{
                optionB.setTextColor(itemView.getContext().getResources().getColor(R.color.text_normal));
            }

            if(selected == 3){
                optionC.setTextColor(itemView.getContext().getResources().getColor(color));
            }else{
                optionC.setTextColor(itemView.getContext().getResources().getColor(R.color.text_normal));
            }

            if(selected == 4){
                optionD.setTextColor(itemView.getContext().getResources().getColor(color));
            }else{
                optionD.setTextColor(itemView.getContext().getResources().getColor(R.color.text_normal));
            }
        }
    }


}
