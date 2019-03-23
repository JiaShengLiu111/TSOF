package com.android.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tab.R;
import com.lidroid.xutils.BitmapUtils;

public class Mainactivity_GridViewAdapter extends BaseAdapter {

	private List<Food> foodlist;
	private Context context;
	private LayoutInflater layoutInflater;

	// 鏋勯�鍑芥暟
	public Mainactivity_GridViewAdapter(List<Food> foodlist) {
		this.foodlist = foodlist;
	}

	// 鏋勯�鍑芥暟
	public Mainactivity_GridViewAdapter(Context context, List<Food> foodlist) {
		this.context = context;
		this.foodlist = foodlist;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return foodlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return foodlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {

		BitmapUtils bmu = new BitmapUtils(context);
		class ViewHolder {
			public ImageView food_pic;
			public TextView food_title;
		}
		ViewHolder viewholder = null;
		if (arg1 == null) {
			viewholder = new ViewHolder();

			arg1 = layoutInflater.inflate(R.layout.mainpage_gridview_item,
					arg2, false);

			// arg1 =
			// layoutInflater.inflate(R.layout.manager_group_list_item_parent,
			// null);
			viewholder.food_pic = (ImageView) arg1.findViewById(R.id.image);
			viewholder.food_title = (TextView) arg1.findViewById(R.id.title);
			arg1.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) arg1.getTag();
		}
		viewholder.food_title.setText(foodlist.get(arg0).getFood_title());
		if (foodlist.get(arg0).getFood_pic() == "") { // 表示用户设置了无图模式
			viewholder.food_pic.setBackgroundResource(R.drawable.no_pic_1);
		} else { // 表示用户没有设置无图模式
			bmu.display(viewholder.food_pic,
					((Food) foodlist.get(arg0)).getFood_pic());
		}
		return arg1;
	}
}
