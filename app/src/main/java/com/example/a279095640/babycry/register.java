package com.example.a279095640.babycry;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class register extends AppCompatActivity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private EditText register_username;
    private EditText register_password;
    private EditText confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_username=(EditText)findViewById(R.id.register_username);
        register_password=(EditText)findViewById(R.id.register_password);
        confirm_password = (EditText)findViewById(R.id.confirm_password);
    }


    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    Toast.makeText(register.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                    break;

                case ERROR:
                    Toast.makeText(register.this,"注册失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };
    public void regist(View view){
        final String acc = register_username.getText().toString();
        final String psd = register_password.getText().toString();
        final String confirm = confirm_password.getText().toString();

        if(TextUtils.isEmpty(acc)||TextUtils.isEmpty(psd)){
            Toast.makeText(this, "用户和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!psd.equals(confirm)){
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
                    //http://localhost/xampp/android/login.php
                    //区别1、url的路径不同
                    String path = "http://59.78.15.207:8080/register.php";
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
                        String suc = "注册成功";
                        result= URLEncoder.encode(result,"UTF-8");
                        result =result.substring(9,result.length()-2);
                        suc   = URLEncoder.encode(suc,"UTF-8");
                        if(suc.equals(result))
                        {
                            finish();
                        }
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
