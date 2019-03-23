package com.android.data;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
public class MyDbOperator {
	public SQLiteDatabase mDb=null;
	public final String dataTable=MyDbOpenHelper.dataTable;
	public final String effectTable=MyDbOpenHelper.effectTable;
	public final int numberCid=20;	//使用标签搜索每次返回结果最大数量；
	public final int numberEditText=20;	//使用搜索框每次返回结果最大数量；
	private String sql=null;
	public MyDbOperator(SQLiteDatabase mDb)
	{
		this.mDb=mDb;
	}
	Cursor rawQuery(String sql)
	{
		return mDb.rawQuery(sql,null);
	}
	//菜单界面，通过原始cid寻找数据；
	public Cursor rawQueryByCid(int cid)
	{
		sql="select * from "+dataTable+" where cid = ?";
		return mDb.rawQuery(sql,new String[]{String.valueOf(cid)});
	}
	//function：搜索界面，通过新建cid寻找符合条件的id
	//param:一些cid号
	//return:最符合条件的N条数据；
	public Cursor rawQueryByNewCids(int []cids)
	{
//		SELECT d.id,pid,tags from dtl_4 d  
//		join (select eee.id from (select ee.id,(cid_1*1+cid_2*1+cid_4*1)
//		as grade from effect ee order by grade desc ,ee.id desc limit 20) eee) eeee
//		where d.id=eeee.id;
		if(cids!=null&&cids.length>0)
		{
			//每次搜索标签时，代表对该标签刚兴趣；
			chosenByTags(cids);
			sql="SELECT * FROM "+dataTable+" d " +
					"JOIN ( SELECT eee.id FROM ("+
					"SELECT ee.id,ee.chosenFrequence,ee.chosenTagFrequence,ee.chosenReferenceTagFrequence,(";
			for(int i=0;i<cids.length;i++)
			{
				sql+="cid_"+cids[i]+"*1+";
			}
			sql=sql.substring(0,sql.length()-1);	//去掉最后一个加号
			sql+=") as grade FROM "+effectTable+" ee " +
					"order by grade desc,ee.chosenFrequence desc,ee.chosenTagFrequence desc LIMIT +"+numberCid+" ) eee )eeee "+
					"where d.id=eeee.id ";
			return mDb.rawQuery(sql, null);
			
		}
		else
		{
			return rawQueryByBlank();
		}
	}
	
	//function：什么也不输入，根据以前喜好匹配；(根据被搜的频率)；
	//return:
	public Cursor rawQueryByBlank()
	{
		sql="SELECT * FROM "+dataTable+" d " +
				"JOIN ( SELECT eee.id FROM ("+
				"SELECT ee.id,ee.chosenFrequence,ee.chosenTagFrequence,ee.chosenReferenceTagFrequence";
		sql+="	 FROM "+effectTable+" ee " +
				"order by ee.chosenFrequence desc,ee.chosenTagFrequence desc LIMIT "+numberCid+" ) eee )eeee "+
				"where d.id=eeee.id ";
		return mDb.rawQuery(sql, null);
	}
	
	
			
	
	//function:通过搜索框进行搜索的结果
	//param:String[];  condition;type:1,tags,属性;2:taste,3:burden,4:title;5.ingredients
	//return:
	public Cursor rawQueryByTags(String[]condition,int type)
	{
		
		if(condition!=null&&condition.length>0)
		{
			String typeStr=null;//搜索类型；
			sql="SELECT * FROM "+dataTable+" d WHERE ";
			switch(type)
			{
				case 3:{
					typeStr="d.burden ";
					break;
				}
				case 4:{
					typeStr="d.title ";
					break;
				}
				case 5:{
					typeStr="d.ingredients ";
					break;
				}
				default:
				{
					typeStr="d.tags ";
				}
			}
			for(int i=0;i<condition.length;i++)
			{
				sql+=typeStr+" like \"%"+condition[i]+"%\" AND ";
			}
			sql=sql.substring(0,sql.length()-4);
			sql+=" LIMIT "+numberEditText;
			return mDb.rawQuery(sql, null);
		}
		else
		{
			return rawQueryByBlank();
		}
	}
	
