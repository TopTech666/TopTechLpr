package com.device.player;

import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class FrameQueue {
	private List<Frame> queue = new ArrayList<Frame>();
  

	public synchronized void addFrameToQueue(Frame frame) throws Exception {
		if (null == frame) {
			return;
		}
		try{ 
			
			queue.add(frame);
		}
		catch( UnsupportedOperationException e )
		{
			Log.i("error", e.getMessage());
		}
		
	}


	public synchronized Frame getFrameFromQueue() throws Exception {
		if (queue.size() > 0) {
			return queue.remove(0);
		}
		return null;
	}


	public synchronized void clear() {
		try {
			queue.clear();
		} catch (Exception e) {
			
		}
	}
	
	public synchronized int size() {
		return queue.size();
	}
}
