package com.example.cwgj.toptechvideolpr;

import android.app.Activity;
import android.content.Context;
import android.hardware.SerialManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwgj.basiclib.UiUtils;
import com.cwgj.imgupload.bean.PicBean;
import com.cwgj.imgupload.oss.OSSConfigParam;
import com.cwgj.imgupload.oss.OSSUploadHelper;
import com.cwgj.imgupload.utils.BitmapUtils;
import com.cwgj.imgupload.utils.UploadPicManager;
import com.cwgj.ledlib.baseled.LedDriverManager;
import com.device.DeviceInfo;
import com.device.DeviceManager;
import com.vz.PlateResult;
import com.vz.tcpsdk;

import java.io.UnsupportedEncodingException;
import java.util.List;

import xunfei.tech.com.techlib.XunfeiManager;

public class MainActivity extends Activity implements tcpsdk.OnDataReceiver {

    private static final String TAG = "xxxxxxxxx";

    TextView tv_car_num;

    ImageView iv_plate;

    EditText et_com;

    //
    SerialManager mSerialManager;

    //讯飞
    XunfeiManager mXunfeiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_car_num = (TextView) findViewById(R.id.tv_car_num);
        iv_plate = (ImageView) findViewById(R.id.iv_plate);
        et_com = (EditText) findViewById(R.id.et_com);
        //tcp初始化
        tcpsdk.getInstance().setup();
        DeviceManager.getInstance().setDevice(new DeviceInfo());
        DeviceManager.getInstance().setPlateInfoCallBack(this ,true);
        //图片oss初始化
        initOSS();
        //获取串口操作管理类 ，需要系统签名才能使用，否则无权限奔溃
//        mSerialManager = (SerialManager)getSystemService("serial");
        //讯飞初始化
        setMaxVolume();
        mXunfeiManager = XunfeiManager.getInstance().initTts(this, null);

        //打开相机
        findViewById(R.id.btn_open_plate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceManager.getInstance().openDevice()){
                    Toast.makeText(MainActivity.this, "打开设备成功", Toast.LENGTH_LONG).show();
                    AlarManagerHelper.initCameraConnAlarmManager(MainActivity.this);
                }
            }
        });

        //关闭相机
        findViewById(R.id.btn_close_plate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceManager.getInstance().getopenFlag()){
                    DeviceManager.getInstance().closeDevice();
                    Toast.makeText(MainActivity.this, "关闭设备成功", Toast.LENGTH_LONG).show();
                    AlarManagerHelper.cancleCameraConnAlarmManager(MainActivity.this);
                }
            }
        });
        //隐藏虚拟按键
        findViewById(R.id.btn_hide_ui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.setHideBottomNavi(getWindow());
            }
        });

        //语音合成
        findViewById(R.id.btn_tts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mXunfeiManager.ttsPlay(et_com.getText().toString());
            }
        });


        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_com.getText().toString();
                byte[] bytes = LedDriverManager.getInstance().packTextCommad(4, str);
                DeviceManager.getInstance().serialSend(0,bytes, bytes.length);
            }
        });

        findViewById(R.id.btn_restart_sys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPicManager.getInstance().uploadPicsAsynSilently();
            }
        });

        findViewById(R.id.btn_sn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.tv_sn)).setText("SN:  " + android.os.Build.SERIAL);
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceManager.getInstance().closeDevice();
        //释放资源
        tcpsdk.getInstance().cleanup();
        //
        AlarManagerHelper.cancleCameraConnAlarmManager(MainActivity.this);
        //
        AlarManagerHelper.cancleOSSAlarmManager(MainActivity.this);
    }

    //设置最大音量
    private void setMaxVolume(){
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //获取最大媒体音量值
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //设置媒体音量为最大值，当然也可以设置媒体音量为其他给定的值
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max,0);
    }

    private void initOSS(){
        String keyid = "STS.NJ7eUHx7X6TnwaHaxr7ZCxMNM";
        String secret ="DJW2h9kG4ceVN7jpAiJyjdcDaW8gKhU6ghDe4A3oDhYk";
        String stsToken ="CAIS8wF1q6Ft5B2yfSjIr4mCLu/8leh5gZaFdUf5hXgnO9Vvl4jlrzz2IHFIfnBtB+kWsPg1mWpW5vgflqFoS5JDV0zJa5OaU0O4IkfzDbDasumZsJYi6vT8a0nxZjf/2MjNGZKbKPrWZvaqbX3diyZ32sGUXD6+XlujQ/Lr5Jl8dYZVJH7aCwBLH9BLPABvhdYHPH/KT5aXPwXtn3DbATgD2GM+qxsmt/zumpLCtkOD0ganlbJJnemrfMj4NfsLFYxkTtK40NZxcqf8yyNK43BIjvwn0PYdpGme7o/BWQQLvUXYbvC0+cJgLkp+fbMq2QSCJTR6EJcagAE4JCSQE0jdDFQH0HNP7/Ie08COOsBwU3DNQRit3S3udvXKAL4yg+n1rp2lHNEMhjhL911GBtHWQ7mmRD1tRqjVVIwRVcQgLf+6AWanladn9Z7XNOBFeU+R5Uu1PHib1CghLuUpoYBxe7Cha9oLZiI3hIZZEUAZV8/y6mqR0Skr/w==";
        String region = "oss-cn-shanghai";
        String bucket = "cwgj";

        OSSUploadHelper.getInstance().initConfig(this, new OSSConfigParam(keyid, secret, stsToken, region, bucket));

        //开启上传oss
        AlarManagerHelper.initOSSAlarmManager(MainActivity.this);


    }

    private void printSqliteData(){
       List<PicBean> list =  UploadPicManager.getInstance().queryAllPic();
        for (PicBean bean : list) {
            Log.d(TAG, "数据库bean: " + bean.getPicPath());
        }
    }

    @Override
    public void onDataReceive(int handle, PlateResult plateResult, final int uNumPlates, int eResultType, final byte[] pImgFull, int nFullSize, final byte[] pImgPlateClip, int nClipSize) {
        try {
            final String carNum = new String(plateResult.license,"GBK");
            Log.d(TAG, "onDataReceive: " +carNum);
            if(pImgFull != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String bigPath = BitmapUtils.ByteArrayToFile(pImgFull,  System.currentTimeMillis()+"");
                        String smallPath = BitmapUtils.ByteArrayToFile(pImgPlateClip,  System.currentTimeMillis()+"");
                        PicBean bigBean = new PicBean(bigPath);
                        PicBean smallBean = new PicBean(smallPath);
                        UploadPicManager.getInstance().uploadSinglePic(bigBean);
                        UploadPicManager.getInstance().uploadSinglePic(smallBean);
                        printSqliteData();

                    }
                }).start();
            }
            if(plateResult!=null)
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_car_num.setText(carNum);
                        try {
                            byte[] result = LedDriverManager.getInstance().packTextCommad(2, carNum);
                            DeviceManager.getInstance().serialSend(0, result, result.length);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }






}
