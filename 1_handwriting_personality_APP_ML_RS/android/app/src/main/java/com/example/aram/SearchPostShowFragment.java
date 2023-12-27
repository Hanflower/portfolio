package com.example.aram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchPostShowFragment extends Fragment {
    public RecyclerView recyclerView;
    public SearchPostAdapter postAdapter;
    private ArrayList<PostModel> postLists = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private String searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("로그", "SearchPostFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_mypost, container, false);
        try {
            recyclerView = (RecyclerView) rootView.findViewById(R.id.rc_view);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            postLists = new ArrayList<>();
            postAdapter = new SearchPostAdapter(getActivity().getApplicationContext(), postLists);
            recyclerView.setAdapter(postAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

        getPostData(true);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.mypost_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postLists = new ArrayList<>();
                postAdapter = new SearchPostAdapter(getActivity().getApplicationContext(), postLists);
                recyclerView.setAdapter(postAdapter);
                getPostData(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return rootView;

    }



    public void getPostData(boolean first) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("posts");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = result.getJSONObject(i);

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                        searchText = sharedPref.getString("SEARCH_TEXT", "");
                        String otherID = sharedPref.getString("OTHER_ID","");

                        if (item.getString("postText").contains(searchText) || item.getString("contentTitle").contains(searchText) || item.getString("contentWriter").contains(searchText)) {
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
                        }


                        // 특정 아이템을 상단으로 화면 위치
                        if (first) {
                            Intent intent = getActivity().getIntent();
                            int click_pos = intent.getIntExtra("pos", 0);
                            recyclerView.scrollToPosition(click_pos);
                        }
                        //

                        postAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        SharedPreferences sharedPref = getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String otherID = sharedPref.getString("OTHER_ID", "");
        PostRequest postRequest = new PostRequest(otherID,responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(postRequest);
    }



}
