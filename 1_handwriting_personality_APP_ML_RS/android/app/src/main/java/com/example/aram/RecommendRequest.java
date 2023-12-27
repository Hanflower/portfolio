package com.example.aram;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//추천 탐라 서버 데이터 요청
public class RecommendRequest extends StringRequest {
    final static private String URL = "http://52.78.75.70/try_aram/aram_recommend_contentbased2.php"; // 3.130.40.105


    private Map<String, String> map;

    public RecommendRequest(String userID, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
