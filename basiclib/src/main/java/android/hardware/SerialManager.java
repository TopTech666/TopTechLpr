/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package android.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;


public class SerialManager {
    private static final String TAG = "SerialManager";

    private final Context mContext;
    private final ISerialManager mService;

    private  static int week;
    private  int RETRY =2;
    public    static SerialManager mInstance;
    private   int flag=0;

	 private BroadcastReceiver mBroadcastReceiver_shutdown = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SHUTDOWN)) {
			Log.e("pengyong_in_SerialManager", " shut down mInstance="+mInstance);
		        mInstance.send_system_state_to_mcu(3);
                }
            }
        };




public synchronized boolean sync_system_tiem_from_rtc()	
{

			int[] time =new int[6];	
			time=get_mcu_time();

			Log.e("pengyong", "sync_system_tiem_from_rtc"+"time="+time[0]+":"+time[1]+":"+time[2]+":"+time[3]+":"+time[4]+":"+time[5]);

			Calendar c = Calendar.getInstance();

       			 c.set(Calendar.YEAR, time[0]);
       			 c.set(Calendar.MONTH, time[1]-1);
       		 	 c.set(Calendar.DAY_OF_MONTH, time[2]);

		        c.set(Calendar.HOUR_OF_DAY, time[3]);
       			 c.set(Calendar.MINUTE, time[4]);
       			c.set(Calendar.SECOND, time[5]);
       			c.set(Calendar.MILLISECOND, 0);

       			long when = c.getTimeInMillis();
			Log.e("pengyong","when="+when);

       			if (when / 1000 < Integer.MAX_VALUE) {
				flag=1;
       				 SystemClock.setCurrentTimeMillis(when);
				Log.e("pengyong", "set_rtc on poweron");
       			}

			return true;
}


    /**
     * {@hide}
     */
    public SerialManager(Context context, ISerialManager service) {

        mContext = context;
        mService = service;
	//if(mContext!=null)
         //mContext.registerReceiver(mBroadcastReceiver_shutdown,new IntentFilter(Intent.ACTION_SHUTDOWN), null, null);
    }


     public  static SerialManager getInstance(Context context)
	{

	   		Log.e("pengong","get_instanch in serialManager mInstanch="+mInstance);
		if(mInstance==null)
		{
		
	   		Log.e("pengong","new mInstance");
			IBinder b = ServiceManager.getService("serial");
			mInstance= new SerialManager(context, ISerialManager.Stub.asInterface(b));
	   		Log.e("pengong","new mInstance="+mInstance+"111="+ SerialManager.mInstance);
			try
			{
				//mInstance.openSerialPort("/dev/ttyS1",9600);
                                mInstance.openSerialPort("/dev/ttyS0",9600);      //change to Uart5,2014-07-24.

			} catch (IOException e) {
            			Log.e(TAG, "exception in UsbManager.openDevice", e);
       			}
		
		}
		return mInstance;

	}

       
    /**
     * Returns a string array containing the names of available serial ports
     *
     * @return names of available serial ports
     */
    public String[] getSerialPorts() {
        try {
            return mService.getSerialPorts();
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getSerialPorts", e);
            return null;
        }
    }

    /**
     * Opens and returns the with the given name.
     * The speed of the serial port must be one of:
     * 50, 75, 110, 134, 150, 200, 300, 600, 1200, 1800, 2400, 4800, 9600,
     * 19200, 38400, 57600, 115200, 230400, 460800, 500000, 576000, 921600, 1000000, 1152000,
     * 1500000, 2000000, 2500000, 3000000, 3500000 or 4000000
     *
     * @param name of the serial port
     * @param speed at which to open the serial port
     * @return the serial port
     */
    public void openSerialPort(String name, int speed) throws IOException {
        try {
            		  mService.openSerialPort(name);

             } catch (RemoteException e) {

            Log.e(TAG, "exception in UsbManager.openDevice", e);
        }
        return ;
    }



		private int get_value(byte[]value, int start,int stop)
		{
				int re=0;
	
				String str;
				str=new String(value, start, stop-start+1);
				re= Integer.valueOf(str, 16);
				return re;
				
			}
			

		private byte[]  calu_parity(byte[] value)
		{
			
			byte[] result=new byte[2];
			
			int ret=0;
			int number=0;
			if(value.length<8)
			{		
				Log.e("pengyong", "length is wrong");
				return result;
				
			}
		    
			for(int i=1;i<value.length-3;i+=2)
			{
				
				number=get_value(value, i, i+1);
				ret=ret^number;
				
			}
		
        
            		result= String.format("%02x", ret).getBytes();
			if(result[0]>='a')
				result[0]= (byte)(result[0]-'a'+'A');
			if(result[1]>='a')
				result[1]= (byte)(result[1]-'a'+'A');	
			Log.e("pengong","parity[0]= "+result[0]+"parity[1]="+result[1]);
			return result;
			
		}
		
		private boolean  check_parity(byte[] value)
		{
			byte[]  parity=calu_parity(value);
			if((parity[0]==value[value.length-3])&& (parity[1]==value[value.length-2]))
			{
				
				return true;
			}
			else {
				return false;
			}
			
			
		}



	private String transfer_uart(byte[]  value )
		
		{



			Log.e("pengyong","transfer_uart in  write value="+value.toString());

			String str=new String(value,0,value.length);


			try
               		{
				mService.uart_write(str);
             		} catch (RemoteException e) {
			}

			try
			{
				str=mService.uart_read();

             		} catch (RemoteException e) {
			}

			Log.e("pengyong","transfer_uart out read  "+str);

			return str;

		}

		 public synchronized int[]  get_mcu_time()
		 {

			 int length=0;
			
			 int year,month,day;
			 int  hour,minute,second;
			 String date;
			 String str;
			 
			 byte[] data=new byte[128];	
			 byte[]  resu;
			 byte[] value={0x40,0x30,0x31,0x30,0x31,0x0d};

			 
			for(int i=0;i<RETRY;i++)
			{
             
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}

             		resu=str.getBytes();
             
             
             		Log.e("pengyong",str+"resu[0]="+resu[0]);
             
			if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='1')&&(check_parity(resu)))
		 	{		 
				Log.e("pengyong", "parity is right");
			}
			else
			{
				Log.e("pengyong", "parity is wrong");
				continue;
			
			}

			 year=get_value(resu,5,6)+2000;
			 month=get_value(resu,7,8);
			 day=get_value(resu,9,10);
			 hour=get_value(resu,11,12);
			 minute=get_value(resu, 13, 14);
			 second=get_value(resu, 15, 16);

			int ret[]={year,month,day,hour,minute,second};
			date= String.format("%s","data="+year+"-"+month+"-"+day+":"+hour+":"+minute+":"+second);
			Log.e("pengyong", date);
			return ret;
			}
			
			 int [] ret_false=new int[1];
			 ret_false[0]=-1;
			return ret_false;
 
		 }
		
		 public synchronized boolean  set_mcu_time(int set_value[])
		 {

			 int length=0;
			 String str;
			 int year,month,day;
			 int  hour,minute,second;
			 String date;

			 byte[] result=new byte[2]; 
			 byte[] data=new byte[128];		 			 
			 byte[]  resu;
		 	 byte[] value={0x40, 0x30, 0x32 ,0x30 ,0x45 ,0x30 ,0x33 ,0x31 ,0x35 ,0x30 ,0x31 ,0x31 ,0x45 ,0x31 ,0x39 ,0x31 ,0x43 ,0x0d};
		        result= String.format("%02x", set_value[0]-2000).getBytes();
			if(result[0]>='a')
				result[0]= (byte)(result[0]-'a'+'A');
			if(result[1]>='a')
				result[1]= (byte)(result[1]-'a'+'A');

			value[3]=result[0];
			value[4]=result[1];

			for(int i=1;i<6;i++)
			{
		       	 	result= String.format("%02x", set_value[i]).getBytes();
				if(result[0]>='a')
				result[0]= (byte)(result[0]-'a'+'A');
				if(result[1]>='a')
				result[1]= (byte)(result[1]-'a'+'A');

				value[2*i+3]=result[0];
				value[2*i+4]=result[1];

			}

			result=  calu_parity(value);
			value[15]=result[0];
			value[16]=result[1];
			 
					 
			 
			for(int i=0;i<RETRY;i++)
			{
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}	 
	            	resu=str.getBytes();
	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='2')&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "parity is right");
			 }
			else
			{
				 Log.e("pengyong", "parity is wrong");
				continue;
			}

				return true;
			}
				return false;
		 }
		
		 
		 public synchronized boolean set_onoff_by_week(int set_value[])
		 {
			 
			 int length=0;

			 String str;
			int i; 
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x33,0x30,0x31,0x30,0x31,0x30,0x42,0x30,0x32,0x31,0x36,0x31,0x43,0x0d};
			 byte[] resu;
			 byte[] result=new byte[2];	

			for( i=0;i<5;i++)
			{
		       	 	result= String.format("%02x", set_value[i]).getBytes();
				if(result[0]>='a')
				result[0]= (byte)(result[0]-'a'+'A');
				if(result[1]>='a')
				result[1]= (byte)(result[1]-'a'+'A');

				value[2*i+3]=result[0];
				value[2*i+4]=result[1];

			}

			result=  calu_parity(value);
			value[2*i+3]=result[0];
			value[2*i+4]=result[1];

			for(i=0;i<16;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}
			 


			for(i=0;i<RETRY;i++)
			{
				str=transfer_uart(value);
				if(str==null)
				{
					continue;
				}
	            		resu=str.getBytes();
	            
	            		Log.e("pengyong",str+"resu[0]="+resu[0]);
 
			 
			 	if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='3')&&(check_parity(resu)))
			 	{		 
				 Log.e("pengyong", "set_autopoweron_by_week :parity is right");
			 	}
			 	else
				 {
				 Log.e("pengyong", "set_autopoweron_by_week: parity is wrong");
					continue;
				}

			 return true;
			}
				
			return false;
		 }
		 
	     public synchronized  boolean save_system_time_to_rtc(long millionsecond)
	     {
			 

		        Log.e("pengyong", "set_mcurtc_by_system millionsecond="+millionsecond);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(millionsecond);// add by pengyong 
			int year=calendar.get(Calendar.YEAR);
			int month=calendar.get(Calendar.MONTH)+1;
			int day=calendar.get(Calendar.DAY_OF_MONTH);
			int hour=calendar.get(Calendar.HOUR_OF_DAY);
			int minute=calendar.get(Calendar.MINUTE);
			int second=calendar.get(Calendar.SECOND);
			int[]  time={year,month,day,hour,minute,second};
			Log.e("pengyong ","flag="+flag+"time="+year+":"+month+":"+day+":"+hour+":"+minute+":"+second);
		//	int time[6]={calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),
                 //             calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)};
			return set_mcu_time(time);
	     }

	

		 public synchronized  int[]  get_onoff_by_week(int week)
		 {
			 		 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x34,0x30,0x31,0x30,0x35,0x0d};
			 byte[] resu;
			 byte[] result=new byte[2]; 
			 int [] ret=new int[4];

  	 			result= String.format("%02x", week).getBytes();
				if(result[0]>='a')
				result[0]= (byte)(result[0]-'a'+'A');
				if(result[1]>='a')
				result[1]= (byte)(result[1]-'a'+'A');

				value[3]=result[0];
				value[4]=result[1];
			
				result=  calu_parity(value);
				value[5]=result[0];
				value[6]=result[1];

			for(int i=0;i<8;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}

			 	
			 
			for(int i=0;i<RETRY;i++)
			{


				str=transfer_uart(value);
				if(str==null)
				{	
					continue;
				}
		           	 resu=str.getBytes();
	           

			 ret[0]=get_value(resu,7,8);
			 ret[1]=get_value(resu,9,10);
			 ret[2]=get_value(resu,11,12);
			 ret[3]=get_value(resu,13,14);

	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='4')&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "get_autopoweron_by_week :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "get_autopoweron_by_week: parity is wrong");
				
				continue;
			}		 

				return ret;
			}


			 int [] ret_false=new int[1];
			 ret_false[0]=-1;
			return ret_false;
			
		 }
		 
		 public synchronized boolean enable_onoff_by_week(int set_value[])
		 {
			 
	 		 
		 int length=0;
		 String str;
		 byte[] data=new byte[128];		 			 
		 byte[] value= {0x40,0x30,0x35,0x30 ,0x30,0x30,0x35,0x0d};
		 byte[] resu;
		byte[] result;	
		
		int week=0;
		 
		for(int i=0;i<7;i++)
		{
			if(set_value[i]==1)
			{

				week |= 1<<i;	
			}
			else if (set_value[i]==0)
			{
				week &= ~(1<<i)	;
			}
			else
			{
                		Log.e("pengyong","parameter is error");
				return false;
			}
		}
		
		result= String.format("%02x", week).getBytes();
		value[3]=result[0];
		value[4]=result[1];

		result=  calu_parity(value);
		value[5]=result[0];	
		value[6]=result[1];		

		for(int i=0;i<RETRY;i++)
		{
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}
                	resu=str.getBytes();
            
                	Log.e("pengyong",str+"resu[0]="+resu[0]);
            			
		 	if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='5')&&(check_parity(resu)))
		 	{		 
				 Log.e("pengyong", "set_autopoweron_command :parity is right");
		 	}
		 	else
		 	{
			 Log.e("pengyong", "set_autopoweron_command: parity is wrong");
			continue;
			
			}		 
				return true;
		}	 
			return false;
		 }
		 


		 public synchronized int check_onoff_by_week()
		 {
	 		 
		 int length=0;
		 String str;
		 byte[] data=new byte[128];		 			 
		 byte[] value= {0x40,0x30,0x36,0x30,0x36,0x0d};
		 byte[] resu;
		 	

		 
		 
		for(int i=0;i<RETRY;i++)
		{
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}
            		resu=str.getBytes();
            
            		Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 		
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='6')&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "get_autopoweron_command :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "get_autopoweron_command: parity is wrong");
				continue;
				
			}		 


			if(resu[6]==0x30)
			{

				return 1;
			}
			else
			{
				return 0;
			}
			 
		   }
			return -1;
		 }
		 


		 public synchronized String get_mcu_id()
		 {

			 int length=0;
			
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value={0x40  ,0x30  ,0x37  ,0x30  ,0x37  ,0x0d};
			 byte[] resu;
			 	
			for(int i=0;i<RETRY;i++)
			{
			 
			str=transfer_uart(value);

			if(str==null)
			{

				continue;
			}

	            	resu=str.getBytes();
	            

	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='7')&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "get_id :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "get_id: parity is wrong");
				continue;
			}


			 return new String(resu,5,24);
			}

			return null;
		 }



		 public synchronized boolean send_system_state_to_mcu(int state)
		 {
			 
			 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x38,0x30,0x31,0x30,0x39,0x0d};
			 byte[] resu;
			 byte[] result;
			value[4]=(byte) (state+0x30);	
			
			result=  calu_parity(value);
			value[5]=result[0];	
			value[6]=result[1];	
			for(int i=0;i<8;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}


			for(int i=0;i<RETRY;i++)
			{
				str=transfer_uart(value);

				if(str==null)
				{
				 	Log.e("pengyong", "send system state to mcu again because str=null");
					continue;
				}
				
	            		Log.e("pengyong","str="+str);
	            		resu=str.getBytes();
	            		Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			
			 	if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='8')&&(check_parity(resu)))
			 	{		 
				 Log.e("pengyong", "send_A20_state :parity is right");
			 	}
			 	else
			 	{
				 Log.e("pengyong", "send_A20_state: parity is wrong");
					continue;	
				}		 

				return true;
			}	
			return false;
		 }
		 


		 public synchronized boolean send_heartbeat_to_mcu()
		 {
			 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x39,0x30,0x39,0x0d};
			 byte[] resu;
			
			for(int i=0;i<RETRY;i++)
			{
 
				str=transfer_uart(value);
				
				if(str==null)
				{
					continue;
				}
	            		resu=str.getBytes();
	            
	            		Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
				 
			 
			 	if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='9')&&(check_parity(resu)))
			 	{		 
				 Log.e("pengyong", "send_heartbeat :parity is right");
			 	}
			 	else
				 {
				 Log.e("pengyong", "send_heartbeat: parity is wrong");
					continue;
				}		 
		
				 return true;
				
			}
			return false;
		 }
		 


