package com.example.aram;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SaveTextFragment extends Fragment {

    //자동완성
    private AutoCompleteTextView search_box;
    ArrayList<String> searchList;
    ArrayAdapter adapter;
    private String user_entered;
    private ImageView back, search_btn;

    //검색 히스토리
    private SearchtextAdapter searchtextAdapter;
    private ArrayList<SearchtextModel> SearchTextArrayList;
    private Button deletesearch_full;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_text, container, false);

        //검색창 및 버튼
        back = view.findViewById(R.id.back);
        search_btn = view.findViewById(R.id.search_btn);
        search_box = (AutoCompleteTextView) view.findViewById(R.id.search_edit_text);

        //검색어 저장하여 searchbox에 나타내기
        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        user_entered = sharedPref.getString("SEARCH_TEXT", "empty");
        search_box.setText(user_entered);

        //검색 히스토리
        SearchTextArrayList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchtextAdapter = new SearchtextAdapter();
        recyclerView.setAdapter(searchtextAdapter);

        getSearchtextData();

        //검색 히스토리 전체 지우기
        deletesearch_full = view.findViewById(R.id.delete_fullsearchtext);
        deletesearch_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 검색어 전체 지우기 성공한 경우
                            }
                            else { // 검색어 하나 지우기 실패한 경우
                                Log.e("검색어 전체 지우기 버튼", "실패");
                                return;
                            }

                            // adapter의 값이 변경되었다는 것을 알려줍니다.
                            searchtextAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함
                SharedPreferences sharedPref = getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                String userID = sharedPref.getString("USER_ID", "");

                SearchtextDeleteFullRequest searchtextdeletefullrequest = new SearchtextDeleteFullRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(searchtextdeletefullrequest);

            }
        });

        //자동 검색
        searchList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_dropdown_item_1line,searchList);
        settingList();

        //엔터키 입력해서 키보드 내리고 검색되게하기
        search_box.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_box.getWindowToken(), 0);    //hide keyboard

                    user_entered = search_box.getText().toString();
                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("SEARCH_TEXT", user_entered);
                    editor.commit();

                    ((MainActivity)getActivity()).replaceFragment(new SearchViewFragment());
                    return true;
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //뒤로 가기, mainActivity replaceFragment 수정하여 이전 검색결과 저장하게 만들었음
                getActivity().getSupportFragmentManager().beginTransaction().remove(SaveTextFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        // 버튼 클릭하면 edittext 내용 가져와서 저장
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_entered = search_box.getText().toString();

                SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("SEARCH_TEXT", user_entered);
                editor.commit();

                ///// DB USER_SEARCH TABLE에 검색 기록 삽입
                String userID = sharedPref.getString("USER_ID", "");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 등록에 성공한 경우
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함
                SearchtextRequest searchtextRequest = new SearchtextRequest(userID, user_entered, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(searchtextRequest);
                /////

                ((MainActivity) getActivity()).replaceFragment(new SearchViewFragment());

            }
        });

        return view;
    }
    //자동검색
    private void settingList() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("users");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = result.getJSONObject(i);
                        UserFollowModel model = new UserFollowModel();
                        searchList.add(item.getString("userID"));
                        searchList.add(item.getString("userName"));
                    }
                    search_box.setThreshold(1);
                    search_box.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        UsersRequest usersRequest = new UsersRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(usersRequest);
    }
    //검색 히스토리
    private void getSearchtextData() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("search");

                    for(int i=0; i<result.length(); i++){
                        JSONObject item = result.getJSONObject(i);

                        // 각 List의 값들을 data 객체에 set 해줍니다.
                        SearchtextModel data = new SearchtextModel();
                        data.setSearchText(item.getString("searchText"));
                        data.setSearchDate(item.getString("searchDate"));

                        // 각 값이 들어간 data를 adapter에 추가합니다.
                        searchtextAdapter.addItem(data);
                        searchtextAdapter.notifyDataSetChanged();
                    }

                    // adapter의 값이 변경되었다는 것을 알려줍니다.


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");

        SearchtextGetRequest searchtextgetrequest = new SearchtextGetRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(searchtextgetrequest);

    }
    /////
}


