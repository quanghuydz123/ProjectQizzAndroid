package com.example.projectqizz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.QuestionModel;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.QuestionsActivity;
import com.example.projectqizz.R;
import com.example.projectqizz.ScoreActivity;
import com.example.projectqizz.TestActivity;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>
        //RecyclerView Adapter của danh sách câu hỏi mà người dùng đã lưu trong lúc làm bài
{
    private List<QuestionModel> questionModelList;
    private List<String> size;
    private Context context;
    //khai báo constructor BookmarkAdapter
    public BookmarkAdapter(List<QuestionModel> questionModelList,List<String> size) {
        this.questionModelList = questionModelList;
        this.size = size;
    }

    @NonNull
    @Override//Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item_layout,parent,false);//tạo view
        return new BookmarkAdapter.ViewHolder(view);
    }

    @Override////Gán dữ liệu từ questionModelList vào ViewHolder.
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, int position) {
        String qId = questionModelList.get(position).getqID();
        String ques = questionModelList.get(position).getQuestion();
        String A = questionModelList.get(position).getOptionA();
        String B = questionModelList.get(position).getOptionB();
        String C = questionModelList.get(position).getOptionC();
        String D = questionModelList.get(position).getOptionD();
//        int selected = questionModelList.get(position).getSelectedAns();
        int result = questionModelList.get(position).getCorrectAns();
        //Gọi hàm setData của ViewHolder để cập nhật dữ liệu cho view
        holder.setData(position,ques,A,B,C,D,result,qId);

    }

    @Override//Phương thức này trả về số lượng phần tử trong questionModelList tức là size truyền vào
    public int getItemCount() {
        return size.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
     //ViewHolder chứa các thành phần giao diện của mỗi item trong danh sách, được ánh xạ từ bookmarks_item_layout
    {
        private TextView quesNo, question, optionA, optionB, optionC, optionD, result;
        private Button btnCancelBm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quesNo = itemView.findViewById(R.id.txt_quesNo);
            question = itemView.findViewById(R.id.question);
            optionA = itemView.findViewById(R.id.txt_option_A);
            optionB = itemView.findViewById(R.id.txt_option_B);
            optionC = itemView.findViewById(R.id.txt_option_C);
            optionD = itemView.findViewById(R.id.txt_option_D);
            result = itemView.findViewById(R.id.txtResult);
            btnCancelBm = itemView.findViewById(R.id.btn_cancel_bm);
        }

        private void setData(int pos, String ques, String A, String B, String C, String D, int correctAns,String qId)
        //Cập nhật dữ liệu và giao diện của một item dựa trên thông tin được truyền vào.
        {
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
            btnCancelBm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCancelBm(pos,qId);
                }
            });

        }

        private void handleCancelBm(int pos,String qId)
         //Hàm xử lý khi người dùng muốn xóa câu hỏi đã lưu trong danh sách
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());//xây dựng ra 1 thông báo
            builder.setCancelable(true);
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.alert_dialog_layuot, null);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
            TextView title = view.findViewById(R.id.content);
            title.setText("Bạn có muốn hủy lưu câu hỏi này ?");
            builder.setView(view);
            AlertDialog alertDialog = builder.create();//tạo ra thông báo
            btn_cancel.setOnClickListener(new View.OnClickListener()
            {//xử lý khi người dùng ấn "No"
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            btn_cofirm.setOnClickListener(new View.OnClickListener()
            {//xử lý khi người dùng ấn "Yes"
                @Override
                public void onClick(View v) {
                    if(DbQuery.g_bmIdList.contains(qId)){
                        DbQuery.g_bmIdList.remove(qId);
                        DbQuery.myProfile.setBookMarksCount(DbQuery.g_bmIdList.size());
                    }
                    DbQuery.cancelBookmarks(new MyCompleteListener() {
                        @Override
                        public void onSucces() {
                            alertDialog.dismiss();
                            notifyItemRemoved(pos);
                            Toast.makeText(itemView.getContext(),"Đã hủy",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            alertDialog.dismiss();
                            Toast.makeText(itemView.getContext(),"Hủy thất bại",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            alertDialog.show();//Hiện khi ra thông báo
        }
    }


}
