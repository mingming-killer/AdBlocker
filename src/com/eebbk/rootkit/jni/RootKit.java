package com.eebbk.rootkit.jni;

import android.util.Log;

public final class RootKit {
	
	private final static String TAG = "RootKit";
	
	private static boolean isReady = false;
	
	public static void init() {
	    if (isReady) return;    
	    try {
	      System.loadLibrary("rootkit");
	      Log.d(TAG, "load librootkit.so successed!");
	      isReady = true;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	public static void execlCommand(String command) {
		if (isReady) 
			execlCommandWithRoot(command);
		else 
			Log.d(TAG, "the so is not load, you should call init first !!"); 
	}
	
  	private static native void execlCommandWithRoot(String command);
  	
}
