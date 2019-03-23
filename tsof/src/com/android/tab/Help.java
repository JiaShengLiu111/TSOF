package com.android.tab;

import com.android.tab.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
 

public class Help extends Activity {
	private TextView back = null; // 返回按钮
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		back = (TextView) this.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View arg0) {
				finish();
			}
			
		}); // 点击退出键退出该activity
	}
	 
}
