package com.example.admin.mytest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态类
 * 
 * @author Administrator
 * 
 */
public class NetStatus {

	public static final int NO_NET = -1;// 没有网络

	public static boolean isConnect(Context context) {
		// 网络管理对象
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager == null) {
			return false;
		}

		// 获取网络状态信息对象
		NetworkInfo info = conManager.getActiveNetworkInfo();
		// 进行网络状态信息判断
		if (info == null || !info.isConnected()
				|| info.getState() != NetworkInfo.State.CONNECTED) {
			return false;
		}

		return true;
	}

	/**
	 * 得到网络类型
	 * @param context
	 * @return WIFI、2G、3G、4G什么了 {@link NetworkInfo#getType()},{@link android.telephony.TelephonyManager}
	 *
	 */
	public static int getNetType(Context context){
		if (!isConnect(context)){
			return NO_NET;
		}

		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取网络状态信息对象
		NetworkInfo info = conManager.getActiveNetworkInfo();
		int netType = info.getType();
		if (netType == ConnectivityManager.TYPE_MOBILE){// 如果是手机网络，需要细分至3G,4G
			netType = info.getSubtype();
		}

		return netType;
	}


}
