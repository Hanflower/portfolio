package com.example.aram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchtextRequest extends StringRequest{

    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://52.78.75.70/try_aram/aram_searchtext.php"; // 10.0.2.2:80
    private Map<String, String> map;

    public SearchtextRequest(String userID, String searchText, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("searchText", searchText);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }



}