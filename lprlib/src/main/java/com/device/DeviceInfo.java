package com.device;

/**
 * 设备
 */
public class DeviceInfo {

	   public int handle = 0; //设备句柄
	   public int id = 0;
	   public String deviceName = "设备1";
	   public String ip = "192.168.7.155";
	   public int port = 8131;
	   public String userName = "admin";
	   public String userPwd = "admin";
	   
	   public DeviceInfo(int idInit) {
		   id = idInit;
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