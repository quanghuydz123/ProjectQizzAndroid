package com.example.projectqizz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.TestModel;
import com.example.projectqizz.QuestionsActivity;
import com.example.projectqizz.R;
import com.example.projectqizz.StartTestActivity;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private List<TestModel> testModelList;

    public TestAdapter(List<TestModel> testModelList) {
        this.testModelList = testModelList;
    }

    @NonNull
    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_layuot,parent,false);//tạo view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapter.ViewHolder holder, int position)
    {
        int progress = testModelList.get(position).getTopScore();
        holder.setData(position,progress);
    }

    @Override
    public int getItemCount() {
        return testModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView testNo;
        private TextView topSroce;
        private ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //lay id
            testNo = itemView.findViewById(R.id.txtTestNo);
            topSroce = itemView.findViewById(R.id.txtscore);
            progressBar = itemView.findViewById(R.id.proTestProgressbar);

        }
        public void setData(int pos,int progress)
        {
            //set value cho item view
            testNo.setText(DbQuery.g_testList.get(pos).getName());
            topSroce.setText(String.valueOf(progress)+"%");
            progressBar.setProgress(progress);

            // xử lý click từng view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbQuery.g_selected_test_index = pos;
                    Intent intent = new Intent(itemView.getContext(), StartTestActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
