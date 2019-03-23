package com.android.adapter;

import java.util.ArrayList;
import java.util.List;
import com.android.tab.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenulistAdapter extends BaseAdapter {
	
	private List<String> nemulist=new ArrayList<String>(); 
	private Context context=null;
	private LayoutInflater layoutInflater=null;
	
	 
	public MenulistAdapter(Context context,List<String> nemulist) {
		this.context=context;
		this.nemulist=nemulist;
		this.layoutInflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return nemulist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return nemulist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		class ViewHolder{
			public TextView menuitem_name;
		}
		ViewHolder vh=null;
		if(arg1==null){
			vh=new ViewHolder();
			arg1=layoutInflater.inflate(R.layout.menulist_item, null);
			vh.menuitem_name=(TextView) arg1.findViewById(R.id.menuitem_name);
			arg1.setTag(vh);
		}else{
			vh=(ViewHolder) arg1.getTag();
		}
		vh.menuitem_name.setText(nemulist.get(arg0));
		return arg1;
	}

}
