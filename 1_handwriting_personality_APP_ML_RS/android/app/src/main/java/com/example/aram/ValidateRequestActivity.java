package com.example.aram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequestActivity extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://52.78.75.70/try_aram/aram_userValidate.php";
    private Map<String, String> map;

    public ValidateRequestActivity(String userID, Response.Listener<String>listner){
        super(Method.POST, URL, listner,null);

        map = new HashMap<>();
        map.put("userID",userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return map;
    }
}
