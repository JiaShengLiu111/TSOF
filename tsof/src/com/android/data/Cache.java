package com.android.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import com.android.tab.Search.new_food;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class Cache {
	public static Context context=null;
	private static ArrayList<String> idsList=null;	//全局维护一个收藏id数组；
	private static ArrayList<Boolean>idsExist=null;//全局维护id的存在性与否；false,未被删除，true，已被删除出收藏列表；
	public static boolean isDirty=true;//默认初始值为脏，需要重新读取数据；
	//function：当退出app时，将内存中的仍然在收藏列表中的id们写进文件里；
	//return:true;写入成功，false；写入失败；
	public static boolean writeCollection()
	{
		
		//当idsList为空时，表明未曾打开过任何与收藏有关的功能； 
		if(idsList!=null)
		{
			OutputStream output = null;
			PrintStream print = null;
			try {
				output = context.openFileOutput("collection.txt",
						Activity.MODE_PRIVATE);
				print = new PrintStream(output);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			for(int i=0;i<idsList.size();i++)
			{
				if(!idsExist.get(i))
				{
					print.print( idsList.get(i)+ ",");
				}
			}
			if (print != null) {
				print.close();
			}
		}
		
		return true;
	}
	
		
	
	//function:在收藏界面，点击删除收藏，此处需要刷新adapter;但是不会改变脏位；
	//param:String id;
	public static void deleteCollection(String id)
	{
		int i = 0;
		for (i = 0; i < idsList.size(); i++) {
			if (idsList.get(i).equals(id)) {
				idsExist.set(i, true);
			}
		}
	}

	// function:点击收藏
	// param:id String;
	// return：1，收藏成功；2，已收藏;0,收藏失败;
	public	static int addCollection(String id) 
	{
		if(idsList==null)
		{
			if(!readCollection())
				return 0;
		}
		int i = 0;
		for (i = 0; i < idsList.size(); i++) {
			//匹配到此id,如果此id的存在标记为true,表示可以添加，将其标记置为false，否则表示重复添加；
			if (idsList.get(i).equals(id))
			{
				if(idsExist.get(i))
				{
					idsExist.set(i,false);
					Cache.isDirty=true;
					return 1;
				}
				break;
			}
				
		}
		// 收藏里面没有，进行添加；
		//新增，使得再次进入“收藏界面”时，需要刷新列表，标记脏位；
		if (i == idsList.size()) {
			idsList.add(id);
			idsExist.add(false);
			isDirty=true;
			return 1;
		}
		//收藏里面已经有了;
		return 2;
	}

	//funciotn:根据目前的idsList和idsExist获取食物信息，可能一开始先进入菜单，改变了收藏之后再进入收藏查看；也可能直接进入收藏查看；
	//function:通过idsList是否为空来判断是否需要读文件；
	//function：通过脏位是否为真来判断是否需要重新读文件；
	//return：返回食物信息
	public static ArrayList<new_food> getCollection() {
		ArrayList<new_food> food_info = new ArrayList<new_food>();
		MyDbOperator mDbOperator = new MyDbOperator(
				new MyDbOpenHelper(Cache.context).getReadableDatabase());
		//初始化，读取文件信息；
		if(idsList==null)
		{
			//读取失败，返回空值；
			if(!readCollection())
				return food_info;
		}
		int falseCount=0;//此时仍然在列表中的id个数；
		for(int i=0;i<idsList.size();i++)
		{
			if(!idsExist.get(i))
				falseCount++;
		}
		String ids[]=new String[falseCount];
		for(int i=0,j=0;i<idsList.size();i++)
		{
			if(!idsExist.get(i))
				ids[j++]=idsList.get(i);
		}

		if(ids!=null&&ids.length!=0)
		{
			Cursor result = mDbOperator.rawQueryByIds(ids);
			for (result.moveToFirst(); !result.isAfterLast(); result
					.moveToNext()) {
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
		}
		//每次重新读取一次数据之后，都将脏位置为假；
		isDirty=false;
		return food_info;
	}

	//function：读取collection.txt文件，初始化idsList和idsExist；
	public static boolean readCollection()
	{
		idsList=new ArrayList<String>();
		idsExist=new ArrayList<Boolean>();
		InputStream input = null;
		Scanner scan = null;
		String idsStr = "";
		try {
			input = context.openFileInput("collection.txt");
			scan = new Scanner(input);
			while (scan.hasNext())
				idsStr += scan.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		if (scan != null)
			scan.close();
		if(!idsStr.equals(""))
		{
			String[] ids = idsStr.split(",", 0);
			for(int i=0;i<ids.length;i++)
			{
				idsList.add(ids[i]);
				idsExist.add(false);
			}
		}
		return true;
	}

	//function:第一次使用app进行初始化的过程；
	//param:传递context，因为之后需要多次使用读写文件，
	//return:是否初始化成功；
	public static boolean init(Context context) {
		Cache.context=context;
		return copyRawData()&&createCollection()&&createRecord();

	}

	//function:当collection.txt文件打不开时,或者不存在时调用，重新创建一个文件；
	//收藏；
	//return：true,创建成功；false:创建失败；
	public static boolean createCollection()
	{
		// 第一次使用时，初始化创建一个收藏文件夹；
		File file = new File("/data/data/com.android.tab/files/collection.txt");
		if (!file.exists()) {
			File fileParent=file.getParentFile();
			try {
				if(!fileParent.exists())
				{
					fileParent.mkdir();
				}
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		// 上面是收藏文件夹；
		return true;
	}

	//function:当record.txt文件打不开时,或者不存在时调用，重新创建一个文件；
	//record:历史记录；
	//return：true,创建成功；false:创建失败；
	public static boolean createRecord()
	{
		// 第一次使用时，初始化创建一个历史记录文件；
		File file = new File("/data/data/com.android.tab/files/record.txt");
		if (!file.exists()) {
			File fileParent=file.getParentFile();
			try {
				if(!fileParent.exists())
				{
					fileParent.mkdir();
				}
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public static boolean copyRawData()
	{
		String DB_PATH = "/data/data/com.android.tab/databases/";
		String DB_NAME = "rawData.db";
		// 检查 SQLite 数据库文件是否存在
		if ((new File(DB_PATH + DB_NAME)).exists() == false) {
			// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
			File f = new File(DB_PATH);
			// 如 database 目录不存在，新建该目录
			if (!f.exists()) {
				f.mkdir();
			}
			try {
				// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
				InputStream is = context.getAssets().open(DB_NAME);
				// 输出流
				OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);

				// 文件写入
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				// 关闭文件流
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false; 
			}
		}
		return true;
	}
	
	
	
	//留下备用；
	Bitmap cut(Bitmap src)
	{
		int radius = src.getWidth();
		Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888); 
		Canvas c = new Canvas(dest); 
		Paint paint = new Paint(); 
		paint.setColor(Color.BLACK); 
		paint.setAntiAlias(true); 
		Path path = new Path();
		path.addCircle(radius/2, radius/2, radius/2, Path.Direction.CCW); 
		c.clipPath(path); //裁剪区域 
		c.drawBitmap(src, 0, 0, paint);//把图画上去 
		return dest;
		
	}

}
