package com.example.aram;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.EditText;

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


public class OriginDialog extends AppCompatActivity {

    private OriginAdapter adapter;
    ArrayList<OriginData> OriginArrayList, filteredList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // Dialog 위에 타이틀없이

        setContentView(R.layout.dialog_origin); //
        Window window = getWindow(); // 크기 조절
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT); // 크기 조절

        OriginArrayList = new ArrayList<>();
        filteredList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new OriginAdapter();
        recyclerView.setAdapter(adapter);

        getOriginData(); // 처음에 쭉 불러와서 배치

        // 검색
        EditText searchBox = findViewById(R.id.search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchText = searchBox.getText().toString();
                searchFilter(searchText);

            }
        });

    }

    private void getOriginData() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("contents");

                    for(int i=0; i<result.length(); i++){

                        JSONObject item = result.getJSONObject(i);

                        // 각 List의 값들을 data 객체에 set 해줍니다.
                        OriginData data = new OriginData();
                        data.setTitle(item.getString("contentTitle"));
                        data.setWriter(item.getString("contentWriter"));

                        // 각 값이 들어간 data를 adapter에 추가합니다.
                        adapter.addItem(data);

                        // 검색을 위해 리스트배열에 저장 (내부적으로)
                        OriginArrayList.add(data);

                    }

                    // adapter의 값이 변경되었다는 것을 알려줍니다.
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함
        OriginRequest originRequest = new OriginRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(OriginDialog.this);
        queue.add(originRequest);

    }

    public void searchFilter(String searchText) {
        filteredList.clear();

        // 제목이랑 작가 동시 검색
        for (int i = 0; i < OriginArrayList.size(); i++) {
            if (OriginArrayList.get(i).getTitle().contains(searchText) || OriginArrayList.get(i).getWriter().contains(searchText)) {
                filteredList.add(OriginArrayList.get(i));
            }
        }

        adapter.filterList(filteredList);
    }

}

