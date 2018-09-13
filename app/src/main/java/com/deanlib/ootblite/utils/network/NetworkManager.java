package com.deanlib.ootblite.utils.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.deanlib.ootblite.OotbConfig;
import com.deanlib.ootblite.utils.DLog;
import com.deanlib.ootblite.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;


public class NetworkManager {

	public static final int TYPE_NO_CONNECTION = 0x00;
	public static final int TYPE_WIFI = 0x01;
	public static final int TYPE_CMWAP = 0x02;
	public static final int TYPE_CMNET = 0x03;

	
	public interface NetworkListener{
		
		void onNetworkDisconnect();
		void onNetworkConnected(int type);
		
	}
	
	List<NetworkListener> listeners = new ArrayList<NetworkListener>();
	public void addOnNetworkListener(NetworkListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	/**
	 * 移除网络监听
	 * @param listener
	 */
	public void removeNetworkListener(NetworkListener listener){
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	private static NetworkManager instance  = null;
	
	public static boolean enable = false;

	public static NetworkManager getInstance(){
		if(instance == null){
			instance = new NetworkManager();
		}
		return instance;
	}

	/**
	 * 初始化
	 */
	public void init(){
		if(DeviceUtils.isNetworkConnected()&& DeviceUtils.getNetworkType()!=0){
			enable = true;
		}
	}
	
	NetworkChangedReceiver ncr = new NetworkChangedReceiver();
	
	public void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		OotbConfig.app().registerReceiver(ncr, filter);
	}
	
	public void unregisterNetworkReceiver(){
		OotbConfig.app().unregisterReceiver(ncr);
	}
	
	
	/**
	 * 网络状态接收器
	 * @author louis.lv
	 *
	 */
	class NetworkChangedReceiver extends BroadcastReceiver {
		
		 @Override
		 public void onReceive(Context context, Intent intent) {
			 	String action = intent.getAction();
				DLog.d(action);
				if(DeviceUtils.getNetworkType()==TYPE_NO_CONNECTION){
					NetworkManager.enable = false;
					DLog.d("No Network");
					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
					return;
				}
				
				if(DeviceUtils.getNetworkType()==TYPE_WIFI){
					wifiAction(intent, action);
				}else {
					wapAction(intent, action);
				}
				
		 }

		public void wapAction(Intent intent, String action) {
			if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			    ConnectivityManager cm = (ConnectivityManager) OotbConfig.app().getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			    //如果是在开启wifi连接和有网络状态下
			    if(NetworkInfo.State.CONNECTED==info.getState()){
			        //连接状态
					DLog.e("Network connection");
			        NetworkManager.enable = true;
			        for(NetworkListener lis:listeners){
						lis.onNetworkConnected(DeviceUtils.getNetworkType());
					}
			    }else{
					DLog.e("No network connection");
			        NetworkManager.enable = false;
			        
					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
			    }
			}
		}

		public void wifiAction(Intent intent, String action) {
			if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
				NetworkInfo info =intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if(info!=null){
					DLog.v(info.getDetailedState().toString());
					if(info.getDetailedState()== DetailedState.CONNECTED){
						NetworkManager.enable = true;
						DLog.d("Network connected");
						
						//获取更新--网络断开后没有到达的消息通过这个方法获取
						
						//开启推送服务
						
						for(NetworkListener lis:listeners){
							lis.onNetworkConnected(DeviceUtils.getNetworkType());
						}
					}
				}
			}else if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
				DetailedState state = WifiInfo.getDetailedStateOf((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
				DLog.i(state.toString());
				if(state== DetailedState.DISCONNECTED){//切换网络
					NetworkManager.enable = false;
					DLog.d("Network disconnection");
					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
				}
			}else if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){//开关wifi
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				if(wifiState== WifiManager.WIFI_STATE_DISABLED){
					NetworkManager.enable = false;
					DLog.d("Network close");
					//发送通知给activity 显示toast提示
					for(NetworkListener lis:listeners){
						lis.onNetworkDisconnect();
					}
				}
			}
		}
		 
	}

	/**
	 * 获取当前的网络状态
	 * android.permission.ACCESS_WIFI_STATE
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		//设置默认网路类型
		int netType = TYPE_NO_CONNECTION;
		//获取当前的网络管理器
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//获取网络信息
		@SuppressLint("MissingPermission") NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		//得到网络类型
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			netType = networkInfo.getExtraInfo().toLowerCase().equals("cmnet") ? TYPE_CMNET : TYPE_CMWAP;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = TYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断WiFi网络是否可用
	 * android.permission.ACCESS_WIFI_STATE
	 * @param context
	 * @return
	 */
	public static boolean isWifiConn(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			@SuppressLint("MissingPermission") NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断数据流量是否可用
	 * android.permission.ACCESS_WIFI_STATE
	 * @param context
	 * @return
	 */
	public static boolean isMobileConn(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			@SuppressLint("MissingPermission") NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断是否有网络
	 * android.permission.ACCESS_WIFI_STATE
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConn(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			@SuppressLint("MissingPermission") NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
