package com.cwgj.imgupload.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cwgj.imgupload.bean.PicBean;
import com.cwgj.imgupload.oss.OSSUploadHelper;
import com.cwgj.imgupload.utils.UploadPicManager;

import java.util.List;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/30 14:21
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class UploadPicService extends Service{

    private static final String TAG = "UploadPicService";

    //数据库未上传的图片
    List<PicBean> list;

    private Handler mHandler;

    //上传list图片index
    private int index;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("xxxxxxxxx", "onCreate: ");
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    if(index>=list.size()){
                        Log.d("xxxxxxxxx", "onSuccess: 队列图片全部上传" );
                        return;
                    }
                    final String str = String.format("saas/10010/20180530/inpark/%s.jpg", System.currentTimeMillis());
                    final PicBean picBean = list.get(index);
                    OSSUploadHelper.getInstance().upLoadAn(str,picBean.getPicPath(), new OSSUploadHelper.UploadCallBack() {
                        @Override
                        public void onSuccess() {
                            UploadPicManager.getInstance().deletePic(picBean);
                            index++;
                            mHandler.sendEmptyMessageDelayed(1, 1000);
                            Log.d("xxxxxxxxx", "onSuccess: 队列图片上传成功" + str + picBean.getPicPath());
                        }

                        @Override
                        public void onFail(String msg) {
                            index++;
                            mHandler.sendEmptyMessageDelayed(1, 1000);
                            Log.d("xxxxxxxxx", "onFail: 队列图片上传失败" + msg + picBean.getPicPath());
                        }
                    });
                }
            }
        };
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("xxxxxxxxx", "onStartCommand: ");
        if(intent!=null){
            list = UploadPicManager.getInstance().queryAllPic();
            if(list!=null && list.size()>0){
                index = 0;
                mHandler.sendEmptyMessage(1);
            }else {
                Log.d(TAG, " 队列数据库无图片");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("xxxxxxxxx", "onDestroy: ");
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
