package com.android.tab;

import java.util.ArrayList;
import java.util.List;

import com.android.adapter.Food;
import com.android.adapter.Mainactivity_GridViewAdapter;
import com.android.data.Cache;
import com.android.data.MyDbOpenHelper;
import com.android.data.MyDbOperator;
import com.android.tab.R;
import com.android.tab.Search.new_food;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {

	private GridView gridview = null;
	private List list = new ArrayList<Food>(); // 定义推荐的美食列表
	private Cursor result = null;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.mainpage);

		Cache.init(this);

		ArrayList<new_food> food_info = new ArrayList<new_food>();
		MyDbOperator mDbOperator = new MyDbOperator(new MyDbOpenHelper(
				Cache.context).getReadableDatabase());
		result = mDbOperator.rawQueryByBlank(); // 获取的数据

		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			Food food = new Food();
			food.setFood_id(result.getInt(0) + "");
			food.setFood_title(result.getString(3));
			String as = change_format(result.getString(7));
			food.setFood_pic(as);
			food.setFood_step_num(result.getString(8) + "步");
			list.add(food);
		}
		mDbOperator.close();

		Mainactivity_GridViewAdapter myadapter = new Mainactivity_GridViewAdapter(
				MainActivity.this, list);
		gridview = (GridView) this.findViewById(R.id.gridview);
		gridview.setAdapter(myadapter);
		gridview.setOnItemClickListener(this);

	}

	// 将网络图片的地址转化为可以显示的形式
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
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Cache.writeCollection();
			finish();
			System.exit(0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int flag = 0;
		String id=null;
		String title = null;
		String ingredients = null;
		String burden = null;
		String tags = null;
		String albums = null;
		String imtro = null;
		String step = null;
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			if (flag == arg2) { // 将匹配的食物信息传递到MMenu_foodshow中去
				id=result.getString(0);
				title = result.getString(3);// title
				ingredients = result.getString(5);// ingredients
				burden = result.getString(6);// burden;
				tags = result.getString(4);// tags;
				albums = result.getString(7);// albums;
				imtro = result.getString(9);// imtro;
				step = result.getString(10);// step;JOSNArray;
				break;
			}
			flag++;
		}
		Intent intent = new Intent(MainActivity.this, Menu_foodshow.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("id", id);
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
