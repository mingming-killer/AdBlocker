package com.eebbk.adblocker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.eebbk.rootkit.DumpHostWorker;

import dalvik.system.DexFile;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
	
	private static final String TAG = "AdBlocker";
	
	private PackageManager mPm;
	private ArrayList<String> mInstalledPkgNames = null;
	
	private DumpHostWorker mWorker = null;
	
	private Button mBtnScan = null;
	
	private String mHostPath = null;
	private Button mBtnLinkHost = null;
	private Button mBtnWriteHost = null;
	private Button mBtnCopyHost = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initConfig();
		initView();
	}
	
	private void initConfig() {
		mPm = getPackageManager();
		mInstalledPkgNames = new ArrayList<String> ();
		
		mWorker = new DumpHostWorker(this);
		
		//mHostPath = getDir(DumpHostWorker.INSTALL_PATH, 
		//		Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE)
		//		.getAbsolutePath() + "/hosts";
		mHostPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.hosts";
	}
	
	private void initView() {
		mBtnScan = (Button) findViewById(R.id.btn_scan);
		mBtnLinkHost = (Button) findViewById(R.id.btn_link_host);
		mBtnWriteHost = (Button) findViewById(R.id.btn_write_host);
		mBtnCopyHost = (Button) findViewById(R.id.btn_copy_host);
		
		mBtnScan.setOnClickListener(this);
		mBtnLinkHost.setOnClickListener(this);
		mBtnWriteHost.setOnClickListener(this);
		mBtnCopyHost.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		if (view.equals(mBtnScan)) {
			onBtnScanClicked();
		} else if (view.equals(mBtnLinkHost)) {
			onBtnLinkHostClicked();
		} else if (view.equals(mBtnWriteHost)) {
			onBtnWriteHostClicked();
		} else if (view.equals(mBtnCopyHost)) {
			onBtnCopyHostClicked();
		}
	}
	
	private void onBtnScanClicked() {
		collectInstalledPkgs(mInstalledPkgNames);
		for (String pkgName : mInstalledPkgNames) {
			scanPkgDexFile(pkgName);
		}
	}
	
	private void onBtnLinkHostClicked() {
		mWorker.startWork();
	}
	
	private void onBtnWriteHostClicked() {
		Log.d(TAG, "write file: " + mHostPath);
		mWorker.testWriteHost(mHostPath);
	}
	
	private void onBtnCopyHostClicked() {
		Log.d(TAG, "write file: " + mHostPath);
		mWorker.testCopyHost(mHostPath);
	}
	
	private void collectInstalledPkgs(ArrayList<String> output) {
		if (null == output) {
			Log.d(TAG, "output data is null, we can't collect installed pkgs");
			return;
		}
		
		List<PackageInfo> pkgInfos = mPm.getInstalledPackages(0);
		
		output.clear();
		for (PackageInfo info : pkgInfos) {
			if (null == info) continue;
			output.add(info.packageName);
			Log.d(TAG, "collect pkg: " + info.packageName);
		}
		
		pkgInfos.clear();
	}
	
	private void scanPkgDexFile(String pkgName) {
		if (null == pkgName) {
			Log.d(TAG, "the target pkgName can't null !");
			return;
		}
		
		ApplicationInfo info = null;
		
		try {
			info = mPm.getApplicationInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		if (null == info) {
			return;
		}
		
		// we ignore system app or our bbk app
		if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0 
				|| (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 
				|| pkgName.startsWith("com.eebbk") 
				|| pkgName.startsWith("com.bbk")) {
			Log.d(TAG, "pkg: " + pkgName + " is system app or bbk app, we ignore it.");
			return;
		}
		
		try {
			DexFile dexFile = new DexFile(info.sourceDir);
			Enumeration<String> entries = dexFile.entries();
			Log.d(TAG, "scan pkg: " + pkgName + " source path: " + info.sourceDir);
			while (entries.hasMoreElements()) {
				Log.d(TAG, entries.nextElement());
			}
			Log.d(TAG, " ");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "scan dex file failed !");
		}
	}
	
}
