package com.example.a279095640.babycry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private EditText account;
    private EditText password;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    Toast.makeText(MainActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                    break;

                case ERROR:
                    Toast.makeText(MainActivity.this,"登录失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);


    }
    public void login(View view){
        final String acc = account.getText().toString();
        final String psd = password.getText().toString();

        if(TextUtils.isEmpty(acc)||TextUtils.isEmpty(psd)){
            Toast.makeText(this, "用户和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
                    //http://localhost/xampp/android/login.php
                    //区别1、url的路径不同
                    String path = "http://192.168.1.113:8080/login.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "&username="+acc+"&password="+psd+"&button=";
                    ;
                    conn.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                    //区别4、记得设置把数据写给服务器
                    conn.setDoOutput(true);//设置向服务器写数据
                    byte[] bytes = data.getBytes();
                    conn.getOutputStream().write(bytes);//把数据以流的方式写给服务器
                    int code = conn.getResponseCode();
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        String  result = StreamTools.readStream(is);
                        Message mas= Message.obtain();
                        mas.what = SUCCESS;
                        mas.obj = result;
                        handler.sendMessage(mas);
                        String suc = acc+"已经成功登录";
                        result= URLEncoder.encode(result,"UTF-8");
                        result =result.substring(9,result.length()-2);
                        suc   = URLEncoder.encode(suc,"UTF-8");
                        if(suc.equals(result))
                        {
                            finish();
                        }
                        handler.sendMessage(mas);

                    }else{
                        Message mas = Message.obtain();
                        mas.what = ERROR;
                        handler.sendMessage(mas);
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Message mas = Message.obtain();
                    mas.what = ERROR;
                    handler.sendMessage(mas);
                }
            }
        }.start();

    }



}
