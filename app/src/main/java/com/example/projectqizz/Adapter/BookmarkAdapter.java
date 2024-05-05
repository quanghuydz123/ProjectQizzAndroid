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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private List<QuestionModel> questionModelList;
    private List<String> size;
    private Context context;

    public BookmarkAdapter(List<QuestionModel> questionModelList,List<String> size) {
        this.questionModelList = questionModelList;
        this.size = size;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item_layout,parent,false);//tạo view
        return new BookmarkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, int position) {
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
        return size.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

        private void setData(int pos, String ques, String A, String B, String C, String D, int correctAns,String qId) {
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

        private void handleCancelBm(int pos,String qId) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());//xây dựng ra 1 thông báo
            builder.setCancelable(true);
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.alert_dialog_layuot, null);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
            TextView title = view.findViewById(R.id.content);
            title.setText("Bạn có muốn hủy lưu câu hỏi này ?");
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
            alertDialog.show();
        }
    }


}
