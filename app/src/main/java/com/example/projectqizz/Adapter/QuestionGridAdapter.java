package com.example.projectqizz.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.QuestionsActivity;
import com.example.projectqizz.R;

public class QuestionGridAdapter extends BaseAdapter {
    private int numOfQues;
    private Context context;

    public QuestionGridAdapter(Context context,int numOfQues) {
        this.numOfQues = numOfQues; this.context=context;
    }

    @Override
    public int getCount() {
        return numOfQues;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View myView;
        if(convertView == null){
            myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ques_gird_item_layout,parent,false);
        }else{
            myView=convertView;
        }

//        myView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(context instanceof QuestionsActivity)
//                    ((QuestionsActivity)context)
//            }
//        });

        TextView quesTV = myView.findViewById(R.id.txt_ques_num);
        quesTV.setText(String.valueOf(position + 1));
        switch (DbQuery.g_quesList.get(position).getStatus()){
            case DbQuery.NOT_VISITED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.grey)));
                break;
            case DbQuery.UNANSWERED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.red)));
                break;
            case DbQuery.ANSWERED:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.green)));
                break;
            case DbQuery.REVIEW:
                quesTV.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(myView.getContext(),R.color.pink)));
                break;
            default:
                break;

        }
        return myView;
    }
}
