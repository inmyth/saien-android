package jp.navi.saien.activities.api;

import retrofit.http.POST;

public interface SimpleApi {
	
	@POST("services/api/rest/json?method=upload.mobpic")
	public String picPost();

}
