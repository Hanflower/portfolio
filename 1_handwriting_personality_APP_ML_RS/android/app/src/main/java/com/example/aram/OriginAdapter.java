package com.example.aram;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OriginAdapter extends RecyclerView.Adapter<OriginAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<OriginData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_origin, parent, false);
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

    void addItem(OriginData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // 검색 관련 함수 추가
    public void  filterList(ArrayList<OriginData> filteredList) {
        listData = filteredList;
        notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView1;
        private TextView textView2;

        private OriginData data;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = (TextView)itemView.findViewById(R.id.origin);
            textView2 = (TextView)itemView.findViewById(R.id.origin_writer);
        }

        void onBind(OriginData data) {
            try {
                this.data = data;
                textView1.setText(data.getTitle());
                textView2.setText(data.getWriter());
                itemView.setOnClickListener(this);
            }catch(NullPointerException e){
                e.printStackTrace();
            }

        }

        // 특정 출처 선택
        @Override
        public void onClick(View v) {
            if (data != null) {

                Intent intent = new Intent(v.getContext(), PostActivity.class);
                intent.putExtra("title", data.getTitle());
                intent.putExtra("writer", data.getWriter());

                ((Activity)(v.getContext())).setResult(RESULT_OK, intent); //v.getContext().startActivity(intent);
                ((Activity)(v.getContext())).finish();
            }
        }
    }
}