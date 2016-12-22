package com.example.a279095640.babycry;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
    private EditText company;
    Spinner city;
    Spinner province;
    String tProvince;
    String tCity;
    private String uploadUrl = "http://202.120.62.152:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_username=(EditText)findViewById(R.id.register_username);
        register_password=(EditText)findViewById(R.id.register_password);
        confirm_password = (EditText)findViewById(R.id.confirm_password);
        company = (EditText)findViewById(R.id.company);
        province = (Spinner) findViewById(R.id.sp_province);
        city = (Spinner) findViewById(R.id.sp_city);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.province,
                R.layout.spinner_checked_text);
        province.setAdapter(adapter);
        province.setOnItemSelectedListener(new spinnerItemSelected());
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tCity = city.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
    });
    }

    class spinnerItemSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinner = (Spinner) parent;
            String pro = (String) spinner.getItemAtPosition(position);
            tProvince = province.getSelectedItem().toString();
            // 处理省的市的显示
            ArrayAdapter<CharSequence> cityadapter = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.def, R.layout.spinner_checked_text);
            if (pro.equals("北京")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.北京,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("天津")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.天津,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("河北")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.河北,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("山西")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.山西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("内蒙古")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.内蒙古,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("辽宁")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.辽宁,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("吉林")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.吉林,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("黑龙江")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.黑龙江,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("上海")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.上海,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("江苏")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.江苏,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("浙江")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.浙江,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("安徽")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.安徽,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("福建")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.福建,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("江西")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.江西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("山东")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.山东,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("河南")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.河南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("湖北")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.湖北,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("湖南")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.湖南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("广东")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.广东,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("广西")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.广西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("海南")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.海南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("重庆")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.重庆,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("四川")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.四川,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("贵州")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.贵州,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("云南")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.云南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("西藏")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.西藏,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("陕西")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.陕西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("甘肃")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.甘肃,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("青海")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.青海,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("宁夏")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.宁夏,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("新疆")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.新疆,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("台湾")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.台湾,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("香港")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.香港,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("澳门")) {
                cityadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.澳门,
                        R.layout.spinner_checked_text);
            }

            city.setAdapter(cityadapter);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
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
        final String cmp = company.getText().toString();
        final String pro = tProvince;
        final String city = tCity;
        String default_city = "-城市-";
        String default_pro = "-省份-";
            System.out.println(city);
        System.out.println(pro);
        System.out.println(city.equals(default_city));
        System.out.println(pro.equals(default_pro));
        if(TextUtils.isEmpty(acc)||TextUtils.isEmpty(psd)||TextUtils.isEmpty(cmp)||pro.equals(default_pro)||city.equals(default_city)){
            Toast.makeText(this, "信息不完整！", Toast.LENGTH_SHORT).show();
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
                    String path = uploadUrl+"register.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "&username="+ URLEncoder.encode(acc,"UTF-8")+"&password="+ URLEncoder.encode(psd,"UTF-8")+ "&company="+URLEncoder.encode(cmp,"UTF-8")+"&province="+URLEncoder.encode(pro,"UTF-8")+"&city="+URLEncoder.encode(city,"UTF-8")+"&button=";
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
