package com.android.tab;

import java.util.ArrayList;
import java.util.List;

import com.android.adapter.MenulistAdapter;
import com.android.data.MyDbOpenHelper;
import com.android.data.MyDbOperator;
import com.android.tab.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Menu_pid_foodlist extends Activity implements
		OnItemClickListener, OnClickListener {
	private List list = new ArrayList<String>();
	private TextView back = null; // 返回键
	private ListView mylist = null; // 显示列表
	private String cidname_cast[] = null; // 存储食物的pid
	private int cid_cast[] = null; // 存储食物的cid
	private ProgressDialog dialog = null; // 定义进度条

	// 设置夜间模式
	private SharedPreferences setting;
	private String night;
	private WindowManager mWindowManager;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_food_list);
		
		
		dialog = new ProgressDialog(Menu_pid_foodlist.this); // 设置进度条风格，风格为圆形，旋转的
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
		Bundle bundle = intent.getExtras();
		// 从上一个activity中传来的数据
		cidname_cast = (String[]) bundle.getSerializable("cidname_cast");
		cid_cast = (int[]) bundle.getSerializable("cid_cast");

		for (int i = 0; i < cidname_cast.length; i++) {
			list.add(cidname_cast[i]);
		}

		MenulistAdapter myadapter = new MenulistAdapter(
				Menu_pid_foodlist.this, list);
		mylist = (ListView) this.findViewById(R.id.mylist);
		mylist.setAdapter(myadapter);
		dialog.dismiss();
		mylist.setOnItemClickListener(this);
		back = (TextView) this.findViewById(R.id.back);
		back.setOnClickListener(this);

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

	// 转为网络图片的地址
	public String change_format(String food_pic) {
		int i = 0, j = 0;
		char[] register = food_pic.toCharArray();
		char[] re = new char[food_pic.length()];
		for (i = 2, j = 0; i < register.length - 2; i++) {
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
	public void onClick(View arg0) {
		finish();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(Menu_pid_foodlist.this,
				Menu_cid_food.class);
		Bundle mBundle = new Bundle();
		mBundle.putInt("position", position);
		mBundle.putInt("cid", cid_cast[position]);
		intent.putExtras(mBundle);
		startActivity(intent);
	}

}
