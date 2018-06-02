package com.cwgj.imgupload.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/16 15:52
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class BitmapUtils {


    private static final String TAG = "BitmapUtils";

    //图片保存文件目录
    public static final String ParentPath = Environment.getExternalStorageDirectory()+ "/AAAA/";


    //图片压缩到小于100k
    public static String ByteArrayToFile(byte[] imageBytes, String imgName){
       return  ByteArrayToFile(imageBytes, 1000, imgName);
    }


    public static String ByteArrayToFile(byte[] imageBytes, long maxSize, String imgName){
        ByteArrayOutputStream outputStream = compressImage(imageBytes, maxSize);
        File file = saveBitmapToFile(outputStream, ParentPath + imgName +".jpg");
        return file.getAbsolutePath();
    }

    /**
     * 质量压缩方法
     *
     * @param imgArrayByte
     * @return
     */
    public static ByteArrayOutputStream compressImage(byte[] imgArrayByte, long maxSize) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgArrayByte, 0 , imgArrayByte.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        /* 循环判断如果压缩后图片是否大于mMaxSize ,大于继续压缩*/
        while (baos.toByteArray().length / 1024 > maxSize) {
            baos.reset();
            options -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        return baos;
    }



    /***************************************************
     * 方法描述  图片保存
     * 方法名  :  saveBitmapToFile
     * params ：bitmap需要保存的bitmap  targetPath保存路径
     **************************************************/
    public static File saveBitmapToFile(ByteArrayOutputStream baos, String targetPath) {

        File result = new File(targetPath.substring(0, targetPath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) {
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(targetPath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            baos.close();
            Log.d(TAG, "----"+ targetPath +" save succ");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return new File(targetPath);
    }



}
