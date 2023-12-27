package com.example.aram;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TodayPostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    public static final String TAG = "TodayPostActivity";
    private boolean isRequestSent=false;
    ImageView back, image_added;
    TextView post;
    EditText origin, origin_writer, description;
    String imgString;
    Bitmap bitmap;

    //안드로이드에서 보낼 데이터를 받을 php 서버 주소 ...php?
    private static final String serverUrl="http://52.78.75.70/try_aram/aram_post.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_post);

        back = findViewById(R.id.back);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        origin = findViewById(R.id.origin);
        origin_writer = findViewById(R.id.origin_writer);

        // 오늘의 글귀에 해당되는 출처 적어두기
        origin.setText("사과");
        origin_writer.setText("박준기");


        //<-버튼 누르면 타이머 창으로 이동
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TodayPostActivity.this,StopWatchActivity.class));
                finish();
            }
        });

        //이미지 불러오기
        image_added.setOnClickListener(view -> {
            Dexter.withActivity(TodayPostActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Browse Image"), 1);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();


        });
        //post버튼 누르면 이미지 업로드 및 서버로 보내기
        post.setOnClickListener(view -> {
//            uploadDataToDB();
//            Intent intent = new Intent(TodayPostActivity.this, MainActivity.class);
//            finish();
            if(!isRequestSent){
                uploadDataToDB();
                isRequestSent=true;

                Intent intent = new Intent(TodayPostActivity.this, MainActivity.class);
                finish();

            }
        });
    }
    //출처 lanucher
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Intent intent = result.getData();
                        String title = intent.getStringExtra("title");
                        String writer = intent.getStringExtra("writer");
                        origin.setText(title);
                        origin_writer.setText(writer);
                    }
                }
            });

    private void uploadDataToDB(){
        origin = (EditText) findViewById(R.id.origin);
        origin_writer = (EditText) findViewById(R.id.origin_writer);
        description = (EditText) findViewById(R.id.description);

        //추가.. 수정해야할 수 있음
//        user_id = (EditText)findViewById(R.id.user_id);
//        user_name=(EditText) findViewById(R.id.user_name);
//
//        final String userID = user_id.getText().toString();
//        final String userName = user_name.getText().toString();
        //-----
        final String title = origin.getText().toString().trim();
        final String writer = origin_writer.getText().toString().trim();
        final String postText = description.getText().toString().trim();
        float time = getIntent().getFloatExtra("time",0);
        int dailyQuest = 1;
        StringRequest request = new StringRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                origin.setText("");
//                origin_author.setText("");
                description.setText("");
                image_added.setImageResource(R.mipmap.ic_launcher);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               return;
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map = new HashMap<>();
                // 1번 인자는 PHP 파일의 $_POST['']; 부분과 똑같이 해줘야 한다
                //map.put("userID","ww");
                //map.put("userName","ww"); // 필요없음
                SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                String userID = sharedPref.getString("USER_ID", "");
                map.put("userID",userID);
                map.put("contentTitle", title);
                map.put("contentWriter", writer);
                map.put("postText",postText);
                map.put("upload", imgString);
                map.put("dailyQuest",dailyQuest+"");
                map.put("time",time+"");

                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(request);
    }
    @NonNull
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                image_added.setImageBitmap(bitmap);
                encodeBitmapImage(bitmap);
            }   catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void encodeBitmapImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        imgString = android.util.Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
    }
}

