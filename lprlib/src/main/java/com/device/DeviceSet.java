package com.device;

import com.ToastUtils;
import com.vz.WlistVehicle;
import com.vz.tcpsdk;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 通道机设备操作类
 */
public class DeviceSet {
	
	private DeviceInfo    mDeviceInfo = null;
	private VideoSetView  mVideoSetView = null;
	
	private  boolean     serialOneFlag = false;
	private  boolean     serialTwoFlag = false;
	private  boolean     openFlag = false;
	private  tcpsdk.OnDataReceiver mOnDataReceiver=null ;
	private  int         m_bEnableImg = 0;
	private tcpsdk.onWlistReceiver  mOnWlistReceiver =null;
	 
	public DeviceSet(DeviceInfo deviceInfo, VideoSetView videoSetView ){
		mDeviceInfo = deviceInfo;
		mVideoSetView = videoSetView;
		if(mVideoSetView != null)
			mVideoSetView.setDeviceName(mDeviceInfo.deviceName);
	}

	/**
	 * 打开设备
	 * @return
	 */
	public boolean open( ) {
	   return open(mDeviceInfo.deviceName, mDeviceInfo.ip, mDeviceInfo.port,mDeviceInfo.userName,mDeviceInfo.userPwd);
	}

	/**
	 * 打开设备
	 * @return
	 */
	public boolean open(String deviceName, String ip, int port, String username, String userpassword) {
		if(mDeviceInfo.handle >0) //设备已经打开？
			return false;
		if(mVideoSetView == null)
			return false;
		mVideoSetView.setDeviceName(deviceName);
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
			mVideoSetView.setUrlip(ip);
			if(mOnDataReceiver != null)//设置车牌识别回调
			    tcpsdk.getInstance().setPlateInfoCallBack(mDeviceInfo.handle,mOnDataReceiver,m_bEnableImg);
			openFlag = true;
		 	return true;
		} else {
			ToastUtils.showToast("打开设备失败");
		}
		 return false;
	}

	/**
	 * 关闭设备
	 * @return
	 */
	public void close() {
		if(serialOneFlag || serialTwoFlag) {
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

	/**
	 * 播放视频
	 */
	public void playVideo() {
		if(!openFlag && !open())
			return ;
		mVideoSetView.startPlay();
	}

	/**
	 * 关闭视频
	 */
	public void stopVideo() {
		if(!openFlag)
			return ;
		mVideoSetView.stopPlay();
	}

	//获取设备信息
	public DeviceInfo getDeviceInfo() {
		return mDeviceInfo;
	}

	/**
	 * 设置车牌识别回调
	 * @param reciver
	 * @param bEnableImg 回调中是否包含截图
	 */
	public void setPlateInfoCallBack(tcpsdk.OnDataReceiver reciver , int bEnableImg) {
		mOnDataReceiver = reciver;
		m_bEnableImg  = bEnableImg;
	}
	
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

	
	public boolean getopenFlag() {
		return openFlag;
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

	   //触发车牌识别
	   public  int forceTrigger( ) {
		   return tcpsdk.getInstance().forceTrigger(mDeviceInfo.handle);
	   }

	/**
	 * 开闸
	 * @param uChnId  通道号
	 * @param nDuration
	 * @return
	 */
	   public int  setIoOutputAuto( short uChnId, int nDuration) {
		   return tcpsdk.getInstance().setIoOutputAuto(mDeviceInfo.handle,uChnId,nDuration);
	   }

		/**
		 * 暂停视频
		 */
	    public void pause() {
			mVideoSetView.pause();
		}

	/**
	 * 开启视频
	 */
	public void resume() {
			mVideoSetView.resume();
		}
	 
//		public void setPlateImage(Bitmap bmp) {
//			vedio_set.setPlateImage(bmp);
//		}

//		public void setTrriglePlateText(String plateText)
//		{
//			vedio_set.setTrriglePlateText(plateText);
//		}
//
//	    public void ZoomOutVedio()
//	    {
//	    	vedio_set.ZoomOutVedio();
//	    }
//	    public void ZoomInVedio ()
//	    {
//	    	vedio_set.ZoomInVedio();
//	    }
//	    public void ZoomOutImage()
//	    {
//	    	vedio_set.ZoomOutImage();
//	    }
//	    public void ZoomInImage ()
//	    {
//	    	vedio_set.ZoomInImage();
//	    }
//	public VedioSetVeiw getVedioSetVeiw()
//	{
//		return vedio_set;
//	}
	
	//获取分辨率
	public boolean getFrameSize(StringBuffer rate)
	{
		//192.168.3.30/vb.htm?paratest=bitrate.0
//		String url = device_info.ip + "/vb.htm";
//		String param = "paratest=videosizexy.0";
		
//		String url = "http://"+ device_info.ip + "/login.php";
//		String param = device_info.username +":"+device_info.userpassword;
//		
//		byte [] decodeData = encrypt(param,"天天");//
//		Charset cs = Charset.forName ("GBK");
//	      ByteBuffer bb = ByteBuffer.allocate (decodeData.length);
//	      bb.put (decodeData);
//	        bb.flip ();
//	       CharBuffer cb = cs.decode (bb);
//	       
//	   char [] tempData =    cb.array();
//		
//		
//		String tempParam = String.valueOf(tempData);
//		tempParam = "HgAAAAUAAAChBEKzzjoXVHZdSQ==";
//		 
//		
//		String resText = HttpRequest.sendPost(url, tempParam);
//		
//		 url = "http://"+device_info.ip + "/vb.htm";
//		 param = "paratest=mainstreamsupport.0"; //"paratest=bitrate.0";
//		
//		 resText = HttpRequest.sendGet(url, param);
		
		return true;
	}

	 /** 
	  * 加密 
	  *  
	  * @param content 需要加密的内容 
	  * @param password  加密密码 
	  * @return 
	  */  
	 public static byte[] encrypt(String content, String password) {
	         try {             
	                 KeyGenerator kgen = KeyGenerator.getInstance("AES");
	                 kgen.init(128, new SecureRandom(password.getBytes()));
	                 SecretKey secretKey = kgen.generateKey();
	                 byte[] enCodeFormat = secretKey.getEncoded();  
	                 SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
	                 Cipher cipher = Cipher.getInstance("AES");// 创建密码器
	                 byte[] byteContent = content.getBytes("utf-8");  
	                 cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
	                 byte[] result = cipher.doFinal(byteContent);  
	                 return result; // 加密   
	         } catch (NoSuchAlgorithmException e) {
	                 e.printStackTrace();  
	         } catch (NoSuchPaddingException e) {
	                 e.printStackTrace();  
	         } catch (InvalidKeyException e) {
	                 e.printStackTrace();  
	         } catch (UnsupportedEncodingException e) {
	                 e.printStackTrace();  
	         } catch (IllegalBlockSizeException e) {
	                 e.printStackTrace();  
	         } catch (BadPaddingException e) {
	                 e.printStackTrace();  
	         }  
	         return null;  
	 }
}