/*
 private class HeartThread extends Thread 
{		 

     	HeartThread() 
	{
		super();
	}

	 public void run() 
	{
		
		while(stop==false)
		{
		Log.e("pengong","pid="+Thread.currentThread().getId());
	   	Log.e("pengong","thread  mInstanch="+mInstance);

   		try {
		    Thread.sleep(10000);
		   } catch (InterruptedException e) {
	    		e.printStackTrace();
	   		}


	  		mInstance.send_heartbeat();

		}
	}
}
*/		 
		 public synchronized boolean enable_watchdog(int enable)
		 {
		 
			 int length=0;



			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x41,0x30,0x30,0x30,0x41,0x0d};
			 byte[] resu;
			 byte[] result;

			
			for(int i=0;i<RETRY;i++)
			{
			 	
			if(enable==1)			
			{
				value[4]=0x30;
			}
			else
			{
				value[4]=0x31;
			}

			result=  calu_parity(value);
			value[5]=result[0];
			value[6]=result[1];

			
			for( i=0;i<8;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}

			str=transfer_uart(value);
			if(str==null)	
			{

				continue;
			}

	            	resu=str.getBytes();
	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]=='A')&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "enable_watchdog :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "enable_watchdog: parity is wrong");
				continue;
				
			}		 
		
			return true ;
			}
			return false;
	 }
		 

		 public synchronized boolean shutdown_system()
		{

					 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x42,0x30,0x30,0x30,0x42,0x0d};
			 byte[] resu;
			 byte[] result;
			value[4]=0x30;
			result=  calu_parity(value);
			value[5]=result[0];
			value[6]=result[1];

			for(int i=0;i<8;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}

			for(int i=0;i<RETRY;i++)
			{ 
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}
	            	resu=str.getBytes();
	            
	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]==0x42)&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "send_shutdown :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "send_shutdown: parity is wrong");
				continue;	
			 }

			return true;

			}
			return false;

		}
		 public synchronized boolean restart_system()
		{

			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40,0x30,0x42,0x30,0x30,0x30,0x42,0x0d};
			 byte[] resu;
			 byte[] result;
			value[4]=0x31;
			result=  calu_parity(value);
			value[5]=result[0];
			value[6]=result[1];

			for(int i=0;i<8;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}

			for(int i=0;i<RETRY;i++)
			{ 
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}
	            	resu=str.getBytes();
	            
	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]==0x42)&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "send_shutdown :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "send_shutdown: parity is wrong");
				continue;	
			 }

			return true;

			}
			return false;
		}

		 public synchronized int get_mcu_firmware()

		 {
			 
 
			 int length=0;
			 String str;

			 byte[] data=new byte[128];		 			 

			for(int i=0;i<RETRY;i++)
			{

			 byte[] value= {0x40,0x30,0x43,0x30,0x43,0x0d};
			 byte[] resu;
			 str=transfer_uart(value);

			if(str==null)
			{
				continue;	
			}
	            		resu=str.getBytes();
	            		Log.e("pengyong",str+"resu[0]="+resu[0]);
			 	if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]==0x43)&&(check_parity(resu)))
			 	{		 
					 Log.e("pengyong", "get_mcu_firmware :parity is right");
			 	}
			 	else
			 	{
					 Log.e("pengyong", "get_mcu_firmware: parity is wrong");

					continue;
				
				}		 
		

			 	return get_value(resu,5,6);

			}

			return -1;
		 }
		 
		 
		 
		 
		 public synchronized boolean set_onoff_by_day(int[] set_value2)
		 {
			 		 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] result=new byte[2];		 			 
                         byte[] value={0x40,0x30 ,0x45 ,0x30 ,0x45 ,0x30 ,0x31 ,0x30 ,0x31 ,0x30 ,0x35 ,0x30 ,0x36 ,0x30 ,0x45 ,0x30 ,0x31 ,0x30 ,0x31 ,0x30 ,0x34 ,0x30 ,0x33 ,0x30 ,0x34 ,0x0d};
			 byte[] resu;
			 int i=0;	
			
			Log.e("pengyong", "set_autopower_by_day: ENTER");

                                //¿ª»ú  Äê
				result= String.format("%02X", set_value2[0]-2000).getBytes();
				//if(result[0]>='a')
				//result[0]= (byte)(result[0]-'a'+'A');
				//if(result[1]>='a')
				//result[1]= (byte)(result[1]-'a'+'A');
				value[3]=result[0];
				value[4]=result[1];

                                for( i=1; i<10; i++)  //
				{
					result= String.format("%02X", set_value2[i]).getBytes();
					value[2*i+3]=result[0];
					value[2*i+4]=result[1];
				}

				//¹Ø»ú  Äê
				result= String.format("%02X", set_value2[5]-2000).getBytes();
				//if(result[0]>='a')
				//result[0]= (byte)(result[0]-'a'+'A');
				//if(result[1]>='a')
				//result[1]= (byte)(result[1]-'a'+'A');
				value[2*5+3]=result[0];
				value[2*5+4]=result[1];



				result=  calu_parity(value);
				value[2*i+3]=result[0];
				value[2*i+4]=result[1];

			for( i=0;i<26;i++)		
			{
				Log.e("pengyong", String.format("0x%02x",value[i]));

			}

			 
			for( i=0;i<RETRY;i++)
			{
				str=transfer_uart(value);
				if(str==null)
				{
					continue;
				}

	            		resu=str.getBytes();
	            
	            		Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 	if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]==0x45)&&(check_parity(resu)))
			 	{		 
				 Log.e("pengyong", "set_autopower_by_day :parity is right");
			 	}
			 	else
			 	{
				 Log.e("pengyong", "set_autopower_by_day: parity is wrong");
				continue;	
				}		 
			
				return true;
			}
			return false;
		 }
		 
		 public synchronized int[]  get_onoff_by_day()
		 {
			 		 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= { 0x40,  0x30,  0x46,  0x30,  0x46 , 0x0d};
			 byte[] resu;
			 	
			 
			for(int i=0;i<RETRY;i++)
			{

			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}
	       	           resu=str.getBytes();
	            
	                Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]=='0')&&(resu[4]==0x46)&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "get_autopower_by_day :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "get_autopower_by_day: parity is wrong");
				continue;
				
			}		 

			int open_year=get_value(resu,5,6)+2000;
			int open_month=get_value(resu,7,8);
			 int open_day=get_value(resu,9,10);
			 int open_hour=get_value(resu,11,12);
			 int open_minute=get_value(resu, 13, 14);

			int shut_year=get_value(resu,15,16)+2000;
			int shut_month=get_value(resu,17,18);
			int shut_day=get_value(resu,19,20);
			 int shut_hour=get_value(resu, 21, 22);
			 int shut_minute=get_value(resu, 23, 24);

			int ret[]={open_year,open_month,open_day,open_hour,open_minute,shut_year,shut_month,shut_day,shut_hour,shut_minute};
			return ret;	

			}

			int ret_false[] ={-1};	
			return ret_false;
		 }
		 
		 
		 public synchronized  boolean  enable_onoff_by_day(int enable)
		 {
			 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= {0x40 ,0x31,0x30 ,0x30 ,0x30, 0x31,0x30,0x0d};
			 byte[] resu;
			 byte[] result;
			 	

			if(enable==1)
			{

				value[4]=0x30;
			}
			else
			{
				value[4]=0x31;

			}

				result=  calu_parity(value);
				value[2+3]=result[0];
				value[2+4]=result[1];
			
			for(int i=0;i<RETRY;i++)
			{
			 
			str=transfer_uart(value);
			if(str==null)
			{
				continue;
			}
	            	resu=str.getBytes();
	            
	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]==0x31)&&(resu[4]==0x30)&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "set_autopower_by_day_command :parity is right");
			 }
			 else
			 {
				 Log.e("pengyong", "set_autopower_by_day_command: parity is wrong");
				continue;	
			}		 
				return true;	
			} 
				return false;
		 }
		
		
		 public  synchronized int check_onoff_by_day()
		 {
			 
			 int length=0;
			 String str;
			 byte[] data=new byte[128];		 			 
			 byte[] value= { 0x40,0x31,0x31,0x31,0x31,0x0d };
			 byte[] resu;
			 	
			 
			for(int i=0;i<RETRY;i++)
			{

				str=transfer_uart(value);
				if(str==null)
				{	
					continue;
				}
	                   resu=str.getBytes();
	            

	            	Log.e("pengyong",str+"resu[0]="+resu[0]);
			 
			 if((resu[2]=='0')&&(resu[3]==0x31)&&(resu[4]==0x31)&&(check_parity(resu)))
			 {		 
				 Log.e("pengyong", "get_autopower_by_day_command :parity is right");
				
			 }
			 else
			 {
				 Log.e("pengyong", "get_autopower_by_day_command: parity is wrong");
				continue;		
			}		 
		
	
			 if(resu[6]==0x30)
			{
				return 1;	
			}
			else
			{
				return 0;
			}

			}
			return -1;
		 }
		
}
