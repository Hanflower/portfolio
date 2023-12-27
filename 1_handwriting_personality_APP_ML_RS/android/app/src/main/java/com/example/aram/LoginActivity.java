package com.example.aram;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONException;
        import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText id, pass;
    private Button btn_login, btn_register;
    private CheckBox id_save;
    private Boolean check_save;

//    private CheckBox logincb;
//
//    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = findViewById(R.id.user_id);
        pass = findViewById(R.id.user_password);
        btn_login = (Button)findViewById(R.id.loginbutton);
        btn_register = (Button)findViewById(R.id.signin);
//        appData = getSharedPreferences("appData",MODE_PRIVATE);

        // 체크박스
        id_save = findViewById(R.id.id_save);
        check_save = id_save.isChecked();

        SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
        String checked = sharedPref.getString("ID_CHECK", "empty");
        if (checked.equals("true")){
            id_save.setChecked(true);
            id.setText(sharedPref.getString("USER_ID", ""));
        } else {
            id_save.setChecked(false);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("USER_ID");
            editor.commit();
        }


        btn_register.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼을 클릭하면, 회원가입 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = id.getText().toString();
                String userPassword = pass.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 로그인에 성공한 경우
                                String userID = jsonObject.getString("userID");

                                // 사용자 아이디 값 저장 (따로 정의한 MySharedPreferences.java 활용하지 않는 버전)
                                SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("USER_ID", userID);
                                editor.commit();


                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
//                                intent.putExtra("userPass", userPassword);
                                startActivity(intent);
                                finish();

                            }
                            else { // 로그인에 실패한 경우
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userID,userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

        id_save.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_save = id_save.isChecked();

                SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("ID_CHECK", String.valueOf(check_save));
                editor.commit();
            }
        }) ;

    }



}