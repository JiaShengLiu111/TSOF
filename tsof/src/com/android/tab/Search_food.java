package com.android.tab;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.adapter.Food;
import com.android.adapter.MyListViewAdapter;
import com.android.tab.R;
import com.android.tab.Search.new_food;

public class Search_food extends Activity implements OnItemClickListener {

	private int position;
	private int cid;
	private ListView mylist = null;
	private List list = new ArrayList<Food>(); // 定义推荐的美食列表
	private TextView back = null;
	private Cursor result = null;
	private ProgressDialog dialog = null;
	private new_food new_demo = null;
	private List food_info = null;

	// 设置夜间模式
	private SharedPreferences setting;
	private String night;
	private WindowManager mWindowManager;
	private View view;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_food_list);
		back = (TextView) this.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		dialog = new ProgressDialog(Search_food.this); // 设置进度条风格，风格为圆形，旋转的
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
		dialog.setTitle("加载数据中");
		// 设置ProgressDialog 提示信息
		dialog.setMessage("正在加载，请稍等！");
		// 设置ProgressDialog 标题图标
		dialog.setIcon(android.R.drawable.ic_dialog_map);
		// 设置ProgressDialog 的进度条是否不明确
		dialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		dialog.setCancelable(false);
		dialog.show();// 显示

		Intent intent = getIntent();
		// Bundle bundle = intent.getExtras();
		food_info = intent.getParcelableArrayListExtra("food_info");

		for (int i = 0; i < food_info.size(); i++) {
			new_food newFood = (new_food) food_info.get(i);
			Food food = new Food();
			food.setFood_id(newFood.id);
			food.setFood_title(newFood.title);
			String as = change_format(newFood.albums);
			food.setFood_pic(as);
			food.setFood_step_num(newFood.step_num + "步");
			list.add(food);
		}

		// 读取文件中的数据，判断用户有没有设置无图模式
		SharedPreferences setting = getSharedPreferences("mypf", 0);
		String no_pic = setting.getString("no_pic", "");
		if (no_pic.equals("true")) { // 如果用户设置了无图模式，就执行下面的程序
			for (int i = 0; i < list.size(); i++) {
				((Food) list.get(i)).setFood_pic("");
			}
		}

		MyListViewAdapter myadapter = new MyListViewAdapter(Search_food.this,
				list);
		mylist = (ListView) this.findViewById(R.id.mylist);
		mylist.setAdapter(myadapter);
		dialog.dismiss();
		mylist.setOnItemClickListener(this);

		// 判断是否选择夜间模式，如果是，调为夜间模式
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		setting = getSharedPreferences("mypf", 0);
		night = setting.getString("night", "");
		if (night.equals("true")) {
			Night(view);
		}
	}

	// 夜间模式
	public void Night(View view) {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置
		lp.y = 10;// 距离底部的距离是10像素 如果是 top 就是距离top是10像素
		TextView textView = new TextView(this);
		textView.setBackgroundColor(0x99000000);
		mWindowManager.addView(textView, lp);
	}

	// 将网络上的图片格式转化为正常格式
	public String change_format(String food_pic) {
		int i = 0, j = 0;
		char[] register = food_pic.toCharArray();
		char[] re = new char[food_pic.length()];
		for (i = 0, j = 0; i < register.length; i++) {
			if (register[i] != '\\') {
				re[j] = register[i];
				j++;
			}
		}
		char reg[] = new char[j];
		for (i = 0; i < j; i++) {
			reg[i] = re[i];
		}
		String s = new String(reg);
		return s;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		new_food newFood = (new_food) food_info.get(position);
		String title = newFood.title;
		String ingredients = newFood.ingredients;
		String burden = newFood.burden;
		String tags = newFood.tags;
		String albums = newFood.albums;
		String imtro = newFood.imtro;
		String step = newFood.step;

		// 跳转到显示食物的activity
		Intent intent = new Intent(Search_food.this, Menu_foodshow.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("title", title);
		mBundle.putString("ingredients", ingredients);
		mBundle.putString("burden", burden);
		mBundle.putString("tags", tags);
		mBundle.putString("albums", albums);
		mBundle.putString("imtro", imtro);
		mBundle.putString("step", step);
		intent.putExtras(mBundle);
		startActivity(intent);
	}
}
