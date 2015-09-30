package jp.navi.saien.json;

import java.util.ArrayList;


public class CropItem {
//	title: "test 002",
//	plan: "リーフレタス",
//	datemin: "2013/02/25",
//	customCropType: null,
//	data: [
//	"2013/03/17|植付け|true",
//	"2013/03/30|収穫|true"
//	],
//	monthmax: "201303",
//	body: ""
//	},
	
	private int guid;
	private String title;
	private String plan;
	private String datemin;
	private String customCropType;
	private ArrayList<String> data;
	private String monthmax;
	private String body;
	private String place;
	private String breed;
	
	public void setBreed(String breed) {
		this.breed = breed;
	}
	
	public String getBreed() {
		return breed;
	}
	
	public String getPlace() {
		return place;
	}
	
	public void setPlace(String place) {
		this.place = place;
	}
	
	public void setGuid(int guid) {
		this.guid = guid;
	}
	
	public int getGuid() {
		return guid;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPlan() {
		return plan;
	}
	
	
	public String getCustomCropType() {
		return customCropType;
	}
	
	public void setCustomCropType(String customCropType) {
		this.customCropType = customCropType;
	}
	
	
	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	public String getDatemin() {
		return datemin;
	}
	
	public void setDatemin(String datemin) {
		this.datemin = datemin;
	}
	
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
	
	public ArrayList<String> getData() {
		return data;
	}
	
	public void setMonthmax(String monthmax) {
		this.monthmax = monthmax;
	}
	
	public String getMonthmax() {
		return monthmax;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	

}
