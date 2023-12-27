package com.example.aram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//추천 탐라 프래그먼트
public class RecommendTimelineFragment extends Fragment {

    public RecyclerView recyclerView;
    public PostAdapter postAdapter;
    private ArrayList<PostModel> postLists = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("로그", "RecommendTimelineFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_recommend_timeline, container, false);

        try {
            recyclerView = (RecyclerView) rootView.findViewById(R.id.rc_view);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            postLists = new ArrayList<>();
            postAdapter = new PostAdapter(getContext(), postLists);
            recyclerView.setAdapter(postAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getPostData();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.rc_timeline_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postLists = new ArrayList<>();
                postAdapter = new PostAdapter(getContext(), postLists);
                recyclerView.setAdapter(postAdapter);
                getPostData();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        return rootView;
    }

    public void getPostData() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("posts");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = result.getJSONObject(i);

                        PostModel data = new PostModel();
                        data.setUserid(item.getString("userID"));
                        data.setUsername(item.getString("userName"));
                        data.setPostimage(item.getString("writingImage"));
                        data.setDescription(item.getString("postText"));
                        data.setOrigin(item.getString("contentTitle"));
                        data.setOrigin_writer(item.getString("contentWriter"));
                        data.setPostdate(item.getString("postDate"));

                        data.setPostID(item.getInt("postID"));
                        data.setNum_of_bookmark(item.getInt("num_of_bookmark"));
                        data.setBookmarked(item.getInt("bookmarked"));
                        data.setEmoticoned(item.getInt("emoticoned"));
                        data.setLove(item.getInt("love"));
                        data.setSurprise(item.getInt("surprise"));
                        data.setAngry(item.getInt("angry"));
                        data.setSad(item.getInt("sad"));
                        data.setMine(item.getInt("mine"));

                        postAdapter.addItem(data);
                        postAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        // String userID = "ww";
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");
        RecommendRequest recommendRequest = new RecommendRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(recommendRequest);
    }
}