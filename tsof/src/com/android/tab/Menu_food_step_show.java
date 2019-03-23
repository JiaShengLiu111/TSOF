package com.android.tab;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.adapter.Foodsteps_item_Adapter;
import com.android.adapter.Step;
import com.android.tab.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class Menu_food_step_show extends Activity {
	private ListView food_steps = null;
	private TextView back = null;

	// 设置夜间模式
	private SharedPreferences setting;
	private String night;
	private WindowManager mWindowManager;
	private View view;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_step_show);
		back = (TextView) this.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String step = bundle.getString("step", "");
		step = "{" + "\"" + "step" + "\"" + ":" + step + "}";
		List<Step> mmenu_food_steps = new ArrayList<Step>();
		JSONObject object;
		try {
			object = new JSONObject(step);
			JSONArray steps = object.getJSONArray("step");
			Step step_demo = null;
			for (int j = 0; j < steps.length(); j++) {
				step_demo = new Step(
						((JSONObject) steps.get(j)).getString("step"),
						((JSONObject) steps.get(j)).getString("img"));
				mmenu_food_steps.add(step_demo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<Step> steplist = new ArrayList<Step>();
		int setps_num = mmenu_food_steps.size();
		for (int i = 0; i < setps_num; i++) {
			Step step_demo = new Step();
			step_demo.setFood_step(mmenu_food_steps.get(i).getFood_step());
			String as = mmenu_food_steps.get(i).getFood_image();
			String reg = change_format(mmenu_food_steps.get(i).getFood_image());
			step_demo.setFood_image(as);
			steplist.add(step_demo);
		}
		// 读取文件中的数据，判断用户有没有设置无图模式
		SharedPreferences setting = getSharedPreferences("mypf", 0);
		String no_pic = setting.getString("no_pic", "");
		if (no_pic.equals("true")) { // 如果用户设置了无图模式，就执行下面的程序
			for (int i = 0; i < steplist.size(); i++) {
				((Step) steplist.get(i)).setFood_image("");
			}
		}

		Foodsteps_item_Adapter step_adapter = new Foodsteps_item_Adapter(
				Menu_food_step_show.this, steplist);
		food_steps = (ListView) this.findViewById(R.id.food_steps);
		food_steps.setAdapter(step_adapter);

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
}
