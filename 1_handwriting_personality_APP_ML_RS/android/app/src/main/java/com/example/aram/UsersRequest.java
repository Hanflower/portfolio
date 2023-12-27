package com.example.aram;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class UsersRequest extends StringRequest{

    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://52.78.75.70/try_aram/aram_users.php"; // 3.130.40.105

    public UsersRequest(Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
    }


}