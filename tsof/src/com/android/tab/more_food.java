package com.android.tab;

import com.android.tab.R;
import com.android.zhy_slidingmenu.SlidingMenu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class more_food extends Activity implements OnClickListener {
	private TextView back = null; // 返回键
	private WebView webview = null;

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
		setContentView(R.layout.more_food);
		back = (TextView) this.findViewById(R.id.back);
		back.setOnClickListener(this); // 为返回键设置监听事件
		webview = (WebView) this.findViewById(R.id.webview);

		webview.setVerticalScrollBarEnabled(false); // 取消滚动条
		webview.getSettings().setUseWideViewPort(true);// 使得网页适屏显示
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setBuiltInZoomControls(true);

		// 设置使用默认浏览器，不跳转到别的浏览器
		WebSettings settings = webview.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);

		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportMultipleWindows(true);
		webSettings.setSupportZoom(true);

		// 设置跳转的地址
		webview.loadUrl("http://www.zhms.cn/");
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

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
			Toast.makeText(getApplicationContext(), "再按一次退出更多美食栏目",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			//System.exit(0);
		}
	}

	@Override
	public void onClick(View arg0) {
		finish(); // 点击返回键退出app
	}
}
