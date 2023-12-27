package com.example.aram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InterestRequest extends StringRequest{

    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://52.78.75.70/try_aram/aram_interest.php";
    private Map<String, String> map;

    public InterestRequest(String userID, String contentTypeName1, String contentTypeName2, String contentTypeName3, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("contentTypeName1", contentTypeName1);
        map.put("contentTypeName2", contentTypeName2);
        map.put("contentTypeName3", contentTypeName3);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }



}