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
import com.bumptech.glide.Glide;
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

//서버 저장 activity

public class UpdatePostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    public static final String TAG = "PostActivity";
    ImageView close, image_added;
    TextView post;
    EditText origin, origin_writer, description, user_id, user_name;
    String userID, imgString, postText, title, writer;
    int postID;
    Bitmap bitmap;

    private static final String serverUrl="http://52.78.75.70/try_aram/aram_updatepost.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        origin = findViewById(R.id.origin);
        origin_writer = findViewById(R.id.origin_writer);

        // 기존 값 받아오기
        Intent rawintent = getIntent();

        userID = rawintent.getStringExtra("userID");
        postID = rawintent.getIntExtra("postID", 0);
        postText = rawintent.getStringExtra("postText");
        imgString = rawintent.getStringExtra("upload");
        title = rawintent.getStringExtra("contentTitle");
        writer = rawintent.getStringExtra("contentWriter");

        // 기존 내용 배치
        description.setText(postText);
        origin.setText(title);
        origin_writer.setText(writer);

        Glide.with(getApplicationContext()).load(imgString).into(image_added);

        //X버튼 누르면 창 사라짐
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdatePostActivity.this, MainActivity.class));
                finish();
            }
        });

        //출처 입력하기, origin, origin_author 둘 다 눌러도 출처 검색창으로 이동
        origin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdatePostActivity.this, OriginDialog.class);
                launcher.launch(intent);
            }
        });
        origin_writer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UpdatePostActivity.this, OriginDialog.class);
                launcher.launch(intent);
            }
        });

        //이미지 불러오기
        image_added.setOnClickListener(view -> {
            Dexter.withActivity(UpdatePostActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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
            uploadDataToDB();
            Intent intent = new Intent(UpdatePostActivity.this, MainActivity.class);
            finish();
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
                SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                String userID = sharedPref.getString("USER_ID", "");
                map.put("userID", userID);
                map.put("postID", postID + "");
                map.put("postText", postText);
                map.put("upload", imgString);
                map.put("contentTitle", title);
                map.put("contentWriter", writer);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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
        imgString = Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
    }
}
