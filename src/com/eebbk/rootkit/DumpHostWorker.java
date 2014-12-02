package com.eebbk.rootkit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class DumpHostWorker {
	
	private final static String TAG = "DumpHostWorker";
	
	private final static int MSG_UI_SHOW_WAIT = 1;
	private final static int MSG_UI_CANCEL_WAIT = 2;
	
	// install path is in /data/data/xx.xx/
	public final static String INSTALL_PATH = "rootkit";
	
	private Context mContext = null;
	private String mInstallPath = null;
	
	private ProgressDialog mWaitDlg = null;
	
	private Handler mUIHandler = null;
	private Handler mBackHandler = null;
	private HandlerThread mBackThread = null;
	
	private BackgrounWork mWork = null;
	
	class BackgrounWork implements Runnable {

		@Override
		public void run() {
			do {
				// check the /etc/hosts whether is linked
				if (isHostsLinked()) {
					Log.d(TAG, "the hosts file is alreay linked !");
					break;
				}
				
				// execute command, first install the root kit if necessary
				// may be the install failed, but the root kit is already in the finally path(/data/local/tmp)
				// so we give a chance to execute the command
				RootKit.installRootKit(mContext, mInstallPath, false);
				
				// and the execute the dump link work.
				execlCommand(mInstallPath, RootKit.SHELL_COMMAND);
				
			} while(false);
			
			// work is done.
			mUIHandler.sendEmptyMessage(MSG_UI_CANCEL_WAIT);
		}
	}
	
	public DumpHostWorker(Context UIContext) {
		mContext = UIContext;
		mWaitDlg = new ProgressDialog(mContext);
		
		// notice that: you must let the dir have readable and writable permission,
		// because execlCommand will start a new shell process, the  MODE_PRIVATE only 
		// the apk caller process can access !!
		mInstallPath = mContext.getDir(INSTALL_PATH, 
				//Context.MODE_PRIVATE)
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE)
				.getAbsolutePath();
		
		mWork = new BackgrounWork();
		
		mBackThread = new HandlerThread(getClass().getName() + "-Back-Thread-" + this.hashCode());
		mBackThread.start();
		mBackHandler = new Handler(mBackThread.getLooper());
		
		mUIHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				try {
					switch (msg.what) {
					case MSG_UI_SHOW_WAIT:
						mWaitDlg.setCancelable(false);
						mWaitDlg.setMessage("dump host, please wait ...");
						mWaitDlg.show();
						break;
						
					case MSG_UI_CANCEL_WAIT:
						mWaitDlg.dismiss();
						break;
						
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
	}
	
	public void startWork() {
		mUIHandler.sendEmptyMessage(MSG_UI_SHOW_WAIT);
		mBackHandler.post(mWork);
	}
	
	public void cancelWork() {
		mBackHandler.removeCallbacks(mWork);
		mUIHandler.sendEmptyMessage(MSG_UI_CANCEL_WAIT);
	}
	
	public boolean isHostsLinked() {
		Log.d(TAG, "current abi=" + Build.CPU_ABI + ", abi2=" + Build.CPU_ABI2);
		
		// the /etc/ is a link of /system/etc, so we must check the origin file !!
		File fHosts = new File("/system/etc/hosts");
        String cPath = null;
        String aPath = null;

        try {
            cPath = fHosts.getCanonicalPath();
            aPath = fHosts.getAbsolutePath();
            Log.d(TAG, "hosts CanonicalPath is: " + cPath + ", AbsolutePath is: " + aPath);
            
            if (null == cPath && null == aPath) {
            	return false;
            } else {
            	return !cPath.equals(aPath);
            }
            
        } catch (IOException e) {
        	e.printStackTrace();
        	return false;
        } 
	}
	
	public void testWriteHost(String path) {
		String test = "127.0.0.1 localhost \n";
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			// write it to target file
			fos = new FileOutputStream(path);
			fos.write(test.getBytes());
			fos.flush();

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "write to file=" + path + " error!");
		} finally {
			try {
				if (null != is)
					is.close();
				if (null != fos)
					fos.close();
			} catch (Exception e) {
			}
		}
	}
	
	public void testCopyHost(String path) {
		RootKit.copyFile(mContext, "hosts", path, true);
	}
	
	private void execlCommand(String path, String command) {
		Process proc = null;
		DataOutputStream os = null;
		BufferedReader in = null;
		try {
			Log.d(TAG, "execl path: " + path + ", command: " + command);
			
			// new a shell process for run out root kit
			proc = Runtime.getRuntime().exec("/system/bin/sh");
			
			// send shell command
			os = new DataOutputStream(proc.getOutputStream());
			os.writeBytes("echo new a shell, now execl the command ... \n");
			os.writeBytes("cd " + path + " \n");
			// change the file permission, java api copy file can't provider the file have execute permission
			os.writeBytes("chmod 777 " + RootKit.ROOT_BIN + " \n");
			os.writeBytes("chmod 777 " + RootKit.ROOT_DB + " \n");
			os.writeBytes("chmod 777 " + RootKit.SHELL_TOOL + " \n");
			os.writeBytes("chmod 777 " + RootKit.SHELL_COMMAND + " \n");
			// and then execute the root kit
            os.writeBytes("./" + RootKit.ROOT_BIN + " -c " + command + " \n");
            os.writeBytes("exit \n");
            os.flush();
            
            // wait for shell execute
			proc.waitFor();
            
            // get result of execute command
            Log.d(TAG, "command execl: ");
            in = new BufferedReader(new InputStreamReader(proc.getInputStream())); 
            String line = null;    
            while ((line = in.readLine()) != null) {    
            	Log.d(TAG, line);                    
            }
            
			// release resources
			proc.destroy();
			os.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
