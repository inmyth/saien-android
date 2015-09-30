package jp.navi.saien.json;

import java.util.ArrayList;

public class CropListResponse extends BaseResponse {
	
	private Result result;
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
		
	public class Result {
		private int userGuid;
		private String userName;
		private ArrayList<CropItem> cropItems;

		public void setUserGuid(int userGuid) {
			this.userGuid = userGuid;
		}

		public int getUserGuid() {
			return userGuid;
		}

		public void setCropItems(ArrayList<CropItem> cropItems) {
			this.cropItems = cropItems;
		}

		public ArrayList<CropItem> getCropItems() {
			return cropItems;
		}
		
		public void setUserName(String userName) {
			this.userName = userName;
		}
		
		public String getUserName() {
			return userName;
		}
		
	}
}
