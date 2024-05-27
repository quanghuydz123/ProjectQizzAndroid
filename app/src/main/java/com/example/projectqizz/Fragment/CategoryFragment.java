package com.example.projectqizz.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.projectqizz.Adapter.CategoryAdapter;
import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.MainActivity;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.R;


public class CategoryFragment extends Fragment {

    private GridView categoryView;
    private Button btnCreateCategory;

    public CategoryFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Danh mục");
        categoryView = view.findViewById(R.id.category_Grid);
        btnCreateCategory = view.findViewById(R.id.btn_create_category);
        if(DbQuery.myProfile.getAdmin() == true){
            btnCreateCategory.setVisibility(View.VISIBLE);
        }
        CategoryAdapter categoryAdapter = new CategoryAdapter(DbQuery.g_catList);
        categoryAdapter.notifyDataSetChanged();
        categoryView.setAdapter(categoryAdapter);

        btnCreateCategory.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Thêm danh mục"
            @Override
            public void onClick(View v) {
                createCategory(categoryAdapter);
            }
        });
        return view;
    }

    private void createCategory(CategoryAdapter categoryAdapter)
    {//Hàm xử lý thêm danh mục
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());//xây dựng ra 1 thông báo
        builder.setCancelable(true);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.form_create_category, null);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
        EditText edtNameCategory = view.findViewById(R.id.edt_name_category);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();//tạo ra thông báo
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng ấn vào nút "Hủy"
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_cofirm.setOnClickListener(new View.OnClickListener()
        {//Xử lý khi người dùng click vào nút "Thêm"
            @Override
            public void onClick(View v) {
               if(validateData()){
                   //Gọi hàm xử lý thêm thông tin danh muc vào database
                   DbQuery.createCategory(edtNameCategory.getText().toString(), new MyCompleteListener()
                   {
                       @Override
                       public void onSucces() {
                           categoryAdapter.notifyDataSetChanged();
                           alertDialog.dismiss();
                           Toast.makeText(getContext(),"Thêm thành công",
                                   Toast.LENGTH_SHORT).show();
                       }

                       @Override
                       public void onFailure() {
                           alertDialog.dismiss();
                           Toast.makeText(getContext(),"Lỗi rồi",
                                   Toast.LENGTH_SHORT).show();
                       }
                   });
               }
                
            }

            private boolean validateData()
            {//Kiểm tra dữ liệu người dùng nhập vào có bỏ trống không
                if(edtNameCategory.getText().toString().isEmpty()){
                    edtNameCategory.setError("Hãy nhập tên danh mục !!");
                    return false;
                }
                return true;
            }
        });
        alertDialog.show();
    }



}