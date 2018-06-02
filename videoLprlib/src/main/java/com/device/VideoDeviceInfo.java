package com.device;

/**
 * 设备
 */
public class VideoDeviceInfo {

//	   public int id = 0;
	   public int handle = 0; //设备句柄
	   public String deviceName = "设备1";
	   public String ip = "192.168.1.155";
	   public int port = 8131;

	   //相机的帐号和密码目前逻辑是写死admin
	   public String userName = "admin";
	   public String userPwd = "admin";
	   //抬杠IO
	   public short chnId = 0;

	public VideoDeviceInfo(String deviceName, String ip, String userName, String userPwd) {
		this.deviceName = deviceName;
		this.ip = ip;
		this.userName = userName;
		this.userPwd = userPwd;
	}

	  public VideoDeviceInfo() {
	  }
	   
	   
	   public boolean equals(Object object)
	   {
		   if(object == this)
			   return true;
		   
		   if(object != null && getClass() == object.getClass()  )
		   {
			   VideoDeviceInfo other = (VideoDeviceInfo)object;
			   
			   if( handle == other.handle &&
					   ip == other.ip &&
					   port == other.port &&
					   userName == other.userName &&
					   userPwd == other.userPwd) {
				   return true;
			   }
		   }
		   return false;
	   }
	   
	   public int hashCode() {
         return handle;
	   }
}