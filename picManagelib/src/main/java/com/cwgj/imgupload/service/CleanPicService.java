package com.cwgj.imgupload.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cwgj.imgupload.utils.BitmapUtils;
import com.cwgj.imgupload.utils.FileUtils;

import java.io.File;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：清理sd卡中的图片资源
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/6/1 11:08
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class CleanPicService extends IntentService {

    private static final String TAG = "CleanPicServer";

    public CleanPicService() {
        super("CleanPicServer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;
        File[] files = FileUtils.orderByDate(BitmapUtils.ParentPath);
        if(files!=null && files.length >0){
            for (File file : files) {
                   FileUtils.deleteFile(file);
                Log.d("xxxxxxxx删除图片 ", "sd卡m: " + FileUtils.getSDAvailableSize() + "删除的文件： "+ file.getPath());
                   //sd卡可用内存大于50%停止删除图片
                if(FileUtils.getAvailablePercent()>0.9)
                    break;
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
