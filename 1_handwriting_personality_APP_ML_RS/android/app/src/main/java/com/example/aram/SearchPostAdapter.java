package com.example.aram;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.SearchPostViewHolder> {

    public Context mContext;
    public ArrayList<PostModel> mPost;

    public SearchPostAdapter(Context mContext, ArrayList<PostModel> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override

    public SearchPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new SearchPostAdapter.SearchPostViewHolder(view);
    }

    //아이템 정보 담기
    @Override
    public void onBindViewHolder(@NonNull SearchPostAdapter.SearchPostViewHolder holder, int position) {
        //String userID = "ww";
        //String userName = "아람";
        PostModel post = mPost.get(position);
        holder.onBind(mPost.get(position));

        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
//        publisherInfo(holder.username, holder.postdate, post.getUserid());
        holder.userid.setText(mPost.get(position).getUserid());
        holder.username.setText(mPost.get(position).getUsername());
        holder.origin.setText(mPost.get(position).getOrigin());
        holder.origin_writer.setText(mPost.get(position).getOrigin_writer());
        holder.postdate.setText(mPost.get(position).getPostdate());


        // 글 수정&삭제
        if (mPost.get(position).getMine() == 1) {holder.modify_post.setVisibility(holder.modify_post.VISIBLE);}
        else {holder.modify_post.setVisibility(holder.modify_post.INVISIBLE);}

        if (mPost.get(position).getMine() == 1) {holder.delete_post.setVisibility(holder.delete_post.VISIBLE);}
        else {holder.delete_post.setVisibility(holder.delete_post.INVISIBLE);}

        // emoticon 버튼 세팅
        switch(mPost.get(position).getEmoticoned()) {
            case 4 : { holder.love.setChecked(true); break; }
            case 3 : { holder.surprise.setChecked(true); break; }
            case 2 : { holder.angry.setChecked(true);  break; }
            case 1 : { holder.sad.setChecked(true); break; }
            default : break;
        }

        // emoticon 수 세팅
        holder.num_love.setText(String.valueOf(mPost.get(position).getLove()));
        holder.num_surprise.setText(String.valueOf(mPost.get(position).getSurprise()));
        holder.num_angry.setText(String.valueOf(mPost.get(position).getAngry()));
        holder.num_sad.setText(String.valueOf(mPost.get(position).getSad()));

        // bookmark 버튼 세팅
        holder.bookmark.setSelected(mPost.get(position).getBookmarked() == 1); // 기존에 이미 북마크했으면 selected 처리

        // 아래 click listener는 아래 public class PostViewHolder 에 추가. 위치를 모르겠어서 주석 번갈아가면서 해봤는데 둘 다 작동하는 것처럼 보임..
        //final PostViewHolder postViewHolder = (PostViewHolder) holder;
        // holder.bookmark.setOnClickListener(~)

        // bookmark 수 세팅
        holder.num_bookmark.setText(String.valueOf(mPost.get(position).getNum_of_bookmark()));


    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    void addItem(PostModel data) {
        // 외부에서 item을 추가시킬 함수입니다.
        mPost.add(data);
    }

    public class SearchPostViewHolder extends RecyclerView.ViewHolder {
        public ImageView post_image; // like , save
        public TextView username, userid, likes, origin, origin_writer, postdate, description;

        public Button bookmark, modify_post, delete_post;

        public CheckBox love, surprise, angry, sad;
        public TextView num_love, num_surprise, num_angry, num_sad;

        public TextView num_bookmark;
//        public RelativeLayout userinfo;

        private PostModel data;

        void onBind(PostModel data){
            try {
                this.data = data;
                userid.setText(data.getUserid());
                username.setText(data.getUsername());
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }

        public SearchPostViewHolder(@NonNull View itemView) {
            super(itemView);

//            userinfo = itemView.findViewById(R.id.userinfo);
            userid = itemView.findViewById(R.id.userid);
            post_image = itemView.findViewById(R.id.post_image);
            //emoticon = itemView.findViewById(R.id.emoticon);
            bookmark = itemView.findViewById(R.id.bookmark);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            origin = itemView.findViewById(R.id.origin);
            origin_writer = itemView.findViewById(R.id.origin_writer);
            postdate = itemView.findViewById(R.id.postdate);
            description = itemView.findViewById(R.id.description);

            modify_post = itemView.findViewById(R.id.modify_post);
            delete_post = itemView.findViewById(R.id.delete_post);

            num_bookmark = itemView.findViewById(R.id.num_bookmark);

            love = itemView.findViewById(R.id.love);
            surprise = itemView.findViewById(R.id.surprise);
            angry = itemView.findViewById(R.id.angry);
            sad = itemView.findViewById(R.id.sad);

            num_love = itemView.findViewById(R.id.num_love);
            num_surprise = itemView.findViewById(R.id.num_surprise);
            num_angry = itemView.findViewById(R.id.num_angry);
            num_sad = itemView.findViewById(R.id.num_sad);

            //유저 정보 클릭시
            userid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    if (data != null) {
                       // SharedPreferences sharedPref = (FragmentManager.findFragment(view).getParentFragment().getActivity()).getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("OTHER_ID", item.getUserid());
                        editor.commit();

                        //((MainActivity) FragmentManager.findFragment(view).getParentFragment().getParentFragment().getActivity()).replaceFragment(new OtherUserProfileFragment());

                        ((MainActivity) FragmentManager.findFragment(view).getActivity()).replaceFragment(new OtherUserProfileFragment());

                        //((MainActivity) view.getContext()).replaceFragment(new OtherUserProfileFragment());
//
//                        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("OTHER_ID", item.getUserid());
//                        editor.commit();

                        Log.e("피드에서 타유저프로필 이동", "다른 유저 프로필 연결 오류 발생");

                    }
                }
            });

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    if (data != null) {
//                        SharedPreferences sharedPref = (FragmentManager.findFragment(view).getParentFragment().getActivity()).getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("OTHER_ID", item.getUserid());
                        editor.commit();

                        ((MainActivity) FragmentManager.findFragment(view).getActivity()).replaceFragment(new OtherUserProfileFragment());
//                      ((MainActivity) view.getContext()).replaceFragment(new OtherUserProfileFragment());
//
//                        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("OTHER_ID", item.getUserid());
//                        editor.commit();
                    }
                }
            });

            // 글 수정 -> 기존 값이 배치된 액티비티로 연결 -> 거기서 UpdatePostRequest 연결
            modify_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // OriginAdapter.java 참고
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);

                    Intent intent = new Intent(view.getContext(), UpdatePostActivity.class); // context 얻는게 이게 아닐수도 getActivity() view.getContext()
                    intent.putExtra("userID", item.getUserid());
                    intent.putExtra("postID", item.getPostID());
                    intent.putExtra("postText", item.getDescription());
                    intent.putExtra("upload", item.getPostimage());
                    intent.putExtra("contentTitle", item.getOrigin());
                    intent.putExtra("contentWriter", item.getOrigin_writer());

                    view.getContext().startActivity(intent);

                    notifyItemChanged(pos);

                }
            });


            // 글 삭제
            delete_post.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    int postID = item.getPostID();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) { // 글 삭제에 성공한 경우
                                } else { // 글 삭제에 실패한 경우
                                    Log.e("글 삭제 버튼", "실패");
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    DeletePostRequest deletepostRequest = new DeletePostRequest(postID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                    queue.add(deletepostRequest);

                    notifyItemRemoved(pos);
                }
            });
            ///

            /// 북마크 & 북마크 수(num_bookmark)
            bookmark.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    //String userID = item.getUserid(); // !!!! 이거말고 로그인한 userID를 받아야할거 같은데!!!!
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                    String userID = sharedPref.getString("USER_ID", "");


                    int postID = item.getPostID();

                    view.setSelected(!view.isSelected()); // 버튼 선택여부 반전

                    if (view.isSelected()) {
                        // aram_bookmark.php 연결 (유저 아이디랑 포스트 아이디 둘 다 안드에서 받아와서 그대로 삽입)
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");
                                    if (success) { // 북마크 기록 삽입에 성공한 경우
                                        item.setBookmarked(1);
                                        item.setNum_of_bookmark(item.getNum_of_bookmark() + 1);
                                        num_bookmark.setText(String.valueOf(item.getNum_of_bookmark()));
//                                        notifyItemChanged(pos);
                                    } else { // 북마크 기록 삽입에 실패한 경우
                                        item.setBookmarked(0);
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        BookmarkRequest bookmarkRequest = new BookmarkRequest(userID, postID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                        queue.add(bookmarkRequest);
                    } else {
                        // aram_cancelbookmark.php 연결 (유저 아이디랑 포스트 아이디 둘 다 안드에서 받아와서 해당 행 삭제)
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                item.setBookmarked(0);
                                item.setNum_of_bookmark(item.getNum_of_bookmark() - 1);
                                num_bookmark.setText(String.valueOf(item.getNum_of_bookmark()));
                                notifyItemChanged(pos);
                            }
                        };

                        BookmarkCancelRequest bookmarkcancelRequest = new BookmarkCancelRequest(userID, postID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                        queue.add(bookmarkcancelRequest);
                    }
                }
            });
            ///

            /// 이모티콘 버튼 4개
            love.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    //String userID = item.getUserid(); // !!!! 이거말고 로그인한 userID를 받아야할거 같은데!!!!
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                    String userID = sharedPref.getString("USER_ID", "");

                    int postID = item.getPostID();

                    switch (item.getEmoticoned()) { // 기존 이모티콘 상태

                        case 0: {
                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 삽입에 성공한 경우
                                                item.setEmoticoned(4);
                                                item.setLove(item.getLove() + 1);
                                                num_love.setText(String.valueOf(item.getLove()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 삽입에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonRequest emoticonRequest = new EmoticonRequest(userID, postID, 4, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonRequest);
                            }
                            break;
                        }

                        case 4: {
                            if (!((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        item.setEmoticoned(0);
                                        item.setLove(item.getLove() - 1);
                                        num_love.setText(String.valueOf(item.getLove()));
                                        //notifyItemChanged(pos);
                                    }
                                };

                                EmoticonCancelRequest emoticoncancelRequest = new EmoticonCancelRequest(userID, postID, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticoncancelRequest);
                                break;
                            }
                        }

                        default: {
                            switch (item.getEmoticoned()) {
                                case 3: {
                                    surprise.setChecked(false);
                                    item.setSurprise(item.getSurprise() - 1);
                                    num_surprise.setText(String.valueOf(item.getSurprise()));
                                    break;
                                }
                                case 2: {
                                    angry.setChecked(false);
                                    item.setAngry(item.getAngry() - 1);
                                    num_angry.setText(String.valueOf(item.getAngry()));
                                    break;
                                }
                                case 1: {
                                    sad.setChecked(false);
                                    item.setSad(item.getSad() - 1);
                                    num_sad.setText(String.valueOf(item.getSad()));
                                    break;
                                }
                            }

                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 수정에 성공한 경우
                                                item.setEmoticoned(4);
                                                item.setLove(item.getLove() + 1);
                                                num_love.setText(String.valueOf(item.getLove()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 수정에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonUpdateRequest emoticonupdateRequest = new EmoticonUpdateRequest(userID, postID, 4, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonupdateRequest);
                            }
                            break;
                        }

                    }
                }
            });

            surprise.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    // String userID = item.getUserid(); // !!!! 이거말고 로그인한 userID를 받아야할거 같은데!!!!
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                    String userID = sharedPref.getString("USER_ID", "");

                    int postID = item.getPostID();

                    switch (item.getEmoticoned()) { // 기존 이모티콘 상태

                        case 0: {
                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 삽입에 성공한 경우
                                                item.setEmoticoned(3);
                                                item.setSurprise(item.getSurprise() + 1);
                                                num_surprise.setText(String.valueOf(item.getSurprise()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 삽입에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonRequest emoticonRequest = new EmoticonRequest(userID, postID, 3, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonRequest);
                            }
                            break;
                        }

                        case 3: {
                            if (!((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        item.setEmoticoned(0);
                                        item.setSurprise(item.getSurprise() - 1);
                                        num_surprise.setText(String.valueOf(item.getSurprise()));
                                        //notifyItemChanged(pos);
                                    }
                                };

                                EmoticonCancelRequest emoticoncancelRequest = new EmoticonCancelRequest(userID, postID, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticoncancelRequest);
                                break;
                            }
                        }

                        default: {
                            switch (item.getEmoticoned()) {
                                case 4: {
                                    love.setChecked(false);
                                    item.setLove(item.getLove() - 1);
                                    num_love.setText(String.valueOf(item.getLove()));
                                    break;
                                }
                                case 2: {
                                    angry.setChecked(false);
                                    item.setAngry(item.getAngry() - 1);
                                    num_angry.setText(String.valueOf(item.getAngry()));
                                    break;
                                }
                                case 1: {
                                    sad.setChecked(false);
                                    item.setSad(item.getSad() - 1);
                                    num_sad.setText(String.valueOf(item.getSad()));
                                    break;
                                }
                            }

                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 수정에 성공한 경우
                                                item.setEmoticoned(3);
                                                item.setSurprise(item.getSurprise() + 1);
                                                num_surprise.setText(String.valueOf(item.getSurprise()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 수정에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonUpdateRequest emoticonupdateRequest = new EmoticonUpdateRequest(userID, postID, 3, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonupdateRequest);
                            }
                            break;
                        }

                    }
                }
            });

            angry.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    // String userID = item.getUserid(); // !!!! 이거말고 로그인한 userID를 받아야할거 같은데!!!!
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                    String userID = sharedPref.getString("USER_ID", "");

                    int postID = item.getPostID();

                    switch (item.getEmoticoned()) { // 기존 이모티콘 상태

                        case 0: {
                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 삽입에 성공한 경우
                                                item.setEmoticoned(2);
                                                item.setAngry(item.getAngry() + 1);
                                                num_angry.setText(String.valueOf(item.getAngry()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 삽입에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonRequest emoticonRequest = new EmoticonRequest(userID, postID, 2, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonRequest);
                            }
                            break;
                        }

                        case 2: {
                            if (!((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        item.setEmoticoned(0);
                                        item.setAngry(item.getAngry() - 1);
                                        num_angry.setText(String.valueOf(item.getAngry()));
                                        //notifyItemChanged(pos);
                                    }
                                };

                                EmoticonCancelRequest emoticoncancelRequest = new EmoticonCancelRequest(userID, postID, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticoncancelRequest);
                                break;
                            }
                        }

                        default: {
                            switch (item.getEmoticoned()) {
                                case 4: {
                                    love.setChecked(false);
                                    item.setLove(item.getLove() - 1);
                                    num_love.setText(String.valueOf(item.getLove()));
                                    break;
                                }
                                case 3: {
                                    surprise.setChecked(false);
                                    item.setSurprise(item.getSurprise() - 1);
                                    num_surprise.setText(String.valueOf(item.getSurprise()));
                                    break;
                                }
                                case 1: {
                                    sad.setChecked(false);
                                    item.setSad(item.getSad() - 1);
                                    num_sad.setText(String.valueOf(item.getSad()));
                                    break;
                                }
                            }

                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 수정에 성공한 경우
                                                item.setEmoticoned(2);
                                                item.setAngry(item.getAngry() + 1);
                                                num_angry.setText(String.valueOf(item.getAngry()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 수정에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonUpdateRequest emoticonupdateRequest = new EmoticonUpdateRequest(userID, postID, 2, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonupdateRequest);
                                ;
                            }
                            break;
                        }

                    }
                }
            });

            sad.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    PostModel item = mPost.get(pos);
                    // String userID = item.getUserid(); // !!!! 이거말고 로그인한 userID를 받아야할거 같은데!!!!
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                    String userID = sharedPref.getString("USER_ID", "");

                    int postID = item.getPostID();

                    switch (item.getEmoticoned()) { // 기존 이모티콘 상태

                        case 0: {
                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 삽입에 성공한 경우
                                                item.setEmoticoned(1);
                                                item.setSad(item.getSad() + 1);
                                                num_sad.setText(String.valueOf(item.getSad()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 삽입에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonRequest emoticonRequest = new EmoticonRequest(userID, postID, 1, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonRequest);
                            }
                            break;
                        }

                        case 1: {
                            if (!((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        item.setEmoticoned(0);
                                        item.setSad(item.getSad() - 1);
                                        num_sad.setText(String.valueOf(item.getSad()));
                                        //notifyItemChanged(pos);
                                    }
                                };

                                EmoticonCancelRequest emoticoncancelRequest = new EmoticonCancelRequest(userID, postID, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticoncancelRequest);
                                break;
                            }
                        }

                        default: {
                            switch (item.getEmoticoned()) {
                                case 4: {
                                    love.setChecked(false);
                                    item.setLove(item.getLove() - 1);
                                    num_love.setText(String.valueOf(item.getLove()));
                                    break;
                                }
                                case 3: {
                                    surprise.setChecked(false);
                                    item.setSurprise(item.getSurprise() - 1);
                                    num_surprise.setText(String.valueOf(item.getSurprise()));
                                    break;
                                }
                                case 2: {
                                    angry.setChecked(false);
                                    item.setAngry(item.getAngry() - 1);
                                    num_angry.setText(String.valueOf(item.getAngry()));
                                    break;
                                }
                            }

                            if (((CheckBox) view).isChecked()) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) { // 이모티콘 기록 수정에 성공한 경우
                                                item.setEmoticoned(1);
                                                item.setSad(item.getSad() + 1);
                                                num_sad.setText(String.valueOf(item.getSad()));
                                                //notifyItemChanged(pos);
                                            } else { // 이모티콘 기록 수정에 실패한 경우
                                                item.setEmoticoned(0);
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                EmoticonUpdateRequest emoticonupdateRequest = new EmoticonUpdateRequest(userID, postID, 1, responseListener);
                                RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                                queue.add(emoticonupdateRequest);
                            }
                            break;
                        }

                    }
                }
            });


        }

    }


}