package com.android.tab;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.data.Cache;
import com.android.data.MyDbOpenHelper;
import com.android.data.MyDbOperator;
import com.android.tab.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Search extends Activity implements OnItemClickListener,
		OnClickListener {
	private static final String conditionOri = null;
	private LineGridView grid1 = null; // 定义GridView来显示pid项
	private ListView cidlist = null; // ListView来显示cid项
	private List listitems = new ArrayList<String>();
	private int pid_position; // 记录GridView被点中的位置
	private int cid_position; // 记录ListView被点中的位置
	private String[] PidName = null; // 记录食物pidname
	private int Pid[] = null; // 记录食物的pid
	private int cid[][] = null; // 记录食物的cid
	private int notes_cid[][] = null; // 用于记录哪些cid被选中
	private String cidname[][] = null; // 记录食物的cidname
	private String regester[] = null; // 用于记录被选中的cidname
	RadioGroup raGroup1 = null;
	private Button button5 = null; // 定义搜索按钮
	private EditText editText1 = null; // 定义搜索框

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
		setContentView(R.layout.search);
		editText1 = (EditText) this.findViewById(R.id.editText1);
		raGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		// 复选框的监听事件
		raGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.radiobutton1:

					break;
				case R.id.radiobutton2:

					break;
				case R.id.radiobutton3:

					break;
				case R.id.radiobutton4:

					break;

				default:
					break;
				}
			}
		});
		
		button5 = (Button) this.findViewById(R.id.button5);
		button5.setOnClickListener(this); // 为搜索按钮设置监听事件

		int pcid[] = new int[10000];
		String pcName[] = new String[500];
		regester = new String[pcName.length];
		for (int i = 0; i < regester.length; i++) {
			regester[i] = "";
		}
		int count = getId(pcid, pcName); // 获取cid的准确条目
		int n = 0; // 寰楀埌pid鐨勫噯纭暟鐩�
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
		notes_cid = new int[n][];
		PidName = new String[n];
		cidname = new String[n][];
		for (int i = 0; i < n; i++) {
			cid[i] = new int[x[i] + 1];
			notes_cid[i] = new int[x[i] + 1];
			cidname[i] = new String[x[i] + 1];
		}
		for (int i = 0; i < notes_cid.length; i++) { // 初始化记录哪个cid被选中的数组
			for (j = 0; j < notes_cid[i].length; j++) {
				notes_cid[i][j] = -1;
			}
		}
		int k = 0, h = 0;
		j = -1;
		for (int i = 0; i < count; i++) {
			if (pcid[i] <= 10000) {
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

		for (int i = 0; i < cid.length; i++) {
			int length = cid[i].length;
			cid[i][length - 1] = -1;
		}
		for (int i = 0; i < cidname.length; i++) {
			int length = cidname[i].length;
			cidname[i][length - 1] = getResources().getString(R.string.cancel);
		}
		grid1 = (LineGridView) this.findViewById(R.id.grid1);
		cidlist = (ListView) this.findViewById(R.id.cidlist);
		for (int i = 0; i < PidName.length; i++) {
			Map<String, Object> listitem = new HashMap<String, Object>();

			listitem.put("text", PidName[i]);
			listitem.put("nei", regester[i]);
			listitems.add(listitem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listitems,
				R.layout.gridcell, new String[] { "nei", "text" }, new int[] {
						R.id.textView2, R.id.textView1 });
		grid1.setAdapter(simpleAdapter);
		grid1.setOnItemClickListener(this);
	}

	protected int getId(int[] pcid, String[] pcName) // 读文件获取标签数据
	{
		InputStream input = null;
		Scanner scan = null;
		StringBuffer strBuf = new StringBuffer();
		try {
			input = Search.this.getAssets().open("cids_forSearchReference.txt");
			scan = new Scanner(input);
			while (scan.hasNext()) {
				strBuf.append(scan.next().toString());
			}
		} catch (IOException e) {
			Toast.makeText(Search.this, "int getId(int []pcid)",
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

	// 将记录哪个cid被选中的二维数组转化用一维数组来记录
	int[] change_cid() {
		int num = 0;
		int note_cid[] = null;
		for (int i = 0; i < notes_cid.length; i++) {
			for (int j = 0; j < notes_cid[i].length; j++) {
				if (notes_cid[i][j] != -1) {
					num++;
				}
			}
		}
		int k = 0;
		note_cid = new int[num];
		for (int i = 0; i < notes_cid.length; i++) {
			for (int j = 0; j < notes_cid[i].length; j++) {
				if (notes_cid[i][j] != -1) {
					note_cid[k] = notes_cid[i][j];
					k++;
				}
			}
		}
		return note_cid;
	}

	// GridView点击事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.grid1:
			grid1.setVisibility(View.GONE);
			cidlist.setVisibility(View.VISIBLE);
			pid_position = arg2;
			grid1.getAnimation();

			List listitems1 = new ArrayList<String>();
			for (int i = 0; i < cidname[pid_position].length; i++) {
				Map<String, Object> listitem = new HashMap<String, Object>();
				listitem.put("text", cidname[pid_position][i]);
				listitems1.add(listitem);
			}
			SimpleAdapter simpleAdapter1 = new SimpleAdapter(this, listitems1,
					R.layout.gridlist_cell, new String[] { "text" },
					new int[] { R.id.textView1 });
			cidlist.setAdapter(simpleAdapter1);
			cidlist.setOnItemClickListener(this);
			break;
		case R.id.cidlist:
			grid1.setVisibility(View.VISIBLE);
			cidlist.setVisibility(View.GONE);
			cid_position = arg2;
			regester[pid_position] = cidname[pid_position][cid_position];
			notes_cid[pid_position][cid_position] = cid[pid_position][cid_position];
			int x = notes_cid[pid_position][cid_position];
			int y = x;
			List listitems2 = new ArrayList<String>();
			for (int i = 0; i < PidName.length; i++) {
				Map<String, Object> listitem = new HashMap<String, Object>();
				if (regester[i] == getResources().getString(R.string.cancel)) {
					regester[i] = "";
				}
				listitem.put("nei", regester[i]);
				listitem.put("text", PidName[i]);
				listitems2.add(listitem);
			}
			SimpleAdapter simpleAdapter3 = new SimpleAdapter(this, listitems2,
					R.layout.gridcell, new String[] { "nei", "text" },
					new int[] { R.id.textView2, R.id.textView1 });
			grid1.setAdapter(simpleAdapter3);
			break;
		default:
			break;
		}

	}

	
	//function：搜索按钮事件；
	//根据标签进行数据库的搜索；
	//返回result；
	@Override
	public void onClick(View arg0) {
		SQLiteDatabase mDb = null;
		MyDbOpenHelper mDbOpenHelper = null;
		MyDbOperator mDbOperator = new MyDbOperator(
				new MyDbOpenHelper(this).getReadableDatabase());
		// 1.获取标签cids
		int cid[] = change_cid();
		// 2.获取输入条件字符串
		String conditionOri = null;
		conditionOri = editText1.getText().toString();

		String condition[] = null;
		if (conditionOri.length() != 0) {
			condition = conditionOri.split("[,\\.\\。， ]", 0);
		}
		
		Cursor result = null;
		// 输入框为空；
		if ((conditionOri.length() == 0 || condition.length == 0)) { // 并且标签也为空；
			if (cid.length == 0) {
				result = mDbOperator.rawQueryByBlank();
			}
			// 标签不为空；
			else {
				result = mDbOperator.rawQueryByNewCids(cid);
			}
		}
		// 输入框不为空；
		else {
			// 3.获取输入条件类型
			// type:0：什么都没选；1,tags,属性;2:taste,3:burden,4:title;5.ingredients
			int type = 0;
			switch (raGroup1.getCheckedRadioButtonId()) {
			case R.id.radiobutton1: {
				type = 4;
				break;
			}
			case R.id.radiobutton2: {
				type = 5;
				break;
			}
			case R.id.radiobutton3: {
				type = 3;
				break;
			}
			case R.id.radiobutton4: {
				type = 2;
				break;
			}
			default: {
				type = 0;
			}
			}
			// 标签为空
			if (cid.length == 0) {
				result = mDbOperator.rawQueryByTags(condition, type);
			}
			// 标签不为空
			else {
				result = mDbOperator.rawQueryByNewCidsAndTags(condition, type,
						cid);
			}
		}

		ArrayList<new_food> food_info = new ArrayList<new_food>();
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			new_food demo = new new_food();
			demo.id = result.getInt(0) + "";// id
			demo.title = result.getString(3);// title
			demo.ingredients = result.getString(5);// ingredients
			demo.burden = result.getString(6);// burden;
			demo.tags = result.getString(4);// tags;
			demo.albums = result.getString(7);// albums;
			demo.step_num = result.getString(8);
			demo.imtro = result.getString(9);// imtro;
			demo.step = result.getString(10);// step;JOSNArray;
			food_info.add(demo);
		}
		if (mDbOperator != null)
			mDbOperator.close();

		Intent intent = new Intent(Search.this, Search_food.class);
		// Bundle mBundle = new Bundle();
		// mBundle.putSerializable("food_info", (Serializable) food_info);
		// intent.putExtras(mBundle);
		intent.putParcelableArrayListExtra("food_info", food_info);
		// intent.putIntegerArrayListExtra("food_info",food_info);
		startActivity(intent);
	}

	public static class new_food implements Parcelable {
		public String id = null;
		public String title = null;
		public String ingredients = null;
		public String burden = null;
		public String tags = null;
		public String albums = null;
		public String step_num = null;
		public String imtro = null;
		public String step = null;

		public new_food() {
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeString(title);
			out.writeString(ingredients);
			out.writeString(burden);
			out.writeString(tags);
			out.writeString(albums);
			out.writeString(step_num);
			out.writeString(imtro);
			out.writeString(step);
			out.writeString(id);
		}

		public static final Parcelable.Creator<new_food> CREATOR = new Creator<new_food>() {
			@Override
			public new_food[] newArray(int size) {

				return new new_food[size];
			}

			@Override
			public new_food createFromParcel(Parcel in) {
				new_food newFood = new new_food();
				newFood.title = in.readString();
				newFood.ingredients = in.readString();
				newFood.burden = in.readString();
				newFood.tags = in.readString();
				newFood.albums = in.readString();
				newFood.step_num = in.readString();
				newFood.imtro = in.readString();
				newFood.step = in.readString();
				newFood.id = in.readString();
				return newFood;
			}
		};
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
