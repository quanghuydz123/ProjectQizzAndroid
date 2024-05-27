package com.example.projectqizz.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.TestModel;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.QuestionsActivity;
import com.example.projectqizz.R;
import com.example.projectqizz.StartTestActivity;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder>
        //Tạo một RecyclerView Adapter cho danh sách các bài kiểm tra theo danh mục
{
    private List<TestModel> testModelList;
    //khai báo constructor TestAdapter
    public TestAdapter(List<TestModel> testModelList) {
        this.testModelList = testModelList;
    }

    @NonNull
    @Override//Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới
    public TestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item_layuot,parent,false);//tạo view
        return new ViewHolder(view);
    }

    @Override//Gán dữ liệu từ questionModelList vào ViewHolder
    public void onBindViewHolder(@NonNull TestAdapter.ViewHolder holder, int position)
    {
        int progress = testModelList.get(position).getTopScore();
        holder.setData(position,progress);
    }

    @Override//Phương thức này trả về số lượng phần tử trong testModelList tức là số lượng bài kiểm tra
    public int getItemCount() {
        return testModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            //ViewHolder chứa các thành phần giao diện của mỗi item trong danh sách, được ánh xạ từ test_item_layuot
    {
        private TextView testNo;
        private TextView topSroce;
        private ProgressBar progressBar;
        private Button btnUpdateTest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //lay id
            testNo = itemView.findViewById(R.id.txtTestNo);
            topSroce = itemView.findViewById(R.id.txtscore);
            progressBar = itemView.findViewById(R.id.proTestProgressbar);
            btnUpdateTest = itemView.findViewById(R.id.btn_update_test);

        }
        public void setData(int pos,int progress)
        //Cập nhật dữ liệu và giao diện của một item dựa trên thông tin được truyền vào.
        {
            //set value cho item view
            testNo.setText(DbQuery.g_testList.get(pos).getName());
            topSroce.setText(String.valueOf(progress)+"%");
            progressBar.setProgress(progress);
            if(DbQuery.myProfile.getAdmin() == true){
                btnUpdateTest.setVisibility(View.VISIBLE);
            }
            // xử lý click từng view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbQuery.g_selected_test_index = pos;
                    Intent intent = new Intent(itemView.getContext(), StartTestActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            btnUpdateTest.setOnClickListener(new View.OnClickListener()
            {//Xử lý khi người dùng click vào nút "Cập nhật" ở mỗi bài kiểm tra
                @Override
                public void onClick(View v) {
                    updateTest(pos);
                }
            });
        }

        private void updateTest(int pos)
        {//Xử lý cập nhập bài kiểm tra
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());//xây dựng ra 1 thông báo
            builder.setCancelable(true);
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.form_update_test, null);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Button btn_cofirm = view.findViewById(R.id.btn_comfirm);
            EditText edtName,edtTime;
            edtTime = view.findViewById(R.id.edt_time_test);
            edtName = view.findViewById(R.id.edt_name_test);
            edtName.setText(DbQuery.g_testList.get(pos).getName().toString());
            edtTime.setText(String.valueOf(DbQuery.g_testList.get(pos).getTime()));
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
            {//Xử lý khi người dùng ấn vào nút "Cập nhật"
                @Override
                public void onClick(View v) {
                    //Gọi hàm xử lý lưu thông tin cập nhật xuống database
                    DbQuery.updateTest(pos, edtName.getText().toString(), Integer.parseInt(edtTime.getText().toString()), new MyCompleteListener() {
                        @Override
                        public void onSucces() {
                            alertDialog.dismiss();
                            notifyDataSetChanged();
                            Toast.makeText(itemView.getContext(),"Cập nhập thành công",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            alertDialog.dismiss();
                            Toast.makeText(itemView.getContext(),"Lỗi rồi",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            alertDialog.show();//Hiện khi ra thông báo
        }
    }
}
