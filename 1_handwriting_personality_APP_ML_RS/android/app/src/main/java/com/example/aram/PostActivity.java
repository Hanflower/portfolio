package com.example.aram;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

//서버 저장 activity

public class PostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    public static final String TAG = "PostActivity";
    ImageView close, image_added;
    TextView post;
    EditText origin, origin_writer, description, user_id, user_name;
    String imgString;
    Bitmap bitmap;

    private Uri pictureUri;

    //--카메라 작업
    private static final int PICK_GALLERY = 1;
    private static final int PICK_CAMERA = 2; // 카메라 선택시 인텐트로 보내는 값
    private static final int CAMERA_CROP = 3; // 갤러리 선택 시 인텐트로 보내는 값
    private static final int GALLERY_CROP = 4;

    //--

    //안드로이드에서 보낼 데이터를 받을 php 서버 주소 ...php?
    private static final String serverUrl="http://52.78.75.70/try_aram/aram_post.php";
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

        // -- 권한 체크
        boolean hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasCamPerm || !hasWritePerm)  // 권한 없을 시  권한설정 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        //Uri exposure 무시
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //--

        //X버튼 누르면 창 사라짐
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        //출처 입력하기, origin, origin_author 둘 다 눌러도 출처 검색창으로 이동
        origin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(PostActivity.this, OriginDialog.class);
                launcher.launch(intent);
            }
        });
        origin_writer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(PostActivity.this, OriginDialog.class);
                launcher.launch(intent);
            }
        });

        //--
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                    // 임시로 사용할 파일의 경로를 생성
//                    String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//                    pictureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//
//                    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, pictureUri);
//                    cameraIntent.putExtra("return-data", true);
//                    startActivityForResult(cameraIntent, CAMERA_CROP);
//            }
//        });
        //이미지 불러오기
        image_added.setOnClickListener(view -> {
            Dexter.withActivity(PostActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            //크롭된 이미지 가져와서 이미지뷰에 보여주기
//            case PICK_CAMERA:
//                if (resultCode == RESULT_OK && data.hasExtra("data")) { //데이터를 가지고 있는지 확인
//                    final Bundle extras = data.getExtras();
//                    if(extras != null)
//                    {
//                        Bitmap photo = extras.getParcelable("data"); //크롭한 이미지 가져오기
//                        image_added.setImageBitmap(photo); //이미지뷰에 넣기
//                    }
//                    // 임시 파일 삭제
//                    File f = new File(pictureUri.getPath());
//                    if(f.exists())
//                        f.delete();
//                    break;
//                }
//                break;
//
//            // 이미지 크롭
//            case CAMERA_CROP: {
//                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
//                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
//
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(pictureUri, "image/*");
//
//                intent.putExtra("outputX", 200); //크롭한 이미지 x축 크기
//                intent.putExtra("outputY", 200); //크롭한 이미지 y축 크기
//                intent.putExtra("aspectX", 1); //크롭 박스의 x축 비율
//                intent.putExtra("aspectY", 1); //크롭 박스의 y축 비율
//                intent.putExtra("scale", true);
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, PICK_CAMERA);
//                break;
//            }
//        }
//    }
    private void uploadDataToDB(){
        origin = (EditText) findViewById(R.id.origin);
        origin_writer = (EditText) findViewById(R.id.origin_writer);
        description = (EditText) findViewById(R.id.description);
        //추가.. 수정해야할 수 있음
//        user_id = (EditText)findViewById(R.id.user_id);
//
//        final String userID = user_id.getText().toString();
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
                //map.put("userName","ww"); // 필요없음
                SharedPreferences sharedPref = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
                String userID = sharedPref.getString("USER_ID", "");
                map.put("userID", userID);
                map.put("contentTitle", title);
                map.put("contentWriter", writer);
                map.put("postText",postText);
                map.put("upload", imgString);
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
        else if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            final Bundle extras = data.getExtras();
                    if(extras != null)
                    {
                        Bitmap photo = extras.getParcelable("data"); //크롭한 이미지 가져오기
                        image_added.setImageBitmap(photo); //이미지뷰에 넣기
                    }
                    // 임시 파일 삭제
                    File f = new File(pictureUri.getPath());
                    if(f.exists())
                        f.delete();
        }
        else if (requestCode == 3 && resultCode == RESULT_OK && data != null){
             //이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(pictureUri, "image/*");

                intent.putExtra("outputX", 200); //크롭한 이미지 x축 크기
                intent.putExtra("outputY", 200); //크롭한 이미지 y축 크기
                intent.putExtra("aspectX", 1); //크롭 박스의 x축 비율
                intent.putExtra("aspectY", 1); //크롭 박스의 y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_CAMERA);
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
