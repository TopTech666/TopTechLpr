package com.example.cwgj.manager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.device.VideoDeviceManager;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：主板外设设备的状态监测
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/6/2 11:16
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class DeviceExceptionManager {

    /*
    *  存在的问题：
    *  1.
    *  2.  LED显示屏无法获取异常状态（欧冠led， TCP 模式能反馈通讯是否正常 ，错误协议数据返回）
    *  3.  主板在没有接入麦克风的时候，依然可以正常录音并生成了录音文件（有没有别的方式？？？）
    *
    * */

    private static final String TAG = "DeviceExceptionManager";

    private static DeviceExceptionManager sDeviceExceptionManager;

    private DeviceExceptionManager() {
    }

    public static DeviceExceptionManager getInstance() {
        if (sDeviceExceptionManager == null) {
            synchronized (DeviceExceptionManager.class) {
                if (sDeviceExceptionManager == null) {
                    sDeviceExceptionManager = new DeviceExceptionManager();
                }
            }
        }
        return sDeviceExceptionManager;
    }


    //获取相机是否正常的状态
    public boolean getVideoRunningState() {
        if (VideoDeviceManager.getInstance().getopenFlag() && VideoDeviceManager.getInstance().isConnected()) {
            return true;
        }
        return false;
    }


    public boolean getRecordState() {
        int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {
            audioRecord.startRecording();//检测是否可以进入初始化状态
            Log.d(TAG, "正常进入录音初始状态 ");
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
                Log.d(TAG, "无法进入录音初始状态 ");
            }
            return false;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            //6.0以下机型都会返回此状态，故使用时需要判断bulid版本
            //检测是否在录音中
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
//                LogUtils.d("CheckAudioPermission", "录音机被占用");
            }
            return false;
        } else {
            //检测是否可以获取录音结果

            readSize = audioRecord.read(point, 0, point.length);
            if (readSize <= 0) {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                }
//                LogUtils.d("CheckAudioPermission", "录音的结果为空");
                return false;

            } else {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                }

                return true;
            }
        }


    }
}
