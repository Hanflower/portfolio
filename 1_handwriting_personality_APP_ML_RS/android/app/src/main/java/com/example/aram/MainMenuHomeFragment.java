package com.example.aram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainMenuHomeFragment extends Fragment {

    private HomeAdapter homeAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tabCurrentIdx = 0;

    @Override
    public void onResume(){
        super.onResume();
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu_home, container, false);
        tabLayout = view.findViewById(R.id.home_tab_Layout);
        viewPager = view.findViewById(R.id.home_view_Pager);
        //피드 구성하는 탭레이아웃 + 뷰페이저
        tabLayout.addTab(tabLayout.newTab().setText("추천"));
        tabLayout.addTab(tabLayout.newTab().setText("실시간"));
        //커스텀 어댑터 생성
        homeAdapter = new HomeAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(homeAdapter);
        viewPager.setCurrentItem(tabCurrentIdx);
        viewPager.setSaveEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabCurrentIdx = tab.getPosition();
                onResume();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

}