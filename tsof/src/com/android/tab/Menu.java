package com.android.tab;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.adapter.MenulistAdapter;
import com.android.data.Cache;
import com.android.tab.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Menu extends Activity implements OnItemClickListener {
	private int position; // 记录菜单项被点中的位置
	private ListView Menu = null; // 菜单列表
	private Myhandler myhandler = new Myhandler();

	private String[] PidName = null; // 要显示的菜单列表项内容
	int Pid[] = null; // 记录食物的pid
	int cid[][] = null; // 记录食物的cid
	private String cidname[][] = null; // 记录食物与cid所对应的名称

	List<String> meunn = new ArrayList<String>();
	private ProgressDialog dialog = null; // 进度条的显示
	private MenulistAdapter menulistadapter = null;

	// 点击两次返回键退出app
	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;
	private boolean isStop=false;
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
		setContentView(R.layout.mmenu);

		Menu = (ListView) this.findViewById(R.id.menu);

		Thread mthread = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				dialog = new ProgressDialog(Menu.this); // 设置进度条风格，风格为圆形，旋转的
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

				

				Message msg = myhandler.obtainMessage(); // 获取一个Message对象
				myhandler.sendMessage(msg); // 向handler传递数据

				Looper.loop();
			}
		});
		mthread.start();
		
		int pcid[] = new int[10000];
		String pcName[] = new String[500];
		int count = getId(pcid, pcName); // 获取cid的准确条目
		int n = 0; // 累加pid的数目
		for (int i = 0; i < count; i++) {
			if (pcid[i] > 10000)
				n++;
		}
		int j = 0, rege = 0;
		int x[] = new int[n]; // 计算每个pid下对应的cid条目数
		for (int i = 1; i < count; i++) {
			if (pcid[i] <= 10000) {
				rege++;
			} else {
				x[j] = rege;
				rege = 0;
				j++;
			}
		}
		x[j] = rege;
		Pid = new int[n];
		cid = new int[n][];
		PidName = new String[n];
		cidname = new String[n][];
		for (int i = 0; i < n; i++) {
			cid[i] = new int[x[i]];
			cidname[i] = new String[x[i]];
		}
		int k = 0, h = 0;
		j = -1;
		for (int i = 0; i < count; i++) {
			if (pcid[i] <= 10000) {

				int xb = pcid[i];
				String cc = pcName[i];

				int sd = cid[j].length;
				cid[j][k] = pcid[i];
				cidname[j][k] = pcName[i];
				k++;
			} else {
				Pid[h] = pcid[i];
				PidName[h] = pcName[i];
				h++;
				j++;
				k = 0;
			}
		}
		for (int i = 0; i < PidName.length; i++) {
			meunn.add(PidName[i]);
		}
		menulistadapter = new MenulistAdapter(Menu.this, meunn);
		Menu.setAdapter(menulistadapter);
		Menu.setOnItemClickListener(this);
		isStop=true;
		
	}

	class Myhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(isStop)
			{
				dialog.dismiss();
				isStop=true;
			}
		}
	}

	protected int getId(int[] pcid, String[] pcName) // 读文件获取标签数据
	{
		InputStream input = null;
		Scanner scan = null;
		StringBuffer strBuf = new StringBuffer();
		try {
			input = Menu.this.getAssets().open("cids.txt");
			scan = new Scanner(input);
			while (scan.hasNext()) {
				strBuf.append(scan.next().toString());
			}
		} catch (IOException e) {
			Toast.makeText(Menu.this, "int getId(int []pcid)",
					Toast.LENGTH_LONG).show();
			System.out.println("int getId(int []pcid)");
			e.printStackTrace();
		}
		String str2 = strBuf.toString().replaceAll(" ", "");
		JSONObject all_object = null;
		try {
			all_object = new JSONObject(str2);
		} catch (Exception err) {
			System.out.println("asdfasdfas");
			err.printStackTrace();
			System.exit(2);
		}

		JSONArray all_parent_Array = null;
		try {
			all_parent_Array = all_object.getJSONArray("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String[] parentId = new String[all_parent_Array.length()];
		JSONArray[] all_cid_array = new JSONArray[all_parent_Array.length()];
		JSONObject objectTemp = null;
		int cursor = 0;
		for (int i = 0; i < all_parent_Array.length(); i++) {
			try {
				objectTemp = all_parent_Array.getJSONObject(i);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			try {
				parentId[i] = objectTemp.getString("parentId");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			try {
				pcName[cursor] = objectTemp.getString("name");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			pcid[cursor++] = Integer.parseInt(parentId[i]);
			try {
				all_cid_array[i] = objectTemp.getJSONArray("list");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			for (int j = 0; j < all_cid_array[i].length(); j++) {
				try {
					objectTemp = all_cid_array[i].getJSONObject(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					pcName[cursor] = objectTemp.getString("name");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					pcid[cursor++] = Integer.parseInt(objectTemp
							.getString("id"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return cursor - 1;
	}

	// 转化网络图片的地址
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.position = arg2;
		// 跳转到显示cid列表的activity
		Intent intent = new Intent(Menu.this, Menu_pid_foodlist.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("cidname_cast", cidname[position]);
		mBundle.putSerializable("cid_cast", cid[position]);
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
