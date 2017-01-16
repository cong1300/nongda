package com.xinnuo.apple.nongda;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 高精度定位模式功能演示
 *
 * @创建时间： 2015年11月24日 下午5:22:42
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: Hight_Accuracy_Activity.java
 * @类型名称: Hight_Accuracy_Activity
 */
public class Location_Activity extends Activity implements View.OnClickListener {
	private RadioGroup rgLocationMode;
	private EditText etInterval;
	private EditText etHttpTimeout;
	private CheckBox cbOnceLocation;
	private CheckBox cbAddress;
	private CheckBox cbGpsFirst;
	private CheckBox cbCacheAble;
	private CheckBox cbOnceLastest;
	private CheckBox cbSensorAble;
	private TextView statuTV;
	private Button btLocation;
	private static double signLongitude;
	private static double signLatitude;
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();
	private static final double ALERT_DISTANCE = 50;
	private OkHttpClient client;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		btLocation = (Button) findViewById(R.id.bt_location);
		statuTV = (TextView) findViewById(R.id.textView);
		btLocation.setOnClickListener(this);
		//初始化定位
		initLocation();

	}
	

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyLocation();
	}



	/**
	 * 设置控件的可用状态
	 */
	private void setViewEnable(boolean isEnable) {
		for(int i=0; i<rgLocationMode.getChildCount(); i++){
			rgLocationMode.getChildAt(i).setEnabled(isEnable);
		}
		etInterval.setEnabled(isEnable);
		etHttpTimeout.setEnabled(isEnable);
		cbOnceLocation.setEnabled(isEnable);
		cbGpsFirst.setEnabled(isEnable);
		cbAddress.setEnabled(isEnable);
		cbCacheAble.setEnabled(isEnable);
		cbOnceLastest.setEnabled(isEnable);
		cbSensorAble.setEnabled(isEnable);
	}




	
	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation(){
		//初始化client
		locationClient = new AMapLocationClient(this.getApplicationContext());
		//设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
	}
	
	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		//mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
		return mOption;
	}
	
	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
				//解析定位结果
				String result = Utils.getLocationStr(loc);
				/**
				 * sb.append("经    度    : " + location.getLongitude() + "\n");
				 sb.append("纬    度    : " + location.getLatitude() + "\n");
				 */
				double Longitude =  loc.getLongitude();
				double Latitude =  loc.getLatitude();

				String ret = ""+Longitude + Latitude;
				String jStr = Longitude+",";
				String wStr = Latitude+"";
                statuTV.setText("定位结果：long = "+Longitude+"lat = "+Latitude);
//                test3TV.setText("定位结果：long = "+Longitude+"lat = "+Latitude);

				double distance = DistanceOfTwoPoints(Latitude,Longitude,signLatitude,signLongitude);
//                test4TV.setText("计算结果"+distance);


//				if (distance > ALERT_DISTANCE)
//				{
//					statuTV.setText("签到失败。距离定位点"+distance+"米");
////                    test5TV.setText("超出距离");
//
//				}
//				else
//				{
//					//立即签到
//					signRequest();
////                    test5TV.setText("在距离内");
//
//				}
//
			} else {
				statuTV.setText("定位失败，loc is null");
			}
		}
	};
	


	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void startLocation(){

		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}
	
	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void stopLocation(){
		// 停止定位
		locationClient.stopLocation();
	}
	
	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void destroyLocation(){
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	@Override
	public void onClick(View v) {

		statuTV.setText("正在定位...");
		startLocation();
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，
	 *
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 距离：单位为米
	 */
	public static double DistanceOfTwoPoints(double lat1,double lng1,
											 double lat2,double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * 6378137.0;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private void signRequest(){
		RequestBody requestBodyPost = new FormBody.Builder()
				//.add("id", userId)
				.build();
		Request requestPost = new Request.Builder()
				//.url(makeSignSleep)
				.post(requestBodyPost)
				.build();
		client.newCall(requestPost).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

				//signSleepTextView.setText("网络异常！");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				final String retStr = response.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d("网络请求返回值",retStr);

						try {

							JSONObject jsObj = new JSONObject(retStr);


							String retStr = jsObj.getString("melodyClass");

							if (retStr.equals("yes")) {
								statuTV.setText("签到成功");

							}
							else if (retStr.equals("no"))
							{
								statuTV.setText("不是签到时间");

							}
							else if (retStr.equals("guiqin"))
							{
								statuTV.setText("已经签到过了");


							}else{
								statuTV.setText("服务器异常");


							}


							stopLocation();


						} catch (JSONException e) {
							e.printStackTrace();
							statuTV.setText("签到异常");

							stopLocation();

						}


					}
				});
			}
		});
	}
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}



}
