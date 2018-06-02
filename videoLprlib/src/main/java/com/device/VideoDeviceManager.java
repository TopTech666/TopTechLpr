package com.device;

import android.util.Log;

import com.vz.tcpsdk;

/**
 * 相机设备操作类
 */
public class VideoDeviceManager {
	/*
	* 存在的问题：
	*     1. 判断当前设备是否存在异常： open相机看是否成功。open相机前必须close相机，否则车牌识别会多次回调，但是close相机过程中可能会有车进出场，会出现问题!!!
	*     2. 判断当前相机是否是连接： 连接状态在网络断开可以反馈错误， 但是如果相机其余问题，例如无法识别是否也能反馈在断开的状态！！！
	*     3. 抬杠目前只是自动抬杠，车子压地感是否能自动落杠
	*     4. 抬杠IO口 配置是在云平台还是写死代码
	* */
	private static final String TAG = "VideoDeviceManager";
	
	private VideoDeviceInfo mVideoDeviceInfo = null;

	private  boolean     serialOneFlag = false;
	private  boolean     serialTwoFlag = false;
	private  boolean     openFlag = false;
	private  tcpsdk.OnDataReceiver mOnDataReceiver=null ;
	private  boolean      bEnableImg = false;

	//白名单
	private  tcpsdk.onWlistReceiver  mOnWlistReceiver =null;

	private static VideoDeviceManager sVideoDeviceManager;

	private VideoDeviceManager(){
	}


	public static VideoDeviceManager getInstance(){
		if(sVideoDeviceManager == null){
			synchronized (VideoDeviceManager.class){
				if(sVideoDeviceManager == null){
					sVideoDeviceManager = new VideoDeviceManager();
				}
			}
		}
		return sVideoDeviceManager;
	}

	//添加device
	public void setDevice(VideoDeviceInfo device){
		this.mVideoDeviceInfo = device;
	}

	public VideoDeviceInfo getDevice(){
		return mVideoDeviceInfo;
	}

	/**
	 * 打开设备
	 * @return
	 */
	public boolean openDevice() {
	   if(open(mVideoDeviceInfo.deviceName, mVideoDeviceInfo.ip, mVideoDeviceInfo.port, mVideoDeviceInfo.userName, mVideoDeviceInfo.userPwd)) {
		   if(mOnDataReceiver != null)//设置车牌识别回调
			   tcpsdk.getInstance().setPlateInfoCallBack(mVideoDeviceInfo.handle,mOnDataReceiver,bEnableImg?1:0);
		   return true;
	   }
	   return false;
	}

	//重启设备
	public boolean resetDevice(){
		//必须先关闭device否则车牌会多次识别回调
		closeDevice();
		return openDevice();
	}

	/**
	 * 打开设备
	 * @return
	 */
	private boolean open(String deviceName, String ip, int port, String username, String userpassword) {
		//打开设备
		int res = tcpsdk.getInstance().open(ip.getBytes(),ip.length(), port, username.getBytes(),
 			   username.length(), userpassword.getBytes(), userpassword.length());
		if(res > 0) {
			mVideoDeviceInfo.handle = res;
			mVideoDeviceInfo.deviceName = deviceName;
			mVideoDeviceInfo.ip = ip;
			mVideoDeviceInfo.port = port;
			mVideoDeviceInfo.userName = username;
			mVideoDeviceInfo.userPwd = userpassword;
			openFlag = true;
			Log.d("xxxxxxxxx", "open: " + "打开设备成功");
		 	return true;
		} else {
			Log.d("xxxxxxxxx", "open: " + "打开设备失败");
		}
		 return false;
	}


	/**
	 * 关闭设备
	 * @return
	 */
	public void closeDevice() {
		if((serialOneFlag || serialTwoFlag) && mVideoDeviceInfo.handle >0) {
			//透明通道停止发送数据
			tcpsdk.getInstance().serialStop(mVideoDeviceInfo.handle);
			serialOneFlag = false;
			serialTwoFlag = false;
		}
		if(mVideoDeviceInfo.handle > 0) {
			//关闭设备
			tcpsdk.getInstance().close(mVideoDeviceInfo.handle);
			mVideoDeviceInfo.handle = 0;
		}
		openFlag = false;
	}

