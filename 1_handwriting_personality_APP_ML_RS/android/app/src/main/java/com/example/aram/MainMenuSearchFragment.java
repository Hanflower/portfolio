package com.example.aram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainMenuSearchFragment extends Fragment {
//    private SearchAdapter searchAdapter;

    private Button search_btn;


    //AutocompleteTextView 사용을 위한 변수 선언
    AutoCompleteTextView search_box;
    ArrayList<String> searchList;
    ArrayAdapter adapter;

    private String user_entered;

    //검색 히스토리
    private SearchtextAdapter searchtextAdapter;
    private ArrayList<SearchtextModel> SearchtextArrayList;
    private Button deletesearch_full;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView =(ViewGroup)inflater.inflate(R.layout.fragment_main_menu_search, container, false);

        // 검색창 및 버튼
        search_box = (AutoCompleteTextView) rootView.findViewById(R.id.search_edit_text);
        search_btn = rootView.findViewById(R.id.search_button);

        //검색어 자동 완성
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



        // 버튼 클릭하면 edittext 내용 가져와서 저장
        search_btn.setOnClickListener(new View.OnClickListener(){
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
                    public void onResponse(String response){
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

                ((MainActivity)getActivity()).replaceFragment(new SearchViewFragment());

            }
        });

        return rootView;
    }
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
    }
