package com.android.tab;

import com.android.data.Cache;
import com.android.data.MyDbOpenHelper;
import com.android.data.MyDbOperator;
import com.android.tab.R;
import com.android.zhy_slidingmenu.SlidingMenu;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class TabBarExample extends TabActivity implements OnClickListener {

	private SlidingMenu mMenu; // 侧滑界面
	private TextView clear_cache;

	// 点击两次返回键退出app
	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	private int myMenuRes[] = { R.drawable.tab1, R.drawable.tab2, R.drawable.tab3, R.drawable.tab4 };

	TabHost tabHost;
	TabSpec firstTabSpec;
	TabSpec secondTabSpec;
	TabSpec threeTabSpec;
	TabSpec fourTabSpec;

	// ////////////////////////////////////////////////
	private WindowManager mWindowManager;
	private View view;
	private CheckBox cb_night;
	private CheckBox no_picture;
	private TextView more_food; // 更多美食
	private TextView mores_help; // 帮助
	private TextView others_about;
	private TextView bgTextView; // 设置夜间模式

	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab);
		mMenu = (SlidingMenu) findViewById(R.id.id_menu);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		firstTabSpec = tabHost.newTabSpec("tid1");
		secondTabSpec = tabHost.newTabSpec("tid2");
		threeTabSpec = tabHost.newTabSpec("tid3");
		fourTabSpec = tabHost.newTabSpec("tid4");
		firstTabSpec.setIndicator(getResources().getText(R.string.home_page), getResources().getDrawable(myMenuRes[0]));
		secondTabSpec.setIndicator(getResources().getText(R.string.menu), getResources().getDrawable(myMenuRes[1]));
		threeTabSpec.setIndicator(getResources().getText(R.string.search), getResources().getDrawable(myMenuRes[2]));
		fourTabSpec.setIndicator(getResources().getText(R.string.collect), getResources().getDrawable(myMenuRes[3]));
		firstTabSpec.setContent(new Intent(this, MainActivity.class));
		secondTabSpec.setContent(new Intent(this, Menu.class));
		threeTabSpec.setContent(new Intent(this, Search.class));
		fourTabSpec.setContent(new Intent(this, Collect.class));
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
		tabHost.addTab(threeTabSpec);
		tabHost.addTab(fourTabSpec);

		// ////////////////////////////////////////////////
		more_food = (TextView) mMenu.findViewById(R.id.more_food);
		more_food.setOnClickListener(this);
		mores_help = (TextView) mMenu.findViewById(R.id.others_help);
		mores_help.setOnClickListener(this);
		others_about = (TextView) mMenu.findViewById(R.id.others_about);
		others_about.setOnClickListener(this);
		// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		cb_night = (CheckBox) this.findViewById(R.id.checkBox2);

		// 得到一个对象
		SharedPreferences setting = getSharedPreferences("mypf", 0);

		String night = setting.getString("night", "");
		if (night == "" || night.equals("false")) { // 如果首次使用app或者设置记录为日间模式，使用日间模式
			Day(view); // 设置日间模式
			cb_night.setChecked(false); // 复选框不选中
		} else if (night.equals("true")) { // 如果设置记录为夜间模式，使用夜间模式
			Night(view); // 设置夜间模式
			cb_night.setChecked(true); // 复选框选中
		}

		// 为夜间模式复选框设置监听事件
		cb_night.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			// 得到一个对象
			SharedPreferences setting = getSharedPreferences("mypf", 0);
			// 得到对象的编辑器
			SharedPreferences.Editor edit = setting.edit();

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// 将选中的结果写入文件之中
				if (arg1 == true) {
					Night(view); // 设置夜间模式
					edit.putString("night", "true");
					edit.commit();

				} else {
					Day(view); // 设置日间模式
					edit.putString("night", "false");
					edit.commit();

				}
			}
		});

		// 设置无图模式
		no_picture = (CheckBox) this.findViewById(R.id.checkBox1);
		// 得到一个对象
		setting = getSharedPreferences("mypf", 0);

		String no_pic = setting.getString("no_pic", "");
		if (no_pic == "" || no_pic.equals("false")) { // 如果首次使用app或者设置记录为有图模式，使用有图模式
			no_picture.setChecked(false); // 复选框不选中
		} else if (no_pic.equals("true")) { // 如果设置记录为无图模式，使用无图模式
			no_picture.setChecked(true); // 复选框选中
		}

		// 为无图复选框设置监听事件
		no_picture.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			SharedPreferences setting = getSharedPreferences("mypf", 0); // 取得存储文件的一个对象
			// 得到对象的编辑器
			SharedPreferences.Editor edit = setting.edit();

			// String no_pic = setting.getString("no_pic", "");
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				// 设置刷新收藏标志
				Cache.isDirty = true;

				// 将选中的结果写入文件之中
				if (arg1 == true) {
					edit.putString("no_pic", "true");
					edit.commit();

				} else {
					edit.putString("no_pic", "false");
					edit.commit();

				}
			}
		});

		clear_cache = (TextView) this.findViewById(R.id.clear_cache);
		clear_cache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { 
				
				Dialog dialog = new AlertDialog.Builder(TabBarExample.this).setTitle("清除缓存").setIcon(R.drawable.appiron)
						.setMessage("确认清除缓存吗？")
						// 相当于点击确认按钮
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MyDbOperator mDboperator=new MyDbOperator(new MyDbOpenHelper(TabBarExample.this).getWritableDatabase());
						mDboperator.clear();
						mDboperator.close();

						Toast.makeText(TabBarExample.this, "缓存已清除", Toast.LENGTH_SHORT).show();
					}
				})
						// 相当于点击取消按钮
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// 取消不做任何操作
					}
				}).create();
				dialog.show();

			}
		});
	}

	// 夜间模式
	public void Night(View view) {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置
		lp.y = 10;// 距离底部的距离是10像素 如果是 top 就是距离top是10像素
		if (bgTextView == null) {
			bgTextView = new TextView(this);
			bgTextView.setBackgroundColor(0x99000000);
			mWindowManager.addView(bgTextView, lp);
		} else {
			bgTextView.setBackgroundColor(0x99000000);
		}
	}

	public void Day(View view) {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置
		lp.y = 10;// 距离底部的距离是10像素 如果是 top 就是距离top是10像素
		if (bgTextView == null) {
			bgTextView = new TextView(this);
			bgTextView.setBackgroundColor(0x00000000);
			mWindowManager.addView(bgTextView, lp);
		} else {
			bgTextView.setBackgroundColor(0x00000000);
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.more_food:
			Intent intent1 = new Intent(TabBarExample.this, more_food.class);
			startActivity(intent1);
			break;
		case R.id.others_about:
			Intent intent2 = new Intent(TabBarExample.this, About.class);
			startActivity(intent2);
			break;
		case R.id.others_help:
			Intent intent3 = new Intent(TabBarExample.this, Help.class);
			startActivity(intent3);
			break;
		}
	}

	// 点击两次返回键退出该app
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

}








