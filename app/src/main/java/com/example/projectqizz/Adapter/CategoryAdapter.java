package com.example.projectqizz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.CategoryModel;
import com.example.projectqizz.R;
import com.example.projectqizz.TestActivity;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @Override
    public int getCount() {
        return categoryModelList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView;
        if(convertView == null)
        {
            myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,parent,false);
        }else
        {
            myView = convertView;
        }

        //set click tung item
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbQuery.g_selected_cat_index = position;
                Intent intent = new Intent(v.getContext(), TestActivity.class);
                v.getContext().startActivity(intent);

            }
        });
        TextView category_name = myView.findViewById(R.id.category_name);
        TextView noOfTests = myView.findViewById(R.id.no_of_tests);

        category_name.setText(categoryModelList.get(position).getName());
        noOfTests.setText(String.valueOf(categoryModelList.get(position).getNoOfTests())+" Tests"); // ép kiểu string
        return myView;
    }

}
