package com.example.projectqizz;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectqizz.DB.DbQuery;
import com.example.projectqizz.Fragment.AccountFragment;
import com.example.projectqizz.Fragment.CategoryFragment;
import com.example.projectqizz.Fragment.LeaderBoardFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectqizz.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private TextView drawerProfileName,drawerProfileText,drawerProfileEmail;

    private BottomNavigationView bottomNavigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FrameLayout main_frame;
    private  BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() //xử lý click botton navigation view
            {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if(id == R.id.nav_home_buttonMenu){
                setFragement(new CategoryFragment());
                return true;
            }else if(id == R.id.nav_leaderboard_buttonMenu){
                setFragement(new LeaderBoardFragment());
                return true;
            }else if(id == R.id.nav_account_buttonMenu){
                setFragement(new AccountFragment());
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Categories");

        bottomNavigationView = findViewById(R.id.botton_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);//gọi hàm để xứ lý

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.nav_dwar_open,R.string.nav_dwar_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Load thông tin người dùng
        loadInfoUser(navigationView);
        setFragement(new CategoryFragment());//giao diện hiện thi đầu tiên
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

    }

    private void loadInfoUser(NavigationView navigationView) {
        drawerProfileName = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_name);
        drawerProfileEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_email);
        drawerProfileText = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_text);
        String profileName = DbQuery.myProfile.getName();
        String email = DbQuery.myProfile.getEmail();
        drawerProfileName.setText(profileName);
        drawerProfileEmail.setText(email);
        String[] parts = profileName.split(" ");
        String lastName = parts[parts.length - 1]; // Lấy từ cuối cùng trong tên
        String firstLetter = lastName.toUpperCase().substring(0, 1);
        drawerProfileText.setText(firstLetter);
    }

    private void setFragement(Fragment categoryFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(main_frame.getId(),categoryFragment);//main_frame là frame thay thế
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            setFragement(new CategoryFragment());
        }else if(id == R.id.nav_account){
            setFragement(new AccountFragment());
        }else if(id == R.id.nav_leaderboard)//giả xu sử lý log uot
        {
            setFragement(new LeaderBoardFragment());
        }
        else if(id == R.id.nav_bookmarks){
            Intent intent = new Intent(MainActivity.this,BookmarksActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}