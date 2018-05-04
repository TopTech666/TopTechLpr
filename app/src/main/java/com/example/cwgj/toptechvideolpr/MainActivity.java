package com.example.cwgj.toptechvideolpr;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.ToastUtils;
import com.device.DeviceInfo;
import com.device.DeviceSet;
import com.device.VideoSetView;
import com.vz.PlateResult;
import com.vz.tcpsdk;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements tcpsdk.OnDataReceiver
,VideoSetView.OnVideoLprListener{

    public static final int StopVedio = 0x20001;
    public static final int StartVedio = 0x20002;

    public static final int SelectVedio = 0x20009;
    public static final int ConfigDeivce = 0x20010;
    public static final int DClickVedio = 0x200011;
    public static final int PlateImage = 0x200012;


    RelativeLayout rl_main;

    DeviceSet ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl_main = findViewById(R.id.rl_main);
        tcpsdk.getInstance().setup();
        addVideoSetView();

    }

    private void addVideoSetView(){
        DeviceInfo deviceInfo = new DeviceInfo(11);
        VideoSetView vsv = new VideoSetView(MainActivity.this);
        vsv.setId(deviceInfo.id);
        vsv.setOnVideoLprListener(this);
        ds = new DeviceSet(deviceInfo ,vsv);
        ds.setPlateInfoCallBack(this, 1);
        rl_main.addView(vsv, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ds!=null)
            ds.close();
        tcpsdk.getInstance().cleanup();
    }

    @Override
    public void onDataReceive(int handle, PlateResult plateResult, int uNumPlates, int eResultType, byte[] pImgFull, int nFullSize, byte[] pImgPlateClip, int nClipSize) {
        try {
            if(plateResult!=null)
            ToastUtils.showToast( new String(plateResult.license,"GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SelectVedio: {
                }
                break;
                case DClickVedio: {
                }
                break;

                case ConfigDeivce: {
                }
                break;

                case StopVedio: {
                }
                break;
                case StartVedio: {
                }
                break;
                case PlateImage: {
//                    Bitmap bmp = (Bitmap)msg.obj;
//
//                    DeviceSet ds = MainActivity.this.getDeviceSetFromId(msg.arg1);
//
//                    if(bmp != null) {
//                        ds.setPlateImage(bmp);
//                    }
//                    Bundle bundle =  msg.getData();
//                    ds.setTrriglePlateText(bundle.getString("plate"));
//
                }
                break;
                default:
                    ToastUtils.showToast("未知消息");
                    break;
            }

        };
    };

    @Override
    public void startVideo() {
        if(ds != null) {
            ds.playVideo();
        }
    }

    @Override
    public void stopVideo() {
        if(ds != null) {
            ds.stopVideo();
        }
    }

    @Override
    public void operateDoor() {
        if(ds != null) {
            ds.setIoOutputAuto((short) 0, 500);
        }
    }

    @Override
    public void shotUp() {
        if(ds != null) {
        }
    }
}
