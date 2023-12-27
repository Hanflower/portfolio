package com.example.aram;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {
    private TextView back;
    private EditText name,id,pw,pw2,email, user_password, user_password2;
    private Button pwcheck, submit, idcheckbutton, visible, visible2;
    private RadioGroup gender_group;
    private AlertDialog.Builder dialog;
    private boolean validate = false;

    public int getGender(View v){

        RadioButton female = (RadioButton)findViewById(R.id.gender_female);
        RadioButton male = (RadioButton)findViewById(R.id.gender_male);
        int Gender = 1;


        /**"남자"radio button이 체크되면 => gender값에 남자
         "여자"radio button이 체크되면 => gender값에 여자 **/
        if(female.isChecked()){


        }else if(male.isChecked()){
            Gender = 0;
        }
        return Gender;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //뒤로 가기 버튼
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed() );

        //기입 항목
        name = findViewById(R.id.user_name);
        id=findViewById(R.id.user_id);
        pw=findViewById(R.id.user_password);
        pw2=findViewById(R.id.user_password2);
        email=findViewById(R.id.user_email);

        //비밀번호 확인 버튼
        pwcheck = findViewById(R.id.pwcheckbutton);
        pwcheck.setOnClickListener(v -> {
            if(pw.getText().toString().equals(pw2.getText().toString())){
                pwcheck.setText("일치");
                pwcheck.setBackgroundColor(getResources().getColor(R.color.primrose));
            }else{
                Toast.makeText(RegisterActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });



        // 비밀번호 보이고 안 보이고
        user_password = findViewById(R.id.user_password);
        user_password2 = findViewById(R.id.user_password2);
        visible = findViewById(R.id.visible);
        visible2 = findViewById(R.id.visible2);

        visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                if (view.isSelected()) {
                    user_password.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    user_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        visible2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                if (view.isSelected()) {
                    user_password2.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    user_password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        //id 중복 확인 버튼
        idcheckbutton = findViewById(R.id.idcheckbutton);
        idcheckbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String UserID = id.getText().toString();
                if(validate){
                    return; //검증완료
                }

                if(UserID.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인",null);
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인",null);
                                dialog.show();
                                id.setEnabled(false);//아이디값 고정
                                validate = true;//검증 완료
                                idcheckbutton.setBackgroundColor(getResources().getColor(R.color.primrose));
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인",null);
                                dialog.show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequestActivity validateRequest = new ValidateRequestActivity(UserID,responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }

        });

        //회원가입 완료 버튼
        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼 클릭 시 수행
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 가져온다
                String userID = id.getText().toString();
                String userPassword = pw.getText().toString();
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                int userGender = getGender(view);



                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) { // 회원 등록에 성공한 경우
                                Intent intent = new Intent(RegisterActivity.this, InterestActivity.class);
                                intent.putExtra("userID", userID); // 추가
                                startActivity(intent);

                            }
                            else { // 회원 등록에 실패한 경우
                                Toast.makeText(getApplicationContext(), "회원 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userEmail, userGender, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });

    }
}