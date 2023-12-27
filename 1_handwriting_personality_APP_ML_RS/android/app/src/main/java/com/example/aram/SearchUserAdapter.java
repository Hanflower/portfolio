package com.example.aram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//searchuserfragment에서 사용
public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchViewHolder>{
    public Context uContext;

    private ArrayList<UserFollowModel> listData;
    OnItemClick onItemClick;
    public void onItemClickListener(OnItemClick listener){
        this.onItemClick = listener;
    }

    public SearchUserAdapter(Context uContext, ArrayList<UserFollowModel> listData){
        this.uContext = uContext;
        this.listData = listData;
    }

    @NonNull
    @Override
    public SearchUserAdapter .SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(uContext).inflate(R.layout.recyclerview_follow,parent,false);
        return new SearchUserAdapter.SearchViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position){
        holder.onBind(listData.get(position));
        holder.search_userid.setText(listData.get(position).getFollowid());
        holder.search_username.setText(listData.get(position).getFollowname());

    }

    @Override
    public int getItemCount(){
        return listData.size();
    }

    void addItem(UserFollowModel data){
        listData.add(data);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView search_userid;
        private UserFollowModel data;
        //followername 출력된다면..
        private TextView search_username;

        SearchViewHolder(View itemView){
            super(itemView);
            search_userid = (TextView)itemView.findViewById(R.id.follow_userid);
            search_username = (TextView)itemView.findViewById(R.id.follow_username);
        }

        void onBind(UserFollowModel data){
            try {
                this.data = data;
                search_userid.setText(data.getFollowid());
                search_username.setText(data.getFollowname());
                itemView.setOnClickListener(this);
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }

        //profile 화면이 완료가 되면 아래에 followerid를 클릭했을경우 그 팔로워의 프로필 탭으로 이동하는 함수 작성예정
        @Override
        public void onClick(View view) {
            if(data != null){

                SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("OTHER_ID", data.getFollowid());
                editor.commit();

                ((MainActivity) FragmentManager.findFragment(view).getParentFragment().getActivity()).replaceFragment(new OtherUserProfileFragment());

//                ((MainActivity)view.getContext()).replaceFragment(new OtherUserProfileFragment());
//
//                SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putString("OTHER_ID", data.getFollowid());
//                editor.commit();
            }
        }
    }
}
