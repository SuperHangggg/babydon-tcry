package com.example.a279095640.babycry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;
    private String uploadUrl = "http://202.120.62.152:8080/";
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
        readAccount();
        if(!account.getText().toString().isEmpty()&&!password.getText().toString().isEmpty())
        {
            Message mas= Message.obtain();
            mas.what = SUCCESS;
            String suc = account.getText().toString()+"已经成功登录";
            mas.obj = suc;
            handler.sendMessage(mas);
            Intent intent = new Intent(MainActivity.this, main.class);
            intent.putExtra("username", account.getText().toString());
            startActivity(intent);}

    }
    public void readAccount() {

        //创建SharedPreferences对象
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);

        //获得保存在SharedPredPreferences中的用户名和密码
        String username = sp.getString("username", "");
        String pass = sp.getString("password", "");

        //在用户名和密码的输入框中显示用户名和密码
        account.setText(username);
        password.setText(pass);
    }
    public void login(View view){
        final String acc = account.getText().toString();
        final String psd = password.getText().toString();
        CheckBox cb = (CheckBox)findViewById(R.id.checkBox);

        if(TextUtils.isEmpty(acc)||TextUtils.isEmpty(psd)){
            Toast.makeText(this, "用户和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(cb.isChecked()) {

            //创建sharedPreference对象，info表示文件名，MODE_PRIVATE表示访问权限为私有的
            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);

            //获得sp的编辑器
            SharedPreferences.Editor ed = sp.edit();

            //以键值对的显示将用户名和密码保存到sp中
            ed.putString("username", acc);
            ed.putString("password", psd);

            //提交用户名和密码
            ed.commit();
        }
        new Thread(){
            public void run(){
                try {
                    //http://localhost/xampp/android/login.php
                    //区别1、url的路径不同
                    String path = uploadUrl+"login.php";
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
                            Intent intent = new Intent(MainActivity.this, main.class);
                            intent.putExtra("username", acc);
                            startActivity(intent);
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
    public void  click_register(View view){
        Intent intent = new Intent(MainActivity.this, register.class);
        startActivity(intent);
    }
    public void  findpass(View view){
        Intent intent = new Intent(MainActivity.this, forget.class);
        startActivity(intent);
    }


}
