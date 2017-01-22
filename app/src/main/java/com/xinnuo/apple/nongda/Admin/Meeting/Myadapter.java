package com.xinnuo.apple.nongda.Admin.Meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.xinnuo.apple.nongda.R;

import java.util.List;

public class Myadapter extends BaseAdapter {
	private List<String> arraylist;
	private List<String> arraylist1;
	private List<String> arraylist2;
	private List<String> arraylist3;
	private Context context;
	private boolean[] array;
	
	public Myadapter(Context context, List<String> arraylist,List<String> arraylist1,List<String> arraylist2,List<String> arraylist3, boolean[] array ){
		this.context = context;
		this.arraylist = arraylist;
		this.arraylist1 = arraylist1;
		this.arraylist2 = arraylist2;
		this.arraylist3 = arraylist3;
		this.array = array;
		
	}
	@Override
	public View getView(final int position, View convertView,
						ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh  = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_query_all_teacher, null);

			vh.tv = (TextView) convertView.findViewById(R.id.admin_number);
			vh.tv1 = (TextView) convertView.findViewById(R.id.admin_name);
			vh.tv2 = (TextView) convertView.findViewById(R.id.admin_subject);
			vh.tv3 = (TextView) convertView.findViewById(R.id.admin_phone);
			vh.cb = (CheckBox) convertView.findViewById(R.id.checkBox);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}	
		if(AdminQueryAllteacherActivity.visiblecheck){
			vh.cb.setVisibility(View.VISIBLE);
		}else{
			vh.cb.setVisibility(View.GONE);
		}
		
		vh.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				array[position] = isChecked;
			}
		});
		vh.tv.setText(arraylist.get(position));
		vh.tv1.setText(arraylist1.get(position));
		vh.tv2.setText(arraylist2.get(position));
		vh.tv3.setText(arraylist3.get(position));
		vh.cb.setChecked(array[position]);
		return convertView;
	}

	class ViewHolder {
		
		
		public TextView tv;
		public TextView tv1;
		public TextView tv2;
		public TextView tv3;
		public CheckBox cb;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arraylist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arraylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

}
