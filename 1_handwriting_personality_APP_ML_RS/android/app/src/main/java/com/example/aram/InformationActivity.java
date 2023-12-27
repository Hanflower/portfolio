package com.example.aram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

public class InformationActivity extends AppCompatActivity {
   // TextView todayquote;
    ImageView close;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

      // todayquote = findViewById(R.id.today_quote);
        next = findViewById(R.id.next_button);
        close = findViewById(R.id.close);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this,StopWatchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this,MainMenuQuestFragment.class);
                finish();
            }
        });


    }
}
