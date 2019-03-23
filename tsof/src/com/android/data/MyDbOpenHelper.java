package com.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class MyDbOpenHelper extends android.database.sqlite.SQLiteOpenHelper{

	
	public static final String DBNAME="rawData.db";
	public static final String dataTable="dtl_4";
	public static final String effectTable="effect";
	public static final int DBVERSION=1;
	public Context context=null;
	public MyDbOpenHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
		this.context=context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sql="CREATE TABLE IF NOT EXISTS "+ dataTable +
				" ( id 			integer 		primary key,"
				+ "pid 			integer 		not null,"
				+"cid 			integer 		not null,"
				+"title			varchar(20)		not null,"
				+"tags			varchar(200)	not null,"
				+"ingredients	varchar(200)	not null,"
				+"burden		varchar(200)	not null,"
				+"albums		varchar(100)	not null,"
				+"stepNum		integer			not null,"
				+"imtro			varchar(300)	not null,"
				+"step			varchar(4000)	not null)";
		db.execSQL(sql);
	}
	public void onOpen(SQLiteDatabase db)
	{
		String sql="CREATE TABLE IF NOT EXISTS "+ dataTable +
				" ( id 			integer 		primary key,"
				+ "pid 			integer 		not null,"
				+"cid 			integer 		not null,"
				+"title			varchar(20)		not null,"
				+"tags			varchar(200)	not null,"
				+"ingredients	varchar(200)	not null,"
				+"burden		varchar(200)	not null,"
				+"albums		varchar(100)	not null,"
				+"stepNum		integer			not null,"
				+"imtro			varchar(300)	not null,"
				+"step			varchar(4000)	not null)";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + dataTable ;		
		db.execSQL(sql);									
		this.onCreate(db); 									
	}
}
