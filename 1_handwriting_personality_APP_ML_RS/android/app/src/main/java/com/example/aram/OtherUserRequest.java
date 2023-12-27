package com.example.aram;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class OtherUserRequest extends StringRequest {
    final static private String URL = "http://52.78.75.70/try_aram/aram_myinfo.php"; // 10.0.2.2:80
    private Map<String, String> map;

    public OtherUserRequest(String userID, String followingID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("followingID", followingID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
