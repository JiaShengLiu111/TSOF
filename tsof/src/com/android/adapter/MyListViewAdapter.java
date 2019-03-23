package com.android.adapter;

import java.util.List;
import com.android.data.Cache;
import com.android.tab.R;
import com.lidroid.xutils.BitmapUtils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyListViewAdapter extends BaseAdapter {

	private List<Food> foodlist;
	private Context context;
	private LayoutInflater layoutInflater; 

	public MyListViewAdapter(List<Food> foodlist) {
		this.foodlist = foodlist;
	}

	public MyListViewAdapter(Context context, List<Food> foodlist) {
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
			public TextView food_step_num;
		}
		ViewHolder viewholder = null;
		if (arg1 == null) {
			viewholder = new ViewHolder();
			arg1 = layoutInflater.inflate(R.layout.listitem, null);
			viewholder.food_pic = (ImageView) arg1.findViewById(R.id.image);
			viewholder.food_title = (TextView) arg1.findViewById(R.id.title);
			viewholder.food_step_num = (TextView) arg1
					.findViewById(R.id.step_num);
			arg1.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) arg1.getTag();
		}
		viewholder.food_step_num.setText(foodlist.get(arg0).getFood_step_num());
		viewholder.food_title.setText(foodlist.get(arg0).getFood_title());
		
		if(foodlist.get(arg0).getFood_pic()==""){  //表示用户设置了无图模式
			viewholder.food_pic.setBackgroundResource(R.drawable.no_pic_1);
		}else{  //表示用户没有设置无图模式
			bmu.display(viewholder.food_pic,
					((Food) foodlist.get(arg0)).getFood_pic());
		} 

		// 给列表项里面的组件设置监听事件
		ImageView collect_iron = (ImageView) arg1
				.findViewById(R.id.collect_iron);

		/*
		 * if(context.toString()=="MMenu_food.this"){
		 * collect_iron.setVisibility(View.Gone); }
		 */
		collect_iron.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg1) {

			//	int flag = addCollection(foodlist.get(arg0).getFood_id());
				int flag=Cache.addCollection(foodlist.get(arg0).getFood_id());
				if (flag == 1) { 
					Toast.makeText(context,
							foodlist.get(arg0).getFood_title() + "收藏成功",
							Toast.LENGTH_SHORT).show();
				} else if (flag == 2) {
					Toast.makeText(context,
							foodlist.get(arg0).getFood_title() + "已收藏",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context,
							foodlist.get(arg0).getFood_title() + "收藏失败",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		return arg1;
	}
}
