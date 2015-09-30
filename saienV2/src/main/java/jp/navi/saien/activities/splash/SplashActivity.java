package jp.navi.saien.activities.splash;

import java.util.HashMap;
import java.util.Map;

import jp.navi.saien.R;
import jp.navi.saien.activities.project.OverviewActivity;
import jp.navi.saien.json.BaseResponse;
import jp.navi.saien.json.CropListResponse;
import jp.navi.saien.json.StringResponse;
import jp.navi.saien.utils.GsonUtils;
import jp.navi.saien.utils.NetworkUtils;
import jp.navi.saien.utils.PreferenceUtils;
import jp.navi.saien.utils.UrlUtils;
import jp.navi.saien.utils.VolleyUtils;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.URLUtil;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.androidquery.AQuery;

public class SplashActivity extends SherlockActivity{
    public static final String LOAD_SPLASH_ERROR = "LOAD_SPLASH_ERROR";

	private AQuery a;
    private RequestQueue queue = VolleyUtils.getRequestQueue();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		a = new AQuery(this);
//		debug();
	}
	
	private void debug(){
		PreferenceUtils.setPassword(this, null);
		PreferenceUtils.setUserName(this, null);
		PreferenceUtils.setToken(this, null);
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	if (!NetworkUtils.isOnline(getBaseContext())){
    		toLogin(getString(R.string.error_no_internet));
    	}   	
    	
		final String userName = PreferenceUtils.getUserName(getBaseContext());
		final String password = PreferenceUtils.getPassword(getBaseContext());
		
		
		if (userName == null || password == null){
			Intent i = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(i);
			finish();
			
		}else{
			 StringRequest sr = new StringRequest(Request.Method.POST, UrlUtils.getAuthenticateUrl(), new Response.Listener<String>() {
				 @Override
				 public void onResponse(String response) {
					 StringResponse stringResponse = GsonUtils.toBean(response, StringResponse.class);
					 if (stringResponse.getStatus() == -1){
						 toLogin(getString(R.string.error_password));
						 return;
					 }				
					 
					 
					 PreferenceUtils.setToken(getBaseContext(), stringResponse.getResult());
					 
					 String urlCrops = UrlUtils.getCropListUrl(stringResponse.getResult());
					 StringRequest initLoad = new StringRequest(urlCrops, new Listener<String>() {

						@Override
						public void onResponse(String response2) {

							CropListResponse cropListResponse = GsonUtils.toBean(response2, CropListResponse.class);
							PreferenceUtils.setUserGuid(getBaseContext(), String.valueOf(cropListResponse.getResult().getUserGuid()));
							Intent i = new Intent(SplashActivity.this, OverviewActivity.class);
							i.putExtra(LoginActivity.LOAD_INITIAL_LOAD, response2);
							startActivity(i);
							finish();
						}
						 
					}, null);
					 
					 queue.add(initLoad);
					 
					 
				 }}, 
				 
				 new Response.ErrorListener() {
					 
					 @Override
					 public void onErrorResponse(VolleyError error) {
						 toLogin(getString(R.string.error_server));
					 }
				 })
			 	 {
					 @Override
					 protected Map<String,String> getParams(){
						 return UrlUtils.getPostParamsAuthenticate(userName, password);
					 }
				  
					 @Override
					 public Map<String, String> getHeaders() throws AuthFailureError {
						 Map<String,String> params = new HashMap<String, String>();
						 params.put("Content-Type","application/x-www-form-urlencoded");
						 return params;
					 }
				 };
			
				 queue.add(sr);
			
		}
    }
    
	
	private void toLogin(String error){
		Intent i = new Intent(this, LoginActivity.class);
		i.putExtra(LOAD_SPLASH_ERROR, error);
		startActivity(i);
		finish();				
	}

}
