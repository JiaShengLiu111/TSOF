package com.android.adapter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.data.Cache;
import com.android.tab.R;
import com.lidroid.xutils.BitmapUtils;

public class Collect_ListViewAdapter extends BaseAdapter {

	private List<Food> foodlist;
	private Context context;
	private LayoutInflater layoutInflater;

	// 鏋勯�鍑芥暟
	public Collect_ListViewAdapter(List<Food> foodlist) {
		this.foodlist = foodlist;
		// //notifyDataSetChanged();
	}

	// 鏋勯�鍑芥暟
	public Collect_ListViewAdapter(Context context, List<Food> foodlist) {
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

			arg1 = layoutInflater.inflate(R.layout.collect_listitem, arg2,
					false);

			// arg1 =
			// layoutInflater.inflate(R.layout.manager_group_list_item_parent,
			// null);
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

		if (foodlist.get(arg0).getFood_pic() == "") { // 表示用户设置了无图模式
			viewholder.food_pic.setBackgroundResource(R.drawable.no_pic_1);
		} else { // 表示用户没有设置无图模式
			bmu.display(viewholder.food_pic,
					((Food) foodlist.get(arg0)).getFood_pic());
		}

		// 给列表项里面的组件设置监听事件
		ImageView collect_iron = (ImageView) arg1
				.findViewById(R.id.collect_iron);

		collect_iron.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg1) {

				Dialog dialog = new AlertDialog.Builder(context)
						.setTitle("删除"+foodlist.get (arg0).getFood_title())
						.setIcon(R.drawable.appiron)
						.setMessage("确认删除吗？")
						// 相当于点击确认按钮
						.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) { 
										String position = foodlist.get (arg0).getFood_id();
										String title = foodlist.get (arg0).getFood_title();
										foodlist.remove(foodlist.get(arg0).getPosition()); // 将数据从列表中删除

										// 更新列表数据
										for (int i = 0; i < foodlist.size(); i++) {
											foodlist.get(i).setPosition(i);
										} 
										notifyDataSetChanged(); 
										Cache.deleteCollection(position);
										Cache.isDirty = false;

										Toast.makeText(context, title + "删除成功", Toast.LENGTH_SHORT).show();
									}
								})
						// 相当于点击取消按钮
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										// 取消不做任何操作
									}
								}).create();
				dialog.show();
			}  
		});
		return arg1;
	}
}
