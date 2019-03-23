package com.android.adapter;

import java.util.List;

import com.android.tab.R;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Foodsteps_item_Adapter extends BaseAdapter {

	private Context context = null;
	private List<Step> steplist = null;
	private LayoutInflater layoutInflater = null;

	// 构造函数
	public Foodsteps_item_Adapter(List<Step> steplist) {
		this.steplist = steplist;
	}

	// 构造函数
	public Foodsteps_item_Adapter(Context context, List<Step> steplist) {
		this.steplist = steplist;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return steplist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return steplist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		BitmapUtils bmu = new BitmapUtils(context);
		class ViewHolder {
			public ImageView food_image;
			public TextView food_step;
		}
		ViewHolder vh = null;
		if (arg1 == null) {
			vh = new ViewHolder();
			arg1 = layoutInflater.inflate(R.layout.foodsteps_item, null);
			vh.food_image = (ImageView) arg1.findViewById(R.id.steps_image);
			vh.food_step = (TextView) arg1.findViewById(R.id.steps_step);
			arg1.setTag(vh);
		} else {
			vh = (ViewHolder) arg1.getTag();
		}
		vh.food_step.setText(steplist.get(arg0).getFood_step());   

		if (steplist.get(arg0).getFood_image() == "") { // 表示用户设置了无图模式
			vh.food_image.setBackgroundResource(R.drawable.no_pic_1);
		} else { // 表示用户没有设置无图模式
			bmu.display(vh.food_image,
					((Step) steplist.get(arg0)).getFood_image());
		}

		return arg1;
	}

}
