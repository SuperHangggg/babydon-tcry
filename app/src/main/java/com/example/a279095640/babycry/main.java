package com.example.a279095640.babycry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfrag);
    }
    public void  create(View view){
        Intent intent = new Intent(main.this,newbabyfile.class);
        startActivity(intent);
    }
}
