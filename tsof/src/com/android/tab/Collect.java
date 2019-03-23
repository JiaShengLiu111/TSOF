package com.android.tab;

import java.util.ArrayList;
import java.util.List;
import com.android.adapter.Collect_ListViewAdapter;
import com.android.adapter.Food;
import com.android.data.Cache;
import com.android.tab.R;
import com.android.tab.Search.new_food;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Collect extends Activity implements OnItemClickListener {
	
	private List<new_food> foodlist = null;
	private ListView mylist = null;
	List list =null;
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
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collect);
	}

	//每次进入此界面都需要执行；
	protected void onResume() {
		super.onResume();
		//如果结果集已经被改变，则需要重新读取，这种改变由初始化或者增加新的收藏引起；读取完数据之后，将脏位置为假；
		if(Cache.isDirty)
		{
			Cache.isDirty=false;
			list=new ArrayList<Food>();
			foodlist = Cache.getCollection();
			if (foodlist != null) {
				for (int i = 0; i < foodlist.size(); i++) {
					Food food = new Food();
					food.setFood_id(foodlist.get(i).id);
					food.setFood_title(foodlist.get(i).title);
					//food.setPosition(i);

					String as = change_format(foodlist.get(i).albums);
					food.setFood_pic(as);
					food.setFood_step_num(foodlist.get(i).step_num + "步");
					list.add(food);
				}
				//更新列表数据
				for(int i=0;i<list.size();i++){
					((Food) list.get(i)).setPosition(i);
				}
			}

			// 读取文件中的数据，判断用户有没有设置无图模式
			SharedPreferences setting = getSharedPreferences("mypf", 0);
			String no_pic = setting.getString("no_pic", "");
			if (no_pic.equals("true")) { // 如果用户设置了无图模式，就执行下面的程序
				for (int i = 0; i < list.size(); i++) {
					((Food) list.get(i)).setFood_pic("");
				}
			}

			Collect_ListViewAdapter myadapter = new Collect_ListViewAdapter(
					Collect.this, list);
			mylist = (ListView) this.findViewById(R.id.listView1);
			mylist.setAdapter(myadapter);
			mylist.setOnItemClickListener(this);
		}

	}

	//这里面onDestroy()不能被执行，，，用onStop代替。不过会导致写文件次数增多；
	/*protected void onStop() {
		Cache.writeCollection(); 
		super.onStop();
	}*/

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

	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		new_food newFood = (new_food) foodlist.get(position);
		String id=newFood.id;
		String title = newFood.title;
		String ingredients = newFood.ingredients;
		String burden = newFood.burden;
		String tags = newFood.tags;
		String albums = newFood.albums;
		String imtro = newFood.imtro;
		String step = newFood.step;

		Intent intent = new Intent(Collect.this, Menu_foodshow.class);
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

}
