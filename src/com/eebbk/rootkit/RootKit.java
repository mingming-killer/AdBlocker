package com.eebbk.rootkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

public final class RootKit {
	
	private final static String TAG = "RootKit";
	
	private final static String ABI = Build.CPU_ABI;
	private final static int FILE_BUFF_SIZE = 1024;
	
	final static String ROOT_BIN = "run_root_shell";
	final static String ROOT_DB = "device.db";
	final static String SHELL_TOOL = "busybox";
	final static String SHELL_COMMAND = "dump_hosts.sh";
	
	// need copy files, stored in assets
	private final static String[] sInstallFiles = {
		ABI + File.separator + ROOT_BIN,
		ROOT_DB,
		ABI + File.separator + SHELL_TOOL,
		SHELL_COMMAND
	};
	
	public static boolean installRootKit(Context context, String path, boolean force) {
		if (null == context || null == path) {
			Log.d(TAG, "context or path is null, install failed !!");
			return false;
		}
		
		boolean ret = true;
		String dstFile = null;
		String dstPath = null;
		
		for (String file : sInstallFiles) {
			dstFile = getFileNameFromPath(file);
			dstPath = path + File.separator + dstFile;
			Log.d(TAG, "install " + file + " to " + dstPath + " ...");
			if (!copyFile(context, file, dstPath, force)) {
				ret = false;
				Log.d(TAG, file + " installed failed !");
			} else {
				Log.d(TAG, file + " installed success !");
			}
		}
		
		return ret;
	}
	
	static boolean copyFile(Context context, String src, String dst, boolean force) {
		if (null == context || null == src || null == dst) {
			Log.d(TAG, "copy file failed !!");
			return false;
		}
		
		AssetManager mgr = context.getAssets();
		InputStream is = null;
		FileOutputStream fos = null;
		
		try {
			String dstDir = getParentDir(dst);
			File fdir = new File(dstDir);
			File file = new File(dst);
			
			// prepare install, we create the out put dir if don't existed
			if (!fdir.exists() && fdir.mkdirs()) {
				Log.d(TAG, "can't create target file dir: " + dstDir);
				return false;
			}
			if (force) {
				if (file.exists()) {
					Log.d(TAG, "the file " + dst + " is existed, but force install, we delete it !");
					if (!file.delete()) {
						Log.d(TAG, "file " + dst + " delete failed !");
						return false;
					}
				}
			} else if (file.exists()) {
				Log.d(TAG, "the file " + dst + " is existed, ignore it !");
				return true;
			}
			
			is = mgr.open(src);
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[FILE_BUFF_SIZE];
			
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != is) is.close();
				if (null != fos) fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Get file name for a full path. It's get string since the last char '/'.
	 * 
	 * @param path String of path.
	 * @return File name.
	 */
	public final static String getFileNameFromPath(String path) {
		if (null == path) {
			return null;
		}
		
		int index = path.lastIndexOf('/');
		if (index < -1) {
			return null;
		}
		
		return path.substring(index+1);
	}
	
	/**
	 * Get give path parent directory.
	 * 
	 * @param path Path string.
	 * @return Parent path or null.
	 */
	public final static String getParentDir(String path) {
		if (null == path) {
			return null;
		}
		
		try {
			int last = path.lastIndexOf("/");
			if (last <= -1) {
				return null;
			}
			
			return path.substring(0, last);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
  	
}
