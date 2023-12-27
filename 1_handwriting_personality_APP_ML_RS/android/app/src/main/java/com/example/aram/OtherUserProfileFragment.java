package com.example.aram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OtherUserProfileFragment extends Fragment {

    private OtherProfileAdapter otherprofileAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tabCurrentIdx = 0;
    private TextView tv_userID, tv_userName, tv_num_posts ;
    private TextView tv_following_count, tv_followers_count ;
//    private Intent intent;

    private Button follow_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_other_user_profile, container, false);
        tabLayout = rootView.findViewById(R.id.profile_tablayer);
        viewPager = rootView.findViewById(R.id.profile_viewpager);
        // Inflate the layout for this fragment
        tabLayout.addTab(tabLayout.newTab().setText("포스팅"));
        tabLayout.addTab(tabLayout.newTab().setText("스크랩"));
        follow_btn = rootView.findViewById(R.id.follow_btn);

        //커스텀 어댑터 생성
        otherprofileAdapter = new OtherProfileAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(otherprofileAdapter);
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

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");
        String otherID = sharedPref.getString("OTHER_ID", "");

        //만약 내 아이디와 otherid가 같다면 버튼 안보이기
        if(userID.equals(otherID)){
            follow_btn.setVisibility(View.INVISIBLE);
        }

        try {
            //화면 적용
            // 안드 내부에서 받아온 값. getPreferences 관련 ~~
            //String userID = getArguments().getString("userID");
            //String userID = "ww";

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
                        tv_userID.setText(otherID);

                        tv_userName = rootView.findViewById(R.id.userName);
                        tv_userName.setText(userName);

                        tv_following_count = rootView.findViewById(R.id.following_count);
                        tv_following_count.setText(following);
                        tv_following_count.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                ((MainActivity)getActivity()).replaceFragment(new OtherUserFollowingFragment());
                            }
                        });

                        tv_followers_count = rootView.findViewById(R.id.followers_count);
                        tv_followers_count.setText(followers);
                        tv_followers_count.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                ((MainActivity)getActivity()).replaceFragment(new OtherUserFollowerFragment());
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
            MyInfoRequest myinfoRequest = new MyInfoRequest(otherID, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(myinfoRequest);

        }catch (Exception e) {
            e.printStackTrace();
        }

        // 팔로잉 버튼
        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray raw_result = jsonObject.getJSONArray("tf");
                    int res = raw_result.getInt(0);
//                    follow_btn.setSelected(res == 1);

                    if(res == 1){
                        follow_btn.setBackgroundColor(getResources().getColor(R.color.serenity));
                    } // 버튼 초기 세팅. 이미 팔로잉.
                    else{
                        return;
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        FollowingCheckRequest followingcheckRequest = new FollowingCheckRequest(userID, otherID, responseListener2);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(followingcheckRequest);

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected()); // 버튼 선택여부 반전

                if (view.isSelected()) {
                    // aram_following.php 연결 (유저 아이디랑 팔로잉 아이디 둘 다 안드에서 받아와서 그대로 삽입)
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) { // 팔로잉 기록 삽입에 성공한 경우
                                    follow_btn.setBackgroundColor(getResources().getColor(R.color.serenity));
                                    follow_btn.setText("팔로잉");
                                    int count = Integer.parseInt(tv_followers_count.getText().toString());
                                    count++;
                                    tv_followers_count.setText(String.valueOf(count));

                                }
                                else { // 팔로잉 기록 삽입에 실패한 경우
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    FollowingRequest followingRequest = new FollowingRequest(userID, otherID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(followingRequest);

                } else {
                    // aram_cancelfollowing.php 연결 (유저 아이디랑 팔로잉 아이디 둘 다 안드에서 받아와서 해당 행 삭제)
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            follow_btn.setBackgroundColor(getResources().getColor(R.color.classicBlue));
                            follow_btn.setText("팔로우");
                            int count = Integer.parseInt(tv_followers_count.getText().toString());
                            count--;
                            tv_followers_count.setText(String.valueOf(count));
                        }
                    };

                    FollowingCancelRequest followingCancelRequest = new FollowingCancelRequest(userID, otherID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(followingCancelRequest);
                }

            }
        });

        return rootView;
    }
}