package com.example.a279095640.babycry;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class data extends AppCompatActivity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private String uploadUrl = "http://202.120.62.152:8080/";
    private String username;
    private String man;
    private String woman;
    private TextView recorded_woman;
    private TextView recorded_man;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    parseJson2String((String)msg.obj);
                    System.out.println(msg.obj);
                    man = man+"例";
                    woman = woman+"例";
                    recorded_man.setText(String.valueOf(man));
                    recorded_woman.setText(String.valueOf(woman));
                    break;

                case ERROR:
                    Toast.makeText(data.this,"查询失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        username = getIntent().getStringExtra("username");
        recorded_woman = (TextView)findViewById(R.id.recorded_woman);
        recorded_man = (TextView)findViewById(R.id.recorded_man);
        recorded_data();
    }
    private void parseJson2String(String data){
        try{

            JSONObject tmpObj = new JSONObject(data);

            man = tmpObj.getString("man");
            System.out.println(man);
            woman = tmpObj.getString("woman");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void recorded_data(){
        final String acc = username;
        new Thread(){
            public void run(){
                try {
                    //http://localhost/xampp/android/login.php
                    //区别1、url的路径不同
                    String path = uploadUrl+"data.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "&username="+acc+"&button=";
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
                        System.out.println(result);

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
