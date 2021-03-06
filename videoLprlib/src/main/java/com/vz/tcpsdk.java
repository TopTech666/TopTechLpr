package com.vz;


import android.util.Log;

import com.vz.PlateResult;
import com.vz.WlistVehicle;

public class tcpsdk {
	
	private static tcpsdk sM_tcpsdk = null;
	
	private void tcpsdk()
	{
		
	}
	
	public static tcpsdk getInstance()
	{
		if(sM_tcpsdk == null)
			sM_tcpsdk = new tcpsdk();
		return sM_tcpsdk;
	}
	
	/**
	*  @brief 全局初始化
	*  @return 0表示成功，-1表示失败
	*  @note 在所有接口调用之前调用
	*  @ingroup group_global
	*/
    public native int   setup();
    /**
    *  @brief 全局释放
    *  @note 在程序结束时调用，释放SDK的资源
    *  @ingroup group_global
    */
    public native void  cleanup();
     
    /**
    *  @brief 打开一个设备
    *  @param  [IN] ip 设备的IP地址
    *  @param  [IN] ipLength 设备的IP地址长度
    *  @param  [IN] port 设备的端口号
    *  @param  [IN] username 访问设备所需用户名
    *  @param  [IN] userpassword 访问设备所需密码
    *  @return 返回设备的操作句柄，当打开失败时，返回0
    *  @ingroup group_device
    */
    public native int  open(byte[] ip,int ipLength,int port,byte[] username,int userLength,byte[] userpassword ,int passwordLenth);
    
    /**
    *  @brief 关闭一个设备
    *  @param  [IN] handle 由open函数获得的句柄
    *  @return 0表示成功，-1表示失败
    *  @ingroup group_device
    */
    public native int   close(int tcphandle);

	 	/**
	*  @brief 设置IO输出
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] uChnId IO输出的通道号，从0开始
	*  @param  [IN] nOutput  将要设置的IO输出的状态，0表示继电器开路，1表示继电器闭路
	*  @return 0表示成功，-1表示失败
	*  @ingroup group_device
	*/
   public native  int  setIoOutput(int handle, short uChnId, int nOutput);

    
   public native  int   getIoOutput(int  handle,  int  uChnId , int[] nOutput);

   	/**
	*  @brief 设置IO输出，并自动复位
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] uChnId IO输出的通道号，从0开始
	*  @param  [IN] nDuration 延时时间，取值范围[500, 5000]毫秒
	*  @return 0表示成功，-1表示失败
	*  @ingroup group_device
	*/
   public native  int  setIoOutputAuto(int handle, short uChnId, int nDuration);
   
   /**
	*  @brief 获取连接状态
	*  @param  [IN] handle 由open函数获得的句柄
	*  @return true表示连接中，false表示未连接
	*  @note   用户可以周期调用该函数来主动查询设备是否断线，以及断线后是否恢复连接
	*  @ingroup group_device
	*/
   public native  boolean  isConnected(int handle);
   
   /**
	*  @brief 设置脱机结果的回调函数
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] onDataReceiver 识别结果回调函数，如果为NULL，则表示关闭该回调函数的功能
	*  @param  [IN] bEnableImage 指定识别结果的回调是否需要包含截图信息：1为需要，0为不需要
	*  @return 0表示成功，-1表示失败
	*  @ingroup group_device
	*/
   public native int setPlateInfoCallBack( int handle,  OnDataReceiver  onDataReceiver ,int bEnableImage);


 //  public native int setPlateInfoCallBack( OnDataReceiver  onDataReceiver );
   
   /**
	*  @brief 发送软件触发信号，强制处理当前时刻的数据并输出结果
	*  @param  [IN] handle 由open函数获得的句柄
	*  @return 0表示成功，-1表示失败
	*  @note   车牌识别结果通过回调函数的方式返回，如果当前视频画面中无车牌，则回调函数不会被调用
	*  @ingroup group_device
	*/
   public native int forceTrigger(int handle);
   
   /**
	*  @brief 开启透明通道
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] nSerialPort 指定使用设备的串口序号：0表示第一个串口，1表示第二个串口
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int serialStart(int handle, int nSerialPort);

    /**
	*  @brief 透明通道发送数据
	*  @param [IN] nSerialHandle 由serialStart函数获得的句柄
	*  @param [IN] pData 将要传输的数据块的首地址
	*  @param [IN] uSizeData 将要传输的数据块的字节数
	*  @return 0表示成功，其他值表示失败
	*  @ingroup group_device
	*/
   public native int  serialSend(int handle, int nSerialPort, byte[] pData, long uSizeData);

    /**
	*  @brief 透明通道停止发送数据
	*  @param [IN] nSerialHandle 由serialStart函数获得的句柄
	*  @return 0表示成功，其他值表示失败
	*  @ingroup group_device
	*/
   public native int serialStop(int handle);

      /**
	*  @brief 开启透明通道
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] imgBuffer 图片缓冲区
	*  @param  [IN] imgBufferMaxLength 图片缓冲区长度
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int  getSnapImageData(int handle, byte[] imgBuffer, int imgBufferMaxLength);

       /**
	*  @brief 获取RTSP地址
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] url 缓冲区
	*  @param  [IN] urlMaxLength 缓冲区长度
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int  getRtspUrl(int handle,  byte[] url, int urlMaxLength);

       /**
	*  @brief 播放语音
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] voice 语音数据
	*  @param  [IN] interval 语音文件的播放间隔(0-5000）
	*  @param  [IN] volume 声音大小(0-100)
	*  @param  [IN] male  声音类型(男声0，女生1)
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int playVoice( int handle, byte[] voice, int interval, int volume, int male);
   
       /**
	*  @brief 设置白名单回调
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] recevier 回调
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int setWlistInfoCallBack(int handle,onWlistReceiver recevier);

     /**
	*  @brief 导入白名单
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] wllist 白名单
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int  importWlistVehicle(int handle,WlistVehicle wllist);

     /**
	*  @brief 删除白名单
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] plateCode 车牌号
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int  deleteWlistVehicle(int handle,byte[] plateCode);

     /**
	*  @brief 查询白名单
	*  @param  [IN] handle 由open函数获得的句柄
	*  @param  [IN] plateCode 车牌号
	*  @return 返回透明通道句柄，0表示失败
	*  @ingroup group_device
	*/
   public native int  queryWlistVehicle(int handle,byte[] plateCode);
   
	public interface OnDataReceiver {
		
		void onDataReceive(int handle, PlateResult plateResult, int uNumPlates, int eResultType,
						   byte[] pImgFull, int nFullSize, byte[] pImgPlateClip, int nClipSize);

	}

	public interface onWlistReceiver {
		void onWlistReceive(int handle, WlistVehicle wlist);
	}

    static {
    	try {
    		System.loadLibrary("vztcpsdk_dynamic");
            System.loadLibrary("tcpsdk");
    	}
    	catch(UnsatisfiedLinkError e) {
			// fatal error, we can't load some our libs
			Log.d("tcpsdk", "Couldn't load lib: " + " - " + e.getMessage());
			
		}
    }
}
