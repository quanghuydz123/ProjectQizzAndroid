package com.example.projectqizz.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.LoginActivity;
import com.example.projectqizz.MainActivity;
import com.example.projectqizz.MyProfileActivity;
import com.example.projectqizz.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class AccountFragment extends Fragment {

    private LinearLayout btnLogout;
    private TextView txt_image_profile,txt_name,txt_score,txt_rank;
    private LinearLayout btn_leader,btn_profile,btn_bookmark;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);
        init(view);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("My Account");
        String profileName = DbQuery.myProfile.getName();
        String[] parts = profileName.split(" ");
        String lastName = parts[parts.length - 1]; // Lấy từ cuối cùng trong tên
        String firstLetter = lastName.toUpperCase().substring(0, 1);
        txt_image_profile.setText(firstLetter);
        txt_name.setText(profileName);
        txt_score.setText(String.valueOf(DbQuery.myPerformance.getScore()));
        //xử lý đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(),gso);
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa các hoạt động trên cùng của ngăn xếp và tạo một nhiệm vụ mới
                        startActivity(intent); // Bắt đầu hoạt động đăng nhập
                        getActivity().finish();
                    }
                });
            }
        });

        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyProfileActivity.class);
                startActivity(intent);
            }
        });
        btn_leader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.nav_leaderboard_buttonMenu);
                //setFragement(new LeaderBoardFragment());
            }
        });
        return view;
    }
//    private void setFragement(Fragment categoryFragment) {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(main_frame.getId(),categoryFragment);//main_frame là frame thay thế
//        fragmentTransaction.commit();
//    }
    public void init(View view){
        btnLogout = view.findViewById(R.id.btn_logout);
        txt_image_profile = view.findViewById(R.id.txt_image_profile);
        txt_name = view.findViewById(R.id.txt_name_account);
        txt_score = view.findViewById(R.id.txt_totalScore);
        txt_rank = view.findViewById(R.id.txt_rank);
        btn_profile = view.findViewById(R.id.btn_Profile);
        btn_bookmark = view.findViewById(R.id.btn_bookMarkAccount);
        btn_leader = view.findViewById(R.id.btn_leaderAccount);
        bottomNavigationView = getActivity().findViewById(R.id.botton_nav_bar);
    }
}