	//获取设备信息
	public VideoDeviceInfo getVideoDeviceInfo() {
		return mVideoDeviceInfo;
	}

	/**
	 * 设置车牌识别回调
	 * @param receiver
	 * @param enableImg 回调中是否包含截图
	 */
	public void setPlateInfoCallBack(tcpsdk.OnDataReceiver receiver , boolean enableImg) {
		mOnDataReceiver = receiver;
		bEnableImg  = enableImg;
	}


	/**
	 * 透明通道发送数据
	 * @param serialNum 通道 0 1
	 * @param pData
	 * @param uSizeData
	 * @return
	 */
	public boolean serialSend(int serialNum,byte[] pData, long uSizeData) {
		if(!openFlag)
			return false;
		if(serialNum < 0 || serialNum > 1) {
			return false;
		}
		// 串口1
		if( serialNum == 0 ) {
			if(!serialOneFlag) {
				if(tcpsdk.getInstance().serialStart(mVideoDeviceInfo.handle, serialNum) != 0) //开启透明通道
					return false;
				serialOneFlag = true;
			}
		} else {
			if(!serialTwoFlag) {
				if(tcpsdk.getInstance().serialStart(mVideoDeviceInfo.handle, serialNum) != 0)
					return false;
				serialTwoFlag = true;
			}
		}
		if(tcpsdk.getInstance().serialSend(mVideoDeviceInfo.handle, serialNum, pData, uSizeData) != 0)
			return false;
		else
			return true;
	}

	/**
	 * 获取截图
	 * @param imgBuffer
	 * @param imgBufferMaxLength
	 * @return
	 */
	public int getSnapImageData(byte[] imgBuffer, int imgBufferMaxLength) {
		if(!openFlag)
			return -1;
		return tcpsdk.getInstance().getSnapImageData(mVideoDeviceInfo.handle, imgBuffer, imgBufferMaxLength);
	}
	 
	//播放语音
	public int playVoice( byte[] voice, int interval, int volume, int male) {
		if(!openFlag)
			return -1;
		return tcpsdk.getInstance().playVoice(mVideoDeviceInfo.handle, voice, interval, volume, male);
	}

	//获取设备打开状态
	public boolean getopenFlag() {
		return openFlag;
	}

	//触发车牌识别
	public int forceTrigger() {
		if(!openFlag)
			return  -1;
		return tcpsdk.getInstance().forceTrigger(mVideoDeviceInfo.handle);
	}

	//是否连接中
	public synchronized boolean isConnected(){
		return tcpsdk.getInstance().isConnected(mVideoDeviceInfo.handle);
	}

	/**
	 * 开闸
	 * 默认延时500ms
	 * @return
	 */
	public int  setIoOutputAuto() {
		if(!openFlag)
			return  -1;
		return tcpsdk.getInstance().setIoOutputAuto(mVideoDeviceInfo.handle, mVideoDeviceInfo.chnId, 500);
	}

	/**
	 * 开闸
	 * @param uChnId  通道号
	 * @param nDuration
	 * @return
	 */
	public int  setIoOutputAuto( short uChnId, int nDuration) {
		if(!openFlag)
			return  -1;
		return tcpsdk.getInstance().setIoOutputAuto(mVideoDeviceInfo.handle,uChnId,nDuration);
	}


//
//	 public  void setWlistInfoCallBack(tcpsdk.onWlistReceiver recevier) {
//		 if(!openFlag)
//		  return ;
//		  tcpsdk.getInstance().setWlistInfoCallBack(mVideoDeviceInfo.handle, recevier);
//	 }
//
//	 public  int  importWlistVehicle(WlistVehicle wllist) {
//		   if(!openFlag)
//				return -1;
//			return tcpsdk.getInstance().importWlistVehicle(mVideoDeviceInfo.handle, wllist);
//	   }
//
//	   public  int  deleteWlistVehicle(byte[] plateCode) {
//		   if(!openFlag)
//				return -1;
//			return tcpsdk.getInstance().deleteWlistVehicle(mVideoDeviceInfo.handle, plateCode);
//	   }
//
//	   public  int  queryWlistVehicle(byte[] plateCode) {
//		   if(!openFlag)
//				return -1;
//			return tcpsdk.getInstance().queryWlistVehicle(mVideoDeviceInfo.handle, plateCode);
//	   }
//
//



}
