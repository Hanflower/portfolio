package com.example.aram;

import static android.view.accessibility.AccessibilityNodeInfo.CollectionInfo.SELECTION_MODE_NONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class MainMenuQuestFragment extends Fragment {

     public MainMenuQuestFragment() {
        // Required empty public constructor
    }


    public static MainMenuQuestFragment newInstance(String param1, String param2) {
        MainMenuQuestFragment fragment = new MainMenuQuestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_quest, container, false);
        Button big5button = rootView.findViewById(R.id.big5button);
        Button todaybutton = rootView.findViewById(R.id.todaybutton);



        //타이머+오늘의 글귀
        todaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),InformationActivity.class);
                startActivity(intent);
            }
        });

        //특성분석 버튼
        big5button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 아이디로부터 닉네임 받아오기
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject result = jsonObject.getJSONObject("userName");
                            String userName = result.getString("userName");

                            Intent intent = new Intent(getActivity(), Big5Activity.class);
                            intent.putExtra("userName", userName); // 추가

                            SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                            String userID = sharedPref.getString("USER_ID", "");
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                SharedPreferences sharedPref = view.getContext().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                String userID = sharedPref.getString("USER_ID", "");

                GetNameRequest getnameRequest = new GetNameRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(getnameRequest);
            }
        });


        // 날짜 받기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{

                    MaterialCalendarView calendarView = rootView.findViewById(R.id.calendarView);
                    calendarView.setSelectedDate(CalendarDay.today());


                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data_array = jsonObject.getJSONArray("dates");


                    // CalendarDay
                    ArrayList<CalendarDay> calendarDays = new ArrayList<>();
                    for(int i=0 ; i < jsonObject.getJSONArray("dates").length() ; i++) {
                        CalendarDay day = CalendarDay.from(convertStringToDate(data_array.getString(i)));
                        calendarDays.add(day);
                    }

                    calendarView.addDecorator(new HighlightDecorator(calendarDays));
                    calendarView.addDecorator(new OneDayDecorator());
                    calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        };

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("USER_ID", "");
        CalendarRequest calendarRequest = new CalendarRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(calendarRequest);

        return rootView;
    }

    public class HighlightDecorator implements DayViewDecorator {

        private final Drawable highlightDrawable;
        private HashSet<CalendarDay> dates;

        public HighlightDecorator(Collection<CalendarDay> dates) {
            highlightDrawable = getActivity().getResources().getDrawable(R.drawable.cal_circle, getActivity().getTheme());
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(highlightDrawable);
        }
    }

    public class OneDayDecorator implements DayViewDecorator {

        private final Drawable highlightDrawable;

        private CalendarDay date;
        private MaterialCalendarView calendarView;

        public OneDayDecorator() {
            highlightDrawable = getActivity().getResources().getDrawable(R.drawable.today_circle, getActivity().getTheme());
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //view.addSpan(new StyleSpan(Typeface.BOLD));
            //view.addSpan(new RelativeSizeSpan(1.4f));
            view.setBackgroundDrawable(highlightDrawable);
        }
    }

    //문자로부터 날짜 변환
    public static Date convertStringToDate(String date) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2;
    }
}