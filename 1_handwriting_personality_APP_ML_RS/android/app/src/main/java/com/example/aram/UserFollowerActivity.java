package com.example.aram;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserFollowerActivity extends AppCompatActivity {
    public  UserFollowAdapter adapter;
    private ArrayList<UserFollowModel> FollowArrayList = new ArrayList<>();
    public RecyclerView rc_followerView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_follower);

        rc_followerView = findViewById(R.id.rcview_follower);
        rc_followerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc_followerView.setLayoutManager(linearLayoutManager);

        FollowArrayList = new ArrayList<>();
        adapter = new UserFollowAdapter(this,FollowArrayList);
        rc_followerView.setAdapter(adapter);
        getFollowerData();

    }

    private void getFollowerData(){
        SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response){
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("follower_list");

                    for(int i=0; i<result.length(); i++){

                        JSONObject item = result.getJSONObject(i);

                        // 각 List의 값들을 data 객체에 set 해줍니다.
                        UserFollowModel model = new UserFollowModel();
                        model.setFollowid(item.getString("followerID"));
                        model.setFollowname(item.getString("followerName"));

                        // 각 값이 들어간 data를 adapter에 추가합니다.
                        adapter.addItem(model);
                        // adapter의 값이 변경되었다는 것을 알려줍니다.
                        adapter.notifyDataSetChanged();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        UserFollowerRequest userFollowerRequest = new UserFollowerRequest(userID,responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserFollowerActivity.this);
        queue.add(userFollowerRequest);
    }
}
