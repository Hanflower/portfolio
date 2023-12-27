package com.example.aram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeletePostRequest extends StringRequest{

    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://52.78.75.70/try_aram/aram_deletepost.php"; // 10.0.2.2:80
    private Map<String, String> map;

    public DeletePostRequest(int postID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("postID", postID + "");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }



}