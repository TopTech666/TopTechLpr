package com.cwgj.ledlib.baseled;

/**
 * +----------------------------------------------------------------------
 * |  说明     ： led驱动屏管理类
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/29 10:53
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class LedDriverManager {

    private static LedDriverManager sLedDriverManager;

    //led显示屏驱动
    LedDrive mLedDrive;

    private LedDriverManager(){
        //gradle 配置不同品牌的led
        mLedDrive = FactoryLed.getLed(FactoryLed.OuGuan);
    }


    public static LedDriverManager getInstance(){
        if(sLedDriverManager == null){
            synchronized (LedDriverManager.class){
                if(sLedDriverManager == null){
                    sLedDriverManager = new LedDriverManager();
                }
            }
        }
        return sLedDriverManager;
    }

    /**
     * 驱动显示屏显示文字(即时显示)
     * @param line  第几行
     * @param body  显示内容
     * @return
     */
    public byte[] packTextCommad(int line, String body){
        //默认即时消息显示5s
        return mLedDrive.packTextCommad(line, body, 5);

    }

    /**
     * 永久显示文字
     * @param line  第几行
     * @param body  显示内容
     * @return
     */
    public byte[] packForeverTextCommad(int line, String body){

        return mLedDrive.packForeverTextCommad(line, body);
    }





}
