package com.example.aram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//포스팅 프래그먼트
public class ProfilePostFragment extends Fragment {

    public GridView gridView;

    public GridViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("로그", "ProfilePostFragment");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_profile_post, container, false);

        try {
            gridView = (GridView) rootView.findViewById(R.id.rc_view);

            adapter = new GridViewAdapter();
            getPostData();
            gridView.setAdapter(adapter);


        } catch (Exception e) {
            e.printStackTrace();
        }

        //getPostData();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.mypost_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new GridViewAdapter();
                getPostData();
                gridView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    class GridViewAdapter extends BaseAdapter {

        ArrayList<PostModel> postLists = new ArrayList<>();

        @Override
        public int getCount() {
            return postLists.size();
        }

        public void addItem(PostModel item) {
            postLists.add(item);
        }

        @Override
        public Object getItem(int position) {
            return postLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context mContext = viewGroup.getContext();
            final PostModel post = postLists.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.profile_item, viewGroup, false);

                ImageView iv_icon = (ImageView) convertView.findViewById(R.id.post_image);

                Glide.with(mContext).load(post.getPostimage()).into(iv_icon);


            } else {
                View view = new View(mContext);
                view = (View) convertView;
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 액티비티 이동
                    Intent intent = new Intent(getActivity(), ProfilePostActivity.class);
                    intent.putExtra("pos", position);
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }
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


                        adapter.addItem(data);
                        adapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        //String userID = "ww";
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");
        ProfilePostRequest profilePostRequest = new ProfilePostRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(profilePostRequest);
    }
}