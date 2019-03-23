package com.android.adapter;

public class Step {
	private String food_image;
	private String food_step;
	
	public Step() { 
	}
	public Step(String food_step,String food_image) {
		this.food_step=food_step;
		this.food_image=food_image;
	}
	public void setFood_image(String food_image){
		this.food_image=food_image;
	}
	public void setFood_step(String food_step){
		this.food_step=food_step;
	}
	public String getFood_image(){
		return food_image;
	}
	public String getFood_step(){
		return food_step;
	}
}
