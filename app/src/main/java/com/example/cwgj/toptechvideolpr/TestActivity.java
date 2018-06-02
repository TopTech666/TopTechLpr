package com.example.cwgj.toptechvideolpr;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.hardware.SerialManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwgj.imgupload.utils.BitmapUtils;
import com.cwgj.imgupload.utils.FileUtils;
import com.cwgj.ledlib.baseled.LedDriverManager;
import com.device.VideoDeviceInfo;
import com.device.VideoDeviceManager;
import com.vz.PlateResult;
import com.vz.tcpsdk;

import java.io.UnsupportedEncodingException;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/6/1 17:02
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class TestActivity extends Activity implements tcpsdk.OnDataReceiver {

    TextView tv_video_state, tv_plate_result;

    ImageView iv_big_pic, iv_small_pic;

    EditText et_input_ip, et_input_led_line, et_input_led_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tv_video_state = (TextView) findViewById(R.id.tv_video_state);
        tv_plate_result = (TextView) findViewById(R.id.tv_plate_result);

        iv_big_pic = (ImageView) findViewById(R.id.iv_big_pic);
        iv_small_pic = (ImageView) findViewById(R.id.iv_small_pic);

        et_input_ip = (EditText) findViewById(R.id.et_input_ip);
        et_input_led_line = (EditText) findViewById(R.id.et_input_led_line);
        et_input_led_txt = (EditText) findViewById(R.id.et_input_led_txt);

        setListener();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VideoDeviceManager.getInstance().closeDevice();
        //释放资源
        tcpsdk.getInstance().cleanup();
    }

    private void init() {
        tcpsdk.getInstance().setup();
        VideoDeviceManager.getInstance().setDevice(new VideoDeviceInfo());
        VideoDeviceManager.getInstance().setPlateInfoCallBack(this ,true);

        et_input_ip.setText(VideoDeviceManager.getInstance().getDevice().ip);
        et_input_ip.setSelection(et_input_ip.getText().toString().length());

        et_input_led_line.setText("1");


        if(VideoDeviceManager.getInstance().openDevice()){
           tv_video_state.setText("相机识别中");
           tv_video_state.setTextColor(getResources().getColor(R.color.black));
        }else {
            tv_video_state.setText("相机未连接");
            tv_video_state.setTextColor(getResources().getColor(R.color.red));
        }

    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setListener(){
        findViewById(R.id.btn_ensure_ip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改相机ip
                if(TextUtils.isEmpty(et_input_ip.getText().toString())){
                    showToast("ip null");
                    return;
                }
                VideoDeviceManager.getInstance().getDevice().ip = et_input_ip.getText().toString();
                if(VideoDeviceManager.getInstance().resetDevice()){
                    tv_video_state.setText("相机识别中");
                    tv_video_state.setTextColor(getResources().getColor(R.color.black));
                }else {
                    tv_video_state.setText("相机未连接");
                    tv_video_state.setTextColor(getResources().getColor(R.color.red));
                }

            }
        });

        findViewById(R.id.btn_force).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //主动识别车牌
                VideoDeviceManager.getInstance().forceTrigger();
            }
        });

        findViewById(R.id.btn_send_led_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //led 即时消息
                if(TextUtils.isEmpty(et_input_led_txt.getText().toString())){
                    showToast("text null");
                    return;
                }
                byte[] result = LedDriverManager.getInstance().packTextCommad(TextUtils.isEmpty(et_input_led_line.getText().toString())?1:Integer.parseInt(et_input_led_line.getText().toString()),
                        et_input_led_txt.getText().toString());

                VideoDeviceManager.getInstance().serialSend(0, result, result.length);
            }
        });

        findViewById(R.id.btn_send_led_forver_led).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //led 永久消息
                if(TextUtils.isEmpty(et_input_led_txt.getText().toString())){
                    showToast("text null");
                    return;
                }
                byte[] result = LedDriverManager.getInstance().packForeverTextCommad(TextUtils.isEmpty(et_input_led_line.getText().toString())?1:Integer.parseInt(et_input_led_line.getText().toString()),
                        et_input_led_txt.getText().toString());

                VideoDeviceManager.getInstance().serialSend(0, result, result.length);
            }
        });

        findViewById(R.id.btn_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDeviceManager.getInstance().setIoOutputAuto();
            }
        });

        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlarManagerHelper.initCleanPicAlarmManager(TestActivity.this);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true){
//                            VideoDeviceManager.getInstance().forceTrigger();
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        });

        findViewById(R.id.btn_voice_conn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //语音通话

            }
        });


    }


    @Override
    public void onDataReceive(int handle, PlateResult plateResult, int uNumPlates, int eResultType, final byte[] pImgFull, int nFullSize, final byte[] pImgPlateClip, int nClipSize) {
        try {

            final String carNum = new String(plateResult.license,"GBK");

           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                    String xNum = carNum.contains(PlateResult.NO_CARNUM)?"无车牌":carNum;
                    tv_plate_result.setText("识别结果："+xNum);
                    et_input_led_txt.setText(xNum);
                    et_input_led_txt.setSelection(xNum.length());
               }
           });

            if(pImgFull != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String bigPath = BitmapUtils.ByteArrayToFile(pImgFull,  System.currentTimeMillis()+"");
                        final String smallPath = BitmapUtils.ByteArrayToFile(pImgPlateClip,  System.currentTimeMillis()+"");

                        Log.d("xxxxxx压缩图片 ", "可用M: " +FileUtils.getSDAvailableSize()+"  sd可用%："+ FileUtils.getAvailablePercent()+ " big: " + bigPath + "  small: "+ smallPath);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                iv_big_pic.setImageBitmap(BitmapFactory.decodeFile(bigPath));
                                iv_small_pic.setImageBitmap(BitmapFactory.decodeFile(smallPath));

                                FileUtils.deleteFile(bigPath);
                                FileUtils.deleteFile(smallPath);

                            }
                        });


                    }
                }).start();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
