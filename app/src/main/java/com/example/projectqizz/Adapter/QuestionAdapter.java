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

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>
        //Tạo một RecyclerView Adapter cho danh sách câu hỏi khi vào làm bài kiểm tra - Nguyễn Quang Huy
{
    private List<QuestionModel> questionModelList;
    //khai báo constructor QuestionAdapter - Nguyễn Quang Huy

    public QuestionAdapter(List<QuestionModel> questionModelList) {
        this.questionModelList = questionModelList;
    }

    @NonNull
    @Override//Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới - Nguyễn Quang Huy
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_layuot,parent,false);//tạo view
        return new ViewHolder(view);
    }

    @Override//Gán dữ liệu từ questionModelList vào ViewHolder - Nguyễn Quang Huy
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Gọi hàm setData của ViewHolder để cập nhật dữ liệu cho view
        holder.setData(position);

    }

    @Override//Phương thức này trả về số lượng phần tử trong questionModelList tức là số lượng câu hỏi - Nguyễn Quang Huy
    public int getItemCount() {
        return questionModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {//ViewHolder chứa các thành phần giao diện của mỗi item trong danh sách, được ánh xạ từ question_item_layuot - Nguyễn Quang Huy
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

        public void setData(final int pos)
        { //Cập nhật dữ liệu và giao diện của một item dựa trên thông tin được truyền vào. - Nguyễn Quang Huy
            ques.setText(questionModelList.get(pos).getQuestion());
            optionA.setText(questionModelList.get(pos).getOptionA());
            optionB.setText(questionModelList.get(pos).getOptionB());
            optionC.setText(questionModelList.get(pos).getOptionC());
            optionD.setText(questionModelList.get(pos).getOptionD());

            setOptions(optionA,1,pos);
            setOptions(optionB,2,pos);
            setOptions(optionC,3,pos);
            setOptions(optionD,4,pos);

            optionA.setOnClickListener(new View.OnClickListener()
            {//Xử lý khi người dùng đáp án A
                @Override
                public void onClick(View v) {
                    selectOption(optionA,1,pos);
                }
            });

            optionB.setOnClickListener(new View.OnClickListener()
            {//Xử lý khi người dùng đáp án B
                @Override
                public void onClick(View v) {
                    selectOption(optionB,2,pos);

                }
            });

            optionC.setOnClickListener(new View.OnClickListener()
            {//Xử lý khi người dùng đáp án C
                @Override
                public void onClick(View v) {
                    selectOption(optionC,3,pos);

                }
            });

            optionD.setOnClickListener(new View.OnClickListener()
            {//Xử lý khi người dùng đáp án D
                @Override
                public void onClick(View v) {
                    selectOption(optionD,4,pos);

                }
            });



        }

        private void setOptions(Button btn, int option_num, int quesId)
        {//Thiết lập background cho câu hỏi - Nguyễn Quang Huy
            if(DbQuery.g_quesList.get(quesId).getSelectedAns() == option_num){
                btn.setBackgroundResource(R.drawable.selected_btn);
            }else{
                btn.setBackgroundResource(R.drawable.unselected_btn);
                prevSelectedB=null;
            }
        }

        private void selectOption(Button btn, int option_num, int quesId)
        //Xử lý khi người dùng click vào 1 đán án bất kỳ - Nguyễn Quang Huy
        {
            if(prevSelectedB == null){//xử lý nếu như người dùng chưa chọn đáp án vào trước đó
                btn.setBackgroundResource(R.drawable.selected_btn);
                DbQuery.g_quesList.get(quesId).setSelectedAns(option_num);

                changeStatus(quesId,DbQuery.ANSWERED);//thay đổi trạng thái câu trả lời
                prevSelectedB = btn;
            }else{//Xử lý nếu như người dùng dã chọn 1 đáp án trước đó
                if(prevSelectedB.getId() == btn.getId()){//Xử lý nếu như đáp án người dùng vừa chọn giống với đáp án trước đó
                    btn.setBackgroundResource(R.drawable.unselected_btn);
                    DbQuery.g_quesList.get(quesId).setSelectedAns(-1);
                    changeStatus(quesId,DbQuery.UNANSWERED);//thay đổi trạng thái câu trả lời

                    prevSelectedB = null;
                }else{//Xử lý nếu như đáp án người dùng chọn khác với đáp án trước đó
                    prevSelectedB.setBackgroundResource(R.drawable.unselected_btn);
                    btn.setBackgroundResource(R.drawable.selected_btn);

                    changeStatus(quesId,DbQuery.ANSWERED);//thay đổi trạng thái câu trả lời
                    DbQuery.g_quesList.get(quesId).setSelectedAns(option_num);
                    prevSelectedB = btn;
                }
            }
        }

        private void changeStatus(int quesId, int answered)
        {//Cập nhật trang thái câu hỏi (Đã trả lời hoặc chưa trả lời) - Nguyễn Quang Huy
            if (DbQuery.g_quesList.get(quesId).getStatus() != DbQuery.REVIEW){
                DbQuery.g_quesList.get(quesId).setStatus(answered);
            }
        }
    }


}
