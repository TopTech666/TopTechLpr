package com.device;

/**
 * 设备
 */
public class DeviceInfo {

//	   public int id = 0;
	   public int handle = 0; //设备句柄
	   public String deviceName = "设备1";
	   public String ip = "192.168.1.155";
	   public int port = 8131;
	   public String userName = "admin";
	   public String userPwd = "admin";

	public DeviceInfo(String deviceName, String ip, String userName, String userPwd) {
		this.deviceName = deviceName;
		this.ip = ip;
		this.userName = userName;
		this.userPwd = userPwd;
	}

	  public DeviceInfo() {
	  }
	   
	   
	   public boolean equals(Object object)
	   {
		   if(object == this)
			   return true;
		   
		   if(object != null && getClass() == object.getClass()  )
		   {
			   DeviceInfo other = (DeviceInfo)object;
			   
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