package com.example.aram;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserFollowAdapter extends RecyclerView.Adapter<UserFollowAdapter.UserViewHolder>{

    public Context uContext;

    private ArrayList<UserFollowModel> listData;
    OnItemClick onItemClick;
    public void onItemClickListener(OnItemClick listener){
        this.onItemClick = listener;
    }

    public UserFollowAdapter(Context uContext, ArrayList<UserFollowModel> listData){
        this.uContext = uContext;
        this.listData = listData;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(uContext).inflate(R.layout.recyclerview_follow,parent,false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position){
        holder.onBind(listData.get(position));
        holder.follow_userid.setText(listData.get(position).getFollowid());
        holder.follow_username.setText(listData.get(position).getFollowname());

    }

    @Override
    public int getItemCount(){
        return listData.size();
    }

    void addItem(UserFollowModel data){
        listData.add(data);
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView follow_userid;
        private UserFollowModel data;
        //followername 출력된다면..
        private TextView follow_username;

        UserViewHolder(View itemView){
            super(itemView);
            follow_userid = (TextView)itemView.findViewById(R.id.follow_userid);
            follow_username = (TextView)itemView.findViewById(R.id.follow_username);
        }

        void onBind(UserFollowModel data){
            try {
                this.data = data;
                follow_userid.setText(data.getFollowid());
                follow_username.setText(data.getFollowname());
                itemView.setOnClickListener(this);
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }

        //profile 화면이 완료가 되면 아래에 followerid를 클릭했을경우 그 팔로워의 프로필 탭으로 이동하는 함수 작성
        @Override
        public void onClick(View view) {
            if(data != null){

                SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("OTHER_ID", data.getFollowid());
                editor.commit();

                ((MainActivity)view.getContext()).replaceFragment(new OtherUserProfileFragment());


                //Intent intent = new Intent(view.getContext(), OtherUserProfileFragment.class);
                //intent.putExtra("followingID", data.getFollowid());
                //intent.putExtra("followingName",data.getFollowname());
                //여기서 오류 발생할거같음
                Log.e("유저팔로우어댑터","다른 유저 프로필 연결 오류 발생");

                //(Activity)(view.getContext())).setResult(RESULT_OK, intent); //v.getContext().startActivity(intent);
                //((Activity)(view.getContext())).finish();
            }
        }
    }
}
