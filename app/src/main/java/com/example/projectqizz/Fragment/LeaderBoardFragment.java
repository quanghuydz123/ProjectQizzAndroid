package com.example.projectqizz.Fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.Adapter.RankAdapter;
import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.MainActivity;
import com.example.projectqizz.MyCompleteListener;
import com.example.projectqizz.R;
import com.example.projectqizz.TestActivity;

import org.w3c.dom.Text;


public class LeaderBoardFragment extends Fragment {
    private TextView txtTotalUser,txtMyImageText,txtMyScore,txtMyRank;
    private RecyclerView usersView;
    private RankAdapter rankAdapter;
    private Dialog progressDialog;
    private TextView dialogText;
    public LeaderBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment - Bành Viết Hùng
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Xếp hạng");
        View view =  inflater.inflate(R.layout.fragment_leader_board, container, false);


        init(view);

        progressDialog  = new Dialog(getContext());//set vòng quay quay
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.txtdialog);
        dialogText.setText("Loading...");
        progressDialog.show();

        //cấu hình RecyclerView - Bành Viết Hùng
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        usersView.setLayoutManager(linearLayoutManager);
        //set adapter cho usersView - Bành Viết Hùng
        rankAdapter = new RankAdapter(DbQuery.g_usersList);
        usersView.setAdapter(rankAdapter);
        //gọi hàm xử lý lấy danh sách người dùng xếp từ cao đến thấp - Bành Viết Hùng
        DbQuery.getTopUsers(new MyCompleteListener() {
            @Override
            public void onSucces() {
                //Phương thức này sẽ cập nhập lại giá trị rankAdapter - Bành Viết Hùng
                rankAdapter.notifyDataSetChanged();

                if(DbQuery.myPerformance.getScore() != 0 ){
                    if(!DbQuery.isMeOnTopList){
                        calculateRank();
                    }
                    txtMyScore.setText("Điểm số: " + DbQuery.myPerformance.getScore());
                    txtMyRank.setText("Hạng:" +DbQuery.myPerformance.getRank());

                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getContext(),"Tải thất bại",
                        Toast.LENGTH_SHORT).show();
            }
        });
        txtTotalUser.setText("Total Users: " + DbQuery.g_usersCount);
        String[] parts = DbQuery.myPerformance.getName().split(" ");
        String lastName = parts[parts.length - 1]; // Lấy từ cuối cùng trong tên
        String firstLetter = lastName.toUpperCase().substring(0, 1);
        txtMyImageText.setText(firstLetter);
        return view;
    }

    private void calculateRank() {
        //Hàm này tính toán và đặt thứ hạng cho người dùng - Bành Viết Hùng
        int lowTopScore = DbQuery.g_usersList.get(DbQuery.g_usersList.size()-1).getScore();
        int remaining_slots = DbQuery.g_usersCount - 100;
        int mySlot = (DbQuery.myPerformance.getScore()*remaining_slots)/lowTopScore;
        int rank;

        if(lowTopScore != DbQuery.myPerformance.getScore()){
            rank = DbQuery.g_usersCount - mySlot;
        }else{
            rank = 101;
        }
        DbQuery.myPerformance.setRank(rank);
    }

    private void init(View view){
        //Đoạn mã này tìm và gán các thành phần giao diện từ tệp XML layout vào các biến Java - Bành Viết Hùng
        txtTotalUser = view.findViewById(R.id.txt_total_user);
        txtMyImageText = view.findViewById(R.id.txt_image_leader);
        txtMyScore = view.findViewById(R.id.txt_totalScore_leader);
        txtMyRank = view.findViewById(R.id.txt_rank_leader);
        usersView = view.findViewById(R.id.users_view_leader);
    }
}