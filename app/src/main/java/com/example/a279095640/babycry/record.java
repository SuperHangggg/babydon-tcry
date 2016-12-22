package com.example.a279095640.babycry;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class record extends AppCompatActivity {
    private String uploadUrl = "http://202.120.62.152:8080/";
    private String usr;
    private String reasons;
    private String date;
    private EditText otherreason;
    private EditText babyid;
    private EditText weight;
    private MediaPlayer mediaPlayer;
    // 多媒体录制器
    private MediaRecorder mediaRecorder = new MediaRecorder();
    // 音频文件
    private File audioFile;

    // 传给Socket服务器端的上传和下载标志
    private final int UP_LOAD = 1;
    private final int DOWN_LOAD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        usr = intent.getStringExtra("username");
        weight = (EditText)findViewById(R.id.weight_today);
        otherreason = (EditText)findViewById(R.id.inputreason);
        babyid = (EditText)findViewById(R.id.uploadbabyid);
        RadioGroup radiogroup = (RadioGroup) this.findViewById(R.id.radioReasons);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String [] date_string = getResources().getStringArray(R.array.date);
                date = date_string[pos];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) record.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                reasons = rb.getText().toString();
            }
        });

    }

    public void onClick(View view) {
        try {
            String msg = "";
            switch (view.getId()) {
                // 开始录音
                case R.id.startrecording:
                    if (!EnvironmentShare.haveSdCard()) {
                        Toast.makeText(this, "SD不存在，不正常录音！！", Toast.LENGTH_LONG).show();
                    } else {
                        // 设置音频来源(一般为麦克风)
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        // 设置音频输出格式（默认的输出格式）
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        // 设置音频编码方式（默认的编码方式）
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                        // 创建一个临时的音频输出文件.record_是文件的前缀名 .amr是后缀名
                        audioFile = File.createTempFile("record_", ".mp3", EnvironmentShare.getAudioRecordDir());
                        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

                        // 准备并且开始启动录制器
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        msg = "正在录音...";
                        Log.i("Testlog", "录音开始");
                    }
                    break;
                // 停止录音
                case R.id.stoprecording:
                    if (audioFile != null && audioFile.exists()) {
                        // mediaRecorder.stop();
                        mediaRecorder.reset();
                    }
                    msg = "已经停止录音.";
                    break;
                // 录音文件的播放
                case R.id.playrecording:

                    if (mediaRecorder != null) {
                        // mediaRecorder.stop();
                        mediaRecorder.reset();
                    }

                    if (audioFile != null && audioFile.exists()) {
                        Log.i("Testlog", ">>>>>>>>>" + audioFile);
                        mediaPlayer = new MediaPlayer();
                        // 为播放器设置数据文件
                        mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                        // 准备并且启动播放器
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                setTitle("录音播放完毕");

                            }
                        });
                        msg = "正在播放录音...";
                    }
                    break;
                // 上传录音文件
            }
            // 更新标题栏 并用 Toast弹出信息提示用户
            if (!msg.equals("")) {
                setTitle(msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            setTitle(e.getMessage());
        }

    }
    protected static final int ERROR = 2;
    protected static final int ERROR1 = 3;
    protected static final int ERROR2 = 4;
    protected static final int SUCCESS = 1;
    protected static final int SUCCESS1 = 5;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    Toast.makeText(record.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                    break;
                case SUCCESS1:
                    Toast.makeText(record.this,(String)msg.obj, Toast.LENGTH_LONG).show();

                    break;
                case ERROR:
                    Toast.makeText(record.this,"创建失败请重试", Toast.LENGTH_SHORT).show();
                    break;

                case ERROR1:
                    Toast.makeText(record.this,"上传失败请重试", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR2:
                    Toast.makeText(record.this,"未知错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };


    public void search(View view){
        final String acc = usr;
        final String dt = date;
        new Thread(){
            public void run(){
                try {
                    //区别1、url的路径不同
                    String path =uploadUrl+"search.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "&username="+acc+ "&date=" + URLEncoder.encode(dt,"UTF-8")+"&button=";
                    System.out.println(dt);
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
                    }else{
                        Message mas = Message.obtain();
                        mas.what = ERROR1;
                        handler.sendMessage(mas);
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Message mas = Message.obtain();
                    mas.what = ERROR1;
                    handler.sendMessage(mas);
                }
            }
        }.start();
    }
    public void upload(View view){
        final String reason = reasons;
        final String other = otherreason.getText().toString();
        final String id = babyid.getText().toString();
        final String wt = weight.getText().toString();
        if(TextUtils.isEmpty(id)||(TextUtils.isEmpty(reason)&&TextUtils.isEmpty(other))){
            Toast.makeText(this, "信息不完整！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(audioFile==null){
            Toast.makeText(this, "还未录音！！", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            public void run(){
                try {
                    //http://localhost/xampp/android/login.php
                    //区别1、url的路径不同
                    String path = uploadUrl+"upload.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data;
                    if(TextUtils.isEmpty(other)) {
                        data = "&babyid=" + id +"&weight=" + wt + "&reason=" + URLEncoder.encode(reason,"UTF-8")+"&button=";
                    }
                    else {
                        data = "&babyid=" + id + "&weight=" + wt + "&reason=" + URLEncoder.encode(other,"UTF-8")+"&button=";
                    }
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
                        String suc = "上传中";
                        System.out.println(result);
                        String regEx="[^0-9]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(result);
                        String num = m.replaceAll("").trim();
                        System.out.println(num);
                        result = result.replaceAll(num,"");
                        Message mas= Message.obtain();
                        mas.what = SUCCESS;
                        mas.obj = result;
                        handler.sendMessage(mas);
                        result= URLEncoder.encode(result,"UTF-8");
                        result =result.substring(9,result.length()-2);
                        suc   = URLEncoder.encode(suc,"UTF-8");
                        if(suc.equals(result))
                        {
                            String newfile = id+"_"+num+".mp3";
                            String filename;
                            filename = audioFile.getAbsolutePath();
                            System.out.println(filename);
                            filename = filename.substring(0,filename.lastIndexOf("/")+1);
                            System.out.println(filename);
                            filename = filename+newfile;
                            CopyFileUtil.copyFile(audioFile.getAbsolutePath(),filename,true);
                            uploadrecording(filename);
                            Message succses= Message.obtain();
                            succses.what = SUCCESS;
                            succses.obj = "上传成功！";
                            handler.sendMessage(succses);
                            existed_recording();
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
    private void uploadrecording(String filename) throws IOException {
        final String path = filename;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        URL url = new URL(uploadUrl+"testupload.php");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
        // 允许输入输出流
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        // 使用POST方法
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + boundary);

        DataOutputStream dos = new DataOutputStream(
                httpURLConnection.getOutputStream());
        dos.writeBytes(twoHyphens + boundary + end);
        dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                + path.substring(path.lastIndexOf("/") + 1) + "\"" + end);
        dos.writeBytes(end);

        FileInputStream fis = new FileInputStream(path);
        byte[] buffer = new byte[8192]; // 8k
        int count = 0;
        // 读取文件
        while ((count = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, count);
        }
        fis.close();
        dos.writeBytes(end);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
        dos.flush();
        InputStream is = httpURLConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String result = br.readLine();
        dos.close();
        is.close();
    }

    private void existed_recording() {
        final String id = babyid.getText().toString();
        new Thread(){
            public void run(){
                try {
                    //区别1、url的路径不同
                    String path =uploadUrl+"existed_recording.php";
                    URL url = new  URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //区别2、请求方式post
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0(compatible;MSIE 9.0;Windows NT 6.1;Trident/5.0)");
                    //区别3、必须指定两个请求的参数
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//请求的类型  表单数据
                    String data = "&babyid="+id+"&button=";;
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
                        mas.what = SUCCESS1;
                        mas.obj = result;
                        handler.sendMessage(mas);
                    }else{
                        Message mas = Message.obtain();
                        mas.what = ERROR2;
                        handler.sendMessage(mas);
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    Message mas = Message.obtain();
                    mas.what = ERROR2;
                    handler.sendMessage(mas);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

        }
        if (mediaRecorder != null) {
//          mediaRecorder.reset();

            mediaRecorder.release();
            mediaRecorder=null;
        }
    }
    public void  Goback(View view){
        finish();
    }

}
