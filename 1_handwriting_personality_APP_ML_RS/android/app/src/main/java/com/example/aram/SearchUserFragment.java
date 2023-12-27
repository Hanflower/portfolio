package com.example.aram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//포스팅 프래그먼트
public class SearchUserFragment extends Fragment {

    public  SearchUserAdapter adapter;
    private ArrayList<UserFollowModel> FollowArrayList = new ArrayList<>();
    public RecyclerView rc_userView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String searchText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("로그", "SearchUserFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_user, container, false);

        // 사용자가 입력한 검색어
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        searchText = sharedPref.getString("SEARCH_TEXT", "");
        Log.e("넘어온 검색어 :", searchText); // 넘어오는거 확인 후 지우기

        try {
            rc_userView = (RecyclerView) rootView.findViewById(R.id.rc_view_user);
            rc_userView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
            rc_userView.setLayoutManager(linearLayoutManager);

            FollowArrayList = new ArrayList<>();
            adapter = new SearchUserAdapter(this.getActivity().getApplicationContext(),FollowArrayList);
            rc_userView.setAdapter(adapter);
            getUsersData();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        getUsersData();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.rc_user_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FollowArrayList = new ArrayList<>();
                adapter = new SearchUserAdapter(getContext(),FollowArrayList);
                rc_userView.setAdapter(adapter);
                getUsersData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void getUsersData(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {


            @Override
            public void onResponse(String response){
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("users");

                    for(int i=0; i<result.length(); i++){
                        JSONObject item = result.getJSONObject(i);

                        // 검색어를 포함하는 경우에만
                        if (item.getString("userID").contains(searchText) || item.getString("userName").contains(searchText)) {
                            UserFollowModel model = new UserFollowModel();
                            model.setFollowid(item.getString("userID"));
                            model.setFollowname(item.getString("userName"));

                            // 각 값이 들어간 data를 adapter에 추가합니다.
                            adapter.addItem(model);
                            // adapter의 값이 변경되었다는 것을 알려줍니다.
                            adapter.notifyDataSetChanged();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        UsersRequest usersRequest = new UsersRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(usersRequest);
    }

}