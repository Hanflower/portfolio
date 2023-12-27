package com.example.aram;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchtextAdapter extends RecyclerView.Adapter<SearchtextAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<SearchtextModel> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchtext_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(SearchtextModel data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView searchtextView;
        private Button deleteoneText;

        private SearchtextModel data;

        ItemViewHolder(View itemView) {
            super(itemView);

            searchtextView = (TextView)itemView.findViewById(R.id.searchtext);
            deleteoneText = (Button)itemView.findViewById(R.id.delete_onesearchtext);

            // DB USER_SEARCH TABLE의 특정 데이터 하나 삭제
            if(data != null) {
                deleteoneText.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {
                        int pos = getBindingAdapterPosition();
                        SearchtextModel item = listData.get(pos);
                        String searchDate = item.getSearchDate();


                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");
                                    if (success) { // 검색어 하나 지우기 성공한 경우
                                    } else { // 검색어 하나 지우기 실패한 경우
                                        Log.e("검색어 하나 지우기 버튼", "실패");
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };


                        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                        String userID = sharedPref.getString("USER_ID", "");

                        SearchtextDeleteOneRequest searchtextdeleteonerequest = new SearchtextDeleteOneRequest(userID, searchDate, responseListener);
                        RequestQueue queue = Volley.newRequestQueue((view.getContext()));
                        queue.add(searchtextdeleteonerequest);

                        notifyItemRemoved(pos);
                    }
                });
            }

        }

        void onBind(SearchtextModel data) {
            try {
                this.data = data;
                searchtextView.setText(data.getSearchText());
            }catch(NullPointerException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onClick(View v) {
            if (data != null) {

            }
        }
    }
}