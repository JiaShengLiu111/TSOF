package com.android.tab;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.android.data.MyDbOpenHelper;
import com.android.data.MyDbOperator;
import com.android.tab.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Menu_foodshow extends Activity implements OnClickListener {
	private TextView Title = null;
	private TextView M_material = null;
	private TextView A_material = null;
	private TextView F_tags = null;
	private TextView M_property = null;
	private ImageView Food_imge = null;
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAILURE = 1;
	private Myhandler myhandler = new Myhandler(); 
	private String id=null;
	private String title = null;
	private String ingredients = null;
	private String burden = null;
	private String tags = null;
	private String albums = null;
	private String imtro = null;
	private String step = null;
	private TextView back = null;
	private TextView Food_step = null;

	// 设置夜间模式
	private SharedPreferences setting;
	private String night;
	private WindowManager mWindowManager;
	private View view;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_show);
		back = (TextView) this.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		}); 

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getString("id", "");
		title = bundle.getString("title", "");
		ingredients = bundle.getString("ingredients", "");
		burden = bundle.getString("burden", "");
		tags = bundle.getString("tags", "");
		albums = bundle.getString("albums", "");
		imtro = bundle.getString("imtro", "");
		step = bundle.getString("step", "");
		
		MyDbOperator mDboperator=new MyDbOperator(new MyDbOpenHelper(this).getWritableDatabase());
		mDboperator.chosenById(id);
		mDboperator.close();

		Title = (TextView) this.findViewById(R.id.title);
		Title.setText(title);
		M_material = (TextView) this.findViewById(R.id.m_material);
		M_material.setText(ingredients);
		A_material = (TextView) this.findViewById(R.id.a_material);
		A_material.setText(burden);
		F_tags = (TextView) this.findViewById(R.id.f_tags);
		F_tags.setText(tags);
		M_property = (TextView) this.findViewById(R.id.m_property);
		M_property.setText(imtro);
		Food_imge = (ImageView) this.findViewById(R.id.food_image);
		Food_step = (TextView) this.findViewById(R.id.food_step);
		Food_step.setOnClickListener(this);

		Thread mthread = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				HttpClient httpClient = new DefaultHttpClient();
				String imageurl = change_format(albums);
				HttpGet httpGet = new HttpGet(imageurl);
				final Bitmap bitmap;
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					bitmap = BitmapFactory.decodeStream(httpResponse
							.getEntity().getContent());
				} catch (Exception e) {
					myhandler.obtainMessage(MSG_FAILURE).sendToTarget();
					return;
				}
				myhandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget();
				Looper.loop();
			}
		});
		mthread.start();

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

	// 转化网络图片地址的格式
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

	class Myhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SUCCESS:

				// 读取文件中的数据，判断用户有没有设置无图模式
				SharedPreferences setting = getSharedPreferences("mypf", 0);
				String no_pic = setting.getString("no_pic", "");
				if (no_pic.equals("true")) { // 如果用户设置了无图模式，就执行下面的程序
					Food_imge.setBackgroundResource(R.drawable.no_pic_1);
				} else { // 表示用户没有设置无图模式
					Food_imge.setImageBitmap((Bitmap) msg.obj);
				}
				break;
			case MSG_FAILURE:
				Toast.makeText(getApplication(), "图片加载失败", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(Menu_foodshow.this,
				Menu_food_step_show.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("step", step);
		intent.putExtras(mBundle);
		startActivity(intent);
	}
}
