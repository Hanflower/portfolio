package com.example.aram;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class InterestActivity extends AppCompatActivity {

    private Button type1, type2, type3, submit;
    private String contentTypeName1 = "";
    private String contentTypeName2 = "";
    private String contentTypeName3 = "";

    private boolean check1 = false;
    private boolean check2 = false;
    private boolean check3 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);

        type1 = findViewById(R.id.interest_type1);
        type2 = findViewById(R.id.interest_type2);
        type3 = findViewById(R.id.interest_type3);

        // 방금 회원가입한 userID 값 받아오기
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!check1) {
                    check1 = true;
                    type1.setBackgroundColor(getColor(R.color.primrose));
                    contentTypeName1 = "시";
                } else {
                    check1 = false;
                    type1.setBackgroundColor(getColor(R.color.classicBlue));
                    contentTypeName1 = "";
                }

            }
        });

        type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!check2) {
                    check2 = true;
                    type2.setBackgroundColor(getColor(R.color.primrose));
                    contentTypeName2 = "소설";
                } else {
                    check2 = false;
                    type2.setBackgroundColor(getColor(R.color.classicBlue));
                    contentTypeName2 = "";
                }

            }
        });

        type3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!check3) {
                    check3 = true;
                    type3.setBackgroundColor(getColor(R.color.primrose));
                    contentTypeName3 = "가사";
                } else {
                    check3 = false;
                    type3.setBackgroundColor(getColor(R.color.classicBlue));
                    contentTypeName3 = "";
                }

            }
        });

        //선택완료 버튼
        submit = findViewById(R.id.interest_select_button);
        submit.setOnClickListener(new View.OnClickListener() { // 선택 완료 버튼 클릭 시 수행
            @Override
            public void onClick(View view) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 등록에 성공한 경우
                                Toast.makeText(getApplicationContext(), "관심 장르 등록에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InterestActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                            else { // 등록에 실패한 경우
                                Toast.makeText(getApplicationContext(), "관심 장르 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함
                InterestRequest interestRequest = new InterestRequest(userID, contentTypeName1, contentTypeName2, contentTypeName3, responseListener);
                RequestQueue queue = Volley.newRequestQueue(InterestActivity.this);
                queue.add(interestRequest);
            }
        });

    }
}