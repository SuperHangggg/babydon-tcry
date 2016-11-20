package com.example.a279095640.babycry;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.File;

public class record extends AppCompatActivity {
    private String reasons;
    private EditText otherreason;
    private EditText babyid;
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

        Button btnStart = (Button) findViewById(R.id.startrecording);
        Button btnStop = (Button) findViewById(R.id.stoprecording);
        Button btnPlay = (Button) findViewById(R.id.playrecording);
        Button btnUpLoad = (Button) findViewById(R.id.uploadrecording);
        Button btnDownLoad = (Button) findViewById(R.id.downloadrecording);

        otherreason = (EditText)findViewById(R.id.inputreason);
        babyid = (EditText)findViewById(R.id.uploadbabyid);
        RadioGroup radiogroup = (RadioGroup) this.findViewById(R.id.radioReasons);


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
                        audioFile = File.createTempFile("record_", ".amr", EnvironmentShare.getAudioRecordDir());
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


}
