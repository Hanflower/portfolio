package com.example.aram;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MainMenuHomeFragment fragmentHome = new MainMenuHomeFragment();
    private MainMenuSearchFragment fragmentSearch = new MainMenuSearchFragment();
    private MainMenuQuestFragment fragmentQuest = new MainMenuQuestFragment();
    private MainMenuProfileFragment fragmentProfile = new MainMenuProfileFragment();

    private FragmentTransaction transaction;
    //String userID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton fab = findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.menu_home:
                        transaction.replace(R.id.menu_frame_layout, fragmentHome).commitAllowingStateLoss();
                        break;
                    case R.id.menu_search:
                        transaction.replace(R.id.menu_frame_layout, fragmentSearch).commitAllowingStateLoss();
                        break;
                    case R.id.menu_quest:
                        transaction.replace(R.id.menu_frame_layout, fragmentQuest).commitAllowingStateLoss();
                        break;
                    case R.id.menu_profile:
                        transaction.replace(R.id.menu_frame_layout, fragmentProfile).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });
        fragmentHome = new MainMenuHomeFragment();
        fragmentSearch = new MainMenuSearchFragment();
        fragmentQuest = new MainMenuQuestFragment();
        fragmentProfile = new MainMenuProfileFragment();
        setFrag(0);

//        Intent intent = getIntent();
//        String userID = intent.getStringExtra("userID");
    }

    private void setFrag(int n){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        switch(n){
            case 0 :

//                Intent intentHome = getIntent();
//                String userID = intentHome.getStringExtra("userID");
////                String userName = intent.getStringExtra("userName");
//
//                Bundle bundle = new Bundle();
//                bundle.putString("userID",userID);
////                bundle.putString("userName",userName);
//                fragmentHome.setArguments(bundle);
                transaction.replace(R.id.menu_frame_layout, fragmentHome);
                transaction.commit();
                break;
            case 1 :

                transaction.replace(R.id.menu_frame_layout, fragmentSearch);
                transaction.commit();
                break;
            case 2 :
                //0623
//                Intent intentQuest = getIntent();
//                userID = intentQuest.getStringExtra("userID");
//                bundle = new Bundle();
//                bundle.putString("userID",userID);
//
//                fragmentQuest.setArguments(bundle);
                transaction.replace(R.id.menu_frame_layout, fragmentQuest);
                transaction.commit();
                break;
            case 3 :
                // Activity -> Fragment로 데이터 전송
//                Intent intentProfile = getIntent();
//                userID = intentProfile.getStringExtra("userID");
//
//                bundle = new Bundle();
//                bundle.putString("userID", userID);
//                fragmentProfile.setArguments(bundle);

                transaction.replace(R.id.menu_frame_layout, fragmentHome).commit();

                break;
        }

    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.menu_frame_layout, fragment).commit();
        fragmentTransaction.replace(R.id.menu_frame_layout, fragment).addToBackStack(null).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

}
