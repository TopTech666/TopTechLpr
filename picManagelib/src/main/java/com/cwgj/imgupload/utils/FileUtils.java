package com.cwgj.imgupload.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/30 16:29
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class FileUtils {

    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());}


    public static File getFileByPath(String filePath) {
        return TextUtils.isEmpty(filePath)?null:new File(filePath);
    }


    /**
     * 获得SD卡总大小 M
     *
     * @return
     */
    public static long getSDTotalSize() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks/1024/1024;
    }

    /**
     * 获得sd卡剩余容量，即可用大小 M
     *
     * @return
     */
    public static long getSDAvailableSize() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks/1024/1024;
    }

    public static float getAvailablePercent(){
        return (float)getSDAvailableSize()/getSDTotalSize();
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    public static File[] orderByDate(String filePath) {
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdirs();
        }
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName());
            System.out.println(new Date(files[i].lastModified()));
        }
        return files;

    }
}
