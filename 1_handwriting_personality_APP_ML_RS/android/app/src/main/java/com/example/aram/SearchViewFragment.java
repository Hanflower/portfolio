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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchViewFragment extends Fragment {

    private SearchAdapter searchAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tabCurrentIdx = 0;
    private ImageView back, search_btn;

    //자동검색어 관련 선언하기
    private TextView search_box;
//    private ArrayList<String> searchList;
//    ArrayAdapter adapter;

    private String user_entered;

    @Override
    public void onResume(){
        super.onResume();
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_view, container, false);

        //검색창 및 버튼
        back = view.findViewById(R.id.back);
        search_btn = view.findViewById(R.id.search_btn);
        search_box = view.findViewById(R.id.search_edit_text);

        //검색어 저장하여 searchbox에 나타내기
        SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        user_entered = sharedPref.getString("SEARCH_TEXT", "empty");
        search_box.setText(user_entered);

        //자동 검색
        //fragment 전환하여 새 창에서 검색되도록 함
//        searchList = new ArrayList<>();
//        adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_dropdown_item_1line,searchList);
//        settingList();

        search_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).replaceFragment(new SaveTextFragment());
            }
        });
//        //엔터키 입력해서 키보드 내리고 검색되게하기
//        search_box.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent event) {
//
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(search_box.getWindowToken(), 0);    //hide keyboard
//
//                    user_entered = search_box.getText().toString();
//                    SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("SEARCH_TEXT", user_entered);
//                    editor.commit();
//
//                    ((MainActivity)getActivity()).replaceFragment(new SearchViewFragment());
//                    return true;
//                }
//                return false;
//            }
//        });

        tabLayout = view.findViewById(R.id.search_tab_Layout);
        viewPager = view.findViewById(R.id.search_view_Pager);
        //피드 구성하는 탭레이아웃 + 뷰페이저
        tabLayout.addTab(tabLayout.newTab().setText("게시글"));
        tabLayout.addTab(tabLayout.newTab().setText("사용자"));
        //커스텀 어댑터 생성
        searchAdapter = new SearchAdapter(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(searchAdapter);
        viewPager.setCurrentItem(tabCurrentIdx);
        viewPager.setSaveEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabCurrentIdx = tab.getPosition();
                onResume();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //뒤로 가기, mainActivity replaceFragment 수정하여 이전 검색결과 저장하게 만들었음
                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchViewFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }


}


