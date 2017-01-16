package com.xinnuo.apple.nongda.studentActivity.TeacherShow;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xinnuo.apple.nongda.R;
import com.xinnuo.apple.nongda.httpUrl;
import com.xinnuo.apple.nongda.studentActivity.StuClub.StuClubActivity;
import com.xinnuo.apple.nongda.studentActivity.StudentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherShowActivity extends BaseActivity {
	private OkHttpClient client;
	private ListView mListImageLv;
	private DisplayImageOptions options; // 设置图片显示相关参数
	private String[] imageUrls; // 图片路径
	private ArrayList <Map>list = new ArrayList();
	@Override
	public int initResource() {
		return R.layout.activity_list;
	}

	@Override
	public void initComponent() {
		mListImageLv = (ListView) findViewById(R.id.lv_image);
	}

	@Override
	public void initData() {

 		client = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.build();


		Request requestPost = new Request.Builder()
				.url(httpUrl.TeacherShow)
				.build();
		client.newCall(requestPost).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}
			//接收方法
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				//接收httpresponse返回的json数据
				final String retStr = response.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.d("网络请求返回值",retStr);

						try {
							//解析json数据
							jsonParseWithJsonStr(retStr);
						} catch (JSONException e) {
 							e.printStackTrace();
						}
					}
				});
			}
		});
//		Bundle bundle = getIntent().getExtras();
//		imageUrls = bundle.getStringArray(Constants.IMAGES);

	}
	/**
	 * 解析数据方法
	 * */
	//json解析方法

	private  void jsonParseWithJsonStr (String jsonStr) throws JSONException {

		JSONArray jsArr = new JSONArray(jsonStr);
		final ArrayList<String> imgList = new ArrayList<>();
		for (int i = 0; i < jsArr.length();i++){
			JSONObject jsonObject = jsArr.getJSONObject(i);
			String img = jsonObject.getString("tuPian");
			imgList.add(img);
			String name = jsonObject.getString("name");
			String SportsClassName = jsonObject.getString("SportsClassName");
			String score = jsonObject.getString("score");
			String jiangXiang = jsonObject.getString("jiangXiang");
			String intro = jsonObject.getString("intro");
			Map aMap = new HashMap();
			aMap.put("img",img);
			aMap.put("name",name);
			aMap.put("SportsClassName",SportsClassName);
			aMap.put("score",score);
			aMap.put("jiangXiang",jiangXiang);
			aMap.put("intro",intro);
			list.add(aMap);

		}
		String[] imgs = imgList.toArray(new String[imgList.size()]);
		imageUrls = imgs;

//		Log.d("imgList", "imgList: "+imgList);
//		Log.d("imageUrls", "imageUrls: "+imageUrls);
//		Log.d("imgs", "imgs: "+imgs);
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 构建完成
		mListImageLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(TeacherShowActivity.this,TeacherShowInfoActivity.class);
				intent.putExtra("img",list.get(i).get("img").toString());
//				intent.putExtra("img",imgList.get(i));
				intent.putExtra("name",list.get(i).get("name").toString());
				intent.putExtra("SportsClassName",list.get(i).get("SportsClassName").toString());
				intent.putExtra("score",list.get(i).get("score").toString());
				intent.putExtra("jiangXiang",list.get(i).get("jiangXiang").toString());
				intent.putExtra("intro",list.get(i).get("intro").toString());
 				startActivity(intent);
			}
		});
 		mListImageLv.setAdapter(new ItemListAdapter());

	}
	@Override
	public void addListener() {

	}

	class ItemListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {

			return imageUrls[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_list,
						null);
				viewHolder = new ViewHolder();
				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.iv_image);
				viewHolder.text1 = (TextView) convertView
						.findViewById(R.id.tv_1);
				viewHolder.text2 = (TextView) convertView
						.findViewById(R.id.tv_2);
				viewHolder.text3 = (TextView) convertView
						.findViewById(R.id.tv_3);
				viewHolder.text4 = (TextView) convertView
						.findViewById(R.id.tv_4);
				viewHolder.text5 = (TextView) convertView
						.findViewById(R.id.tv_5);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			/**
			 * imageUrl 图片的Url地址 imageView 承载图片的ImageView控件 options
			 * DisplayImageOptions配置文件
			 */
			imageLoader.displayImage(imageUrls[position],
					viewHolder.image, options);
			String name = list.get(position).get("name").toString();
			String SportsClassName = list.get(position).get("SportsClassName").toString();
			String score = list.get(position).get("score").toString();
			String jiangXiang = list.get(position).get("jiangXiang").toString();
			String intro = list.get(position).get("intro").toString();
			String star = "";
			double aScore;
			if (score.equals("nul")){
				aScore = 0;
			}else {
				aScore = Double.valueOf(score).doubleValue();

			}
			//☆★
			if (aScore == 0){
				star = "";
			}else if (aScore == 1){
				star = "★";

			}else if (aScore == 2){
				star = "★★";

			}else if (aScore == 3){
				star = "★★★";

			}else if (aScore == 4){
				star = "★★★★";

			}else if (aScore == 5){
				star = "★★★★★";

			}else if (aScore > 0 && aScore < 1){
				star = "☆";

			}else if (aScore > 1 && aScore < 2){
				star = "★☆";

			}else if (aScore > 2 && aScore < 3){
				star = "★★☆";

			}else if (aScore > 3 && aScore < 4){
				star = "★★★☆";

			}else if (aScore > 4 && aScore < 5){
				star = "★★★★☆";

			}

			viewHolder.text1.setText("教师:"+name); // TextView设置文本
			viewHolder.text2.setText("课程:"+SportsClassName); // TextView设置文本
			viewHolder.text3.setText("评价:"+star); // TextView设置文本
			viewHolder.text4.setText("个人奖项:"+jiangXiang); // TextView设置文本
			viewHolder.text5.setText("个人介绍:"+intro); // TextView设置文本


			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
			public TextView text1;
			public TextView text2;
			public TextView text3;
			public TextView text4;
			public TextView text5;
		}

	}

}
