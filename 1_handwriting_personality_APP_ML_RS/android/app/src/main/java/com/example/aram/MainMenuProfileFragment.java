package com.example.aram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuProfileFragment extends Fragment {

    private ProfileAdapter profileAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tabCurrentIdx = 0;
    private TextView tv_userID, tv_userName, tv_num_posts ;
    private TextView tv_following_count, tv_followers_count ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_profile, container, false);
        tabLayout = rootView.findViewById(R.id.profile_tablayer);
        viewPager = rootView.findViewById(R.id.profile_viewpager);

        tabLayout.addTab(tabLayout.newTab().setText("포스팅"));
        tabLayout.addTab(tabLayout.newTab().setText("스크랩"));

        //커스텀 어댑터 생성
        profileAdapter = new ProfileAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(profileAdapter);
        viewPager.setCurrentItem(tabCurrentIdx);
        viewPager.setSaveEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabCurrentIdx = tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        try {
            //화면 적용
            // 안드 내부에서 받아온 값. getPreferences 관련 ~~
            //String userID = getArguments().getString("userID");
            //String userID = "ww";
            SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
            String userID = sharedPref.getString("USER_ID", "");


            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        // JSONObject result = jsonObject.getJSONObject("myinfo");
                        JSONArray raw_result = jsonObject.getJSONArray("myinfo");
                        JSONObject result = raw_result.getJSONObject(0);


                        String userName = result.getString("userName");
                        String following = String.valueOf(result.getInt("following"));
                        String followers = String.valueOf(result.getInt("followers"));
                        String posts = String.valueOf(result.getInt("posts"));

                        tv_userID = rootView.findViewById(R.id.userID);
                        tv_userID.setText(userID);

                        tv_userName = rootView.findViewById(R.id.userName);
                        tv_userName.setText(userName);

                        tv_following_count = rootView.findViewById(R.id.following_count);
                        tv_following_count.setText(following);
                        tv_following_count.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
//                                Intent intent = new Intent(getActivity(), UserFollowingActivity.class);
//                                startActivity(intent);
                                ((MainActivity)getActivity()).replaceFragment(new UserFollowingFragment());

                            }
                        });

                        tv_followers_count = rootView.findViewById(R.id.followers_count);
                        tv_followers_count.setText(followers);
                        tv_followers_count.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
//                                Intent intent = new Intent(getActivity(), UserFollowerActivity.class);
//                                startActivity(intent);
                                ((MainActivity)getActivity()).replaceFragment(new UserFollowerFragment());

                            }
                        });

                        tv_num_posts = rootView.findViewById(R.id.num_posts);
                        tv_num_posts.setText(posts);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            // 서버로 Volley를 이용해서 요청을 함
            MyInfoRequest myinfoRequest = new MyInfoRequest(userID, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(myinfoRequest);

        }catch (Exception e) {
            e.printStackTrace();
        }



        return rootView;
    }
}