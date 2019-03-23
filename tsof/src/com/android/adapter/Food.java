package com.android.adapter;
public class Food {
	
	private int position;
	private String food_id="卤肉";
	private String food_pic;
	private String food_title;
	private String food_step_num;
	
	public void setPosition(int position){
		this.position=position;
	}
	public void setFood_id(String food_id){
		this.food_id=food_id;
	}
	public void setFood_pic(String food_pic){
		this.food_pic=food_pic;
	}
	public void setFood_step_num(String food_step_num){
		this.food_step_num=food_step_num;
	}
	public void setFood_title(String food_title){
		this.food_title=food_title;
	}
	public String getFood_id(){
		return food_id;
	}
	public String getFood_pic(){
		return food_pic;
	}
	public String getFood_title(){
		return food_title;
	}
	public String getFood_step_num(){
		return food_step_num;
	}
	public int getPosition( ){
		return position;
	}
}
