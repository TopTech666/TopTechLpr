package com.device;

import android.util.Log;

import com.vz.WlistVehicle;
import com.vz.tcpsdk;

/**
 * 相机设备操作类
 */
public class DeviceManager {

	private static final String TAG = "DeviceSet";
	
	private DeviceInfo    mDeviceInfo = null;

	private  boolean     serialOneFlag = false;
	private  boolean     serialTwoFlag = false;
	private  boolean     openFlag = false;
	private  tcpsdk.OnDataReceiver mOnDataReceiver=null ;
	private  boolean      bEnableImg = false;
	//白名单
	private  tcpsdk.onWlistReceiver  mOnWlistReceiver =null;

	private static DeviceManager sDeviceManager;

	private DeviceManager(){
	}


	public static DeviceManager getInstance(){
		if(sDeviceManager == null){
			synchronized (DeviceManager.class){
				if(sDeviceManager == null){
					sDeviceManager = new DeviceManager();
				}
			}
		}
		return sDeviceManager;
	}

	//添加device
	public void setDevice(DeviceInfo device){
		this.mDeviceInfo = device;
	}

	/**
	 * 打开设备
	 * @return
	 */
	public boolean openDevice() {
	   if(open(mDeviceInfo.deviceName, mDeviceInfo.ip, mDeviceInfo.port,mDeviceInfo.userName,mDeviceInfo.userPwd)) {
		   if(mOnDataReceiver != null)//设置车牌识别回调
			   tcpsdk.getInstance().setPlateInfoCallBack(mDeviceInfo.handle,mOnDataReceiver,bEnableImg?1:0);
		   return true;
	   }
	   return false;
	}

	//重启设备
	public void resetDevice(){
		//必须先关闭device否则车牌会多次识别回调
		closeDevice();
		openDevice();
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
			mDeviceInfo.handle = res;
			mDeviceInfo.deviceName = deviceName;
			mDeviceInfo.ip = ip;
			mDeviceInfo.port = port;
			mDeviceInfo.userName = username;
			mDeviceInfo.userPwd = userpassword;
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
		if((serialOneFlag || serialTwoFlag) && mDeviceInfo.handle >0) {
			//透明通道停止发送数据
			tcpsdk.getInstance().serialStop(mDeviceInfo.handle);
			serialOneFlag = false;
			serialTwoFlag = false;
		}
		if(mDeviceInfo.handle > 0) {
			//关闭设备
			tcpsdk.getInstance().close(mDeviceInfo.handle);
			mDeviceInfo.handle = 0;
		}
		openFlag = false;
	}

	//获取设备信息
	public DeviceInfo getDeviceInfo() {
		return mDeviceInfo;
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
				if(tcpsdk.getInstance().serialStart(mDeviceInfo.handle, serialNum) != 0) //开启透明通道
					return false;
				serialOneFlag = true;
			}
		} else {
			if(!serialTwoFlag) {
				if(tcpsdk.getInstance().serialStart(mDeviceInfo.handle, serialNum) != 0)
					return false;
				serialTwoFlag = true;
			}
		}
		if(tcpsdk.getInstance().serialSend(mDeviceInfo.handle, serialNum, pData, uSizeData) != 0)
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
		return tcpsdk.getInstance().getSnapImageData(mDeviceInfo.handle, imgBuffer, imgBufferMaxLength);
	}
	 
	//播放语音
	public int playVoice( byte[] voice, int interval, int volume, int male) {
		if(!openFlag)
			return -1;
		return tcpsdk.getInstance().playVoice(mDeviceInfo.handle, voice, interval, volume, male);
	}

	//获取设备打开状态
	public boolean getopenFlag() {
		return openFlag;
	}

	//触发车牌识别
	public int forceTrigger() {
		if(!openFlag)
			return  -1;
		return tcpsdk.getInstance().forceTrigger(mDeviceInfo.handle);
	}

	//是否连接中
	public boolean isConnected(){
		return tcpsdk.getInstance().isConnected(mDeviceInfo.handle);
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
		return tcpsdk.getInstance().setIoOutputAuto(mDeviceInfo.handle,uChnId,nDuration);
	}
	
	 public  void setWlistInfoCallBack(tcpsdk.onWlistReceiver recevier) {
		 if(!openFlag)
		  return ;
		  tcpsdk.getInstance().setWlistInfoCallBack(mDeviceInfo.handle, recevier);
	 }

	 public  int  importWlistVehicle(WlistVehicle wllist) {
		   if(!openFlag)
				return -1;
			return tcpsdk.getInstance().importWlistVehicle(mDeviceInfo.handle, wllist);
	   }

	   public  int  deleteWlistVehicle(byte[] plateCode) {
		   if(!openFlag)
				return -1;
			return tcpsdk.getInstance().deleteWlistVehicle(mDeviceInfo.handle, plateCode);
	   }

	   public  int  queryWlistVehicle(byte[] plateCode) {
		   if(!openFlag)
				return -1;
			return tcpsdk.getInstance().queryWlistVehicle(mDeviceInfo.handle, plateCode);
	   }





}
