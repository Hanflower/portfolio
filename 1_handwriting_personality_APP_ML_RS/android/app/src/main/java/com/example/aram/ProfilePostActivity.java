package com.example.aram;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ProfilePostActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public PostAdapter postAdapter;
    private ArrayList<PostModel> postLists = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);



        try {
            recyclerView = (RecyclerView) findViewById(R.id.rc_view);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            postLists = new ArrayList<>();
            postAdapter = new PostAdapter(getApplicationContext(), postLists);
            recyclerView.setAdapter(postAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

        getPostData(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mypost_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postLists = new ArrayList<>();
                postAdapter = new PostAdapter(getApplicationContext(), postLists);
                recyclerView.setAdapter(postAdapter);
                getPostData(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

                        // 특정 아이템을 상단으로 화면 위치
                        if (first) {
                            Intent intent = getIntent();
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

        SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");
        ProfilePostRequest profilePostRequest = new ProfilePostRequest(userID,responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(profilePostRequest);
    }
}