	//function:既有搜索框又有标签时；必须先去满足搜索框（获取一批id，制成临时表），获取的结果中再满足标签；
	//param:String[] conditon,int type,int[]cid;//确保不为空，否则调用上面的函数；
	//return:
	public Cursor rawQueryByNewCidsAndTags(String []condition,int type,int []cids)
	{
		//每次搜索标签时，代表对该标签刚兴趣；
		chosenByTags(cids);
		
		String typeStr=null;//搜索类型；
		//首先满足tags，满足条件id选出来；
		sql="CREATE TEMPORARY TABLE IF NOT EXISTS IdsTable (id integer PRIMARY KEY)";
		mDb.execSQL(sql);
		sql="REPLACE INTO IdsTable SELECT id FROM "+dataTable+" d WHERE ";
		switch(type)
		{
			case 3:{
				typeStr="d.burden ";
				break;
			}
			case 4:{
				typeStr="d.title ";
				break;
			}
			case 5:{
				typeStr="d.ingredients ";
				break;
			}
			default:
			{
				typeStr="d.tags ";
			}
		}
		for(int i=0;i<condition.length;i++)
		{
			sql+=typeStr+" like \"%"+condition[i]+"%\" AND ";
		}
		sql=sql.substring(0,sql.length()-4);
		mDb.execSQL(sql);
		//在满足条件的所有id中，选择满足标签的；
		sql="SELECT * FROM "+dataTable+" d " +
				"JOIN ( SELECT eee.id FROM ("+
				"SELECT ee.id,ee.chosenFrequence,ee.chosenTagFrequence,ee.chosenReferenceTagFrequence,(";
		for(int i=0;i<cids.length;i++)
		{
			sql+="cid_"+cids[i]+"*1+";
		}
		sql=sql.substring(0,sql.length()-1);	//去掉最后一个加号
		sql+=") as grade FROM "+effectTable+" ee " +
				"order by grade desc,ee.chosenFrequence desc,ee.chosenTagFrequence desc ) eee )eeee "+
				"JOIN IdsTable i "+
				"where d.id=eeee.id AND eeee.id=i.id LIMIT "+numberEditText;
		return mDb.rawQuery(sql, null);
	}
	public void close()
	{
		 mDb.close();
	}
	//清除缓存，将所有菜
	//chosenFrequence,ee.chosenTagFrequence,ee.chosenReferenceTagFrequence
	//三个字段值置为零。
	public void clear()	
	{
		sql="UPDATE "+effectTable+" SET chosenFrequence=0,chosenTagFrequence=0 where chosenFrequence!=0 or chosenTagFrequence!=0";
		mDb.execSQL(sql);
	} 
	//每次查看一道菜的详细内容，代表使用者对该道菜感兴趣，使该道菜的选中频率加一；
	public void chosenById(String id)
	{
		sql="UPDATE "+effectTable+" SET chosenFrequence=chosenFrequence+1 WHERE id = ?";
		mDb.execSQL(sql,new String[]{id});
	}
	//function：每次进行标签搜索，说明使用者对于该标签感兴趣，是具有该标签的菜的chosenTagFrequence+1;
	//param:tags的ids，int[]tagsIds
	public void chosenByTags(int[]tagsIds)
	{
		for(int i=0;i<tagsIds.length;i++)
		{
			sql="UPDATE "+effectTable+" SET chosenTagFrequence =chosenTagFrequence+1 WHERE cid_"+String.valueOf(tagsIds[i])+"!= '0'";
			mDb.execSQL(sql);
		}
		
	}
	//@param:需要查询的id们,int [];
	//function:通过一批id查询
	//return:
	public Cursor rawQueryByIds(String []ids)
	{
		sql="SELECT * FROM "+dataTable+" WHERE id in (";
		for(int i=0;i<ids.length;i++)
			sql+="'"+ids[i]+"',";
		sql=sql.substring(0,sql.length()-1);
		sql+=")";
		//将ids变为参数；
		//return mDb.rawQuery(sql,ids.toString().split("[\\[\\]]")[1].split(","));
		return mDb.rawQuery(sql,null);
	}
	
	
}







