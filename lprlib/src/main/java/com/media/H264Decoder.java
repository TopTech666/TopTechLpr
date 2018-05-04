package com.media;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/3 15:11
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class H264Decoder {
    private static H264Decoder uniqueInstance = null;

    private H264Decoder() {
        // Exists only to defeat instantiation.
    }

    public static H264Decoder getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new H264Decoder();
            int result = uniqueInstance.init();
        }
        return uniqueInstance;
    }


    public native int init();


    public native int add(int decodeType);


    public native synchronized  int decode(int handle,byte[] src, int length, byte[] dst,int[] wah,int codetype);


    public native void release(int handle);

    static {
        System.loadLibrary("H264Decoder");
    }
}
