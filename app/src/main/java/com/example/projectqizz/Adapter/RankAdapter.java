package com.example.projectqizz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Model.RankModel;
import com.example.projectqizz.R;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder>
        //Tạo một RecyclerView Adapter cho danh sách xếp hạng người dùng
{

    private List<RankModel> userList;
    //khai báo constructor AnswersAdapter
    public RankAdapter(List<RankModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override//Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới
    public RankAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override//Gán dữ liệu từ questionModelList vào ViewHolder
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int position) {
        String name = userList.get(position).getName();
        int score = userList.get(position).getScore();
        int rank = userList.get(position).getRank();
        //Gọi hàm setData của ViewHolder để cập nhật dữ liệu cho view
        holder.setData(name,score,rank);//gọi hàm setdata
    }

    @Override
    public int getItemCount()
    {//Hàm này trả về số lượng người dùng giới hạn chỉ hiện 100 tối đa người dùng trên bảng xếp hàng
        if(userList.size() > 100){
            return 100;
        }else{
            return userList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {//ViewHolder chứa các thành phần giao diện của mỗi item trong danh sách, được ánh xạ từ rank_item_layout
        private TextView txt_name,txt_rank,txt_score,txt_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.name_item_rank);
            txt_rank = itemView.findViewById(R.id.txt_item_rank);
            txt_score = itemView.findViewById(R.id.txt_score_rank);
            txt_image = itemView.findViewById(R.id.txt_image_item_rank);

        }

        private void setData(String name,int score,int rank)
        {//Cập nhật dữ liệu và giao diện của một item dựa trên thông tin được truyền vào.
            txt_name.setText(name);
            txt_score.setText("Điểm số: " + score);
            txt_rank.setText("Hạng: "+rank);
            String[] parts = name.split(" ");
            String lastName = parts[parts.length - 1]; // Lấy từ cuối cùng trong tên
            String firstLetter = lastName.toUpperCase().substring(0, 1);
            txt_image.setText(firstLetter);

        }
    }
}
