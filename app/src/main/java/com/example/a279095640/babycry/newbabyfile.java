package com.example.a279095640.babycry;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class newbabyfile extends AppCompatActivity {
    private EditText babyid;
    private EditText weight;
    private EditText name;
    private String birthday;
    private String sexstring;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbabyfile);
        TextView birthdate = (TextView) findViewById(R.id.birthdate);
        RadioGroup radiogroup = (RadioGroup) this.findViewById(R.id.radioGroup);
        babyid = (EditText) findViewById(R.id.id);
        weight = (EditText) findViewById(R.id.weight);
        name = (EditText) findViewById(R.id.name);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        System.out.println(username);
        birthdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Calendar c = Calendar.getInstance();
                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(newbabyfile.this,
                        // 绑定监听器
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                TextView show = (TextView) findViewById(R.id.birthdate);
                                show.setText(year + "年" + monthOfYear
                                        + "月" + dayOfMonth + "日");
                                birthday = Integer.toString(year) + '-' + Integer.toString(monthOfYear) + '-' + Integer.toString(dayOfMonth);

                            }
                        }
                        // 设置初始日期
                        , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) newbabyfile.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                sexstring = rb.getText().toString();
            }
        });
    }
    protected static final int ERROR = 2;
    protected static final int SUCCESS = 1;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    Toast.makeText(newbabyfile.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                    break;

                case ERROR:
                    Toast.makeText(newbabyfile.this,"创建失败请重试", Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };
    public void confirm(View view){
        final String btd = birthday;
        final String sex = sexstring;
        final String acc = username;
        final String id = babyid.getText().toString();
        final String wt = weight.getText().toString();
        final String nm = name.getText().toString();

        if(TextUtils.isEmpty(btd)||TextUtils.isEmpty(sex)||TextUtils.isEmpty(id)||TextUtils.isEmpty(wt)||TextUtils.isEmpty(nm)){
            Toast.makeText(this, "信息不完整！", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean result=id.matches("[0-9]+");
        if (!result) {
            Toast.makeText(this, "id不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
                    //http://localhost/xampp/android/login.php
                    //区别1、url的路径不同
                    String path = "http://192.168.1.113:8080/createbaby.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "&username="+acc+"&babyid="+id+"&birthdate="+btd+"&name="+URLEncoder.encode(nm,"UTF-8")+ "&sex="+URLEncoder.encode(sex,"UTF-8")+"&weight="+wt+"&button=";
                    System.out.println(acc);
                    System.out.println(id);
                    System.out.println(btd);
                    System.out.println(sex);
                    System.out.println(wt);
                    conn.setRequestProperty("Content-Length", data.length()+"");//数据的长度
                    //区别4、记得设置把数据写给服务器
                    conn.setDoOutput(true);//设置向服务器写数据
                    byte[] bytes = data.getBytes();
                    conn.getOutputStream().write(bytes);//把数据以流的方式写给服务器
                    int code = conn.getResponseCode();
                    System.out.println(code);
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        String  result = StreamTools.readStream(is);
                        Message mas= Message.obtain();
                        mas.what = SUCCESS;
                        mas.obj = result;
                        String suc = "创建成功";
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
