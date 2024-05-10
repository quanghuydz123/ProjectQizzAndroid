package com.example.projectqizz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.CategoryModel;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.R;
import com.example.projectqizz.TestActivity;

import org.w3c.dom.Text;

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
        Button btnUpdateCategory = myView.findViewById(R.id.btn_update_category);
        if(DbQuery.myProfile.getAdmin() == true){
            btnUpdateCategory.setVisibility(View.VISIBLE);
        }

        category_name.setText(categoryModelList.get(position).getName());
        noOfTests.setText(String.valueOf(categoryModelList.get(position).getNoOfTests())+" Tests"); // ép kiểu string

        btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategory(myView,position);
            }
        });


        return myView;
    }

    private void updateCategory(View myView,int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());//xây dựng ra 1 thông báo
        builder.setCancelable(true);
        View view = LayoutInflater.from(myView.getContext()).inflate(R.layout.form_update_category, null);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
        EditText edtNameCategory = view.findViewById(R.id.edt_name_category);
        TextView txtNoTest = view.findViewById(R.id.txt_no_Test);
        edtNameCategory.setText(DbQuery.g_catList.get(position).getName().toString());
        txtNoTest.setText(String.valueOf(DbQuery.g_catList.get(position).getNoOfTests()));
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
                DbQuery.updateCategory(position, edtNameCategory.getText().toString(), new MyCompleteListener() {
                    @Override
                    public void onSucces() {
                        alertDialog.dismiss();
                        notifyDataSetChanged();
                        Toast.makeText(myView.getContext(),"Cập nhập thành công",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        alertDialog.dismiss();
                        Toast.makeText(myView.getContext(),"Lỗi rồi",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertDialog.show();
    }

}
