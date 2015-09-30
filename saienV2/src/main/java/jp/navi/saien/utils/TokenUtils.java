package jp.navi.saien.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jp.navi.saien.R;
import jp.navi.saien.activities.splash.LoginActivity;
import jp.navi.saien.json.BaseResponse;
import jp.navi.saien.json.StringResponse;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

public class TokenUtils {
	
	private StringRequest tokenRequest;
	private RequestFuture<String> tokenFuture = RequestFuture.newFuture();
	private ErrorListener errorListener;
    private RequestQueue queue =VolleyUtils.getRequestQueue();
    private TokenListener tokenListener;

	
	public TokenUtils(final Activity activity, TokenListener listener) {
		this.tokenListener = listener;
		
		errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Intent i = new Intent(activity, LoginActivity.class);

				i.putExtra(LoginActivity.LOAD_RETURN_ERROR, activity.getString(R.string.error_server));
				activity.startActivity(i);
				activity.finish();				
			}
		};
		
		
		tokenRequest = new StringRequest(Request.Method.POST, UrlUtils.getAuthenticateUrl(), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				StringResponse stringResponse = GsonUtils.toBean(response, StringResponse.class);	
				tokenListener.onNewToken(stringResponse.getResult());				
			}
			
			
		}, errorListener)
		
		{
			
			 @Override
			 protected Map<String,String> getParams(){
				 String username = PreferenceUtils.getUserName(activity);
				 String password = PreferenceUtils.getPassword(activity);
				 return UrlUtils.getPostParamsAuthenticate(username, password);
			 }
			  
			 @Override
			 public Map<String, String> getHeaders() throws AuthFailureError {
				 Map<String,String> params = new HashMap<String, String>();
				 params.put("Content-Type","application/x-www-form-urlencoded");
				 return params;
			 }
			
		};	
		
		
		
	}
	
	
	
	
	public void requestToken(){
		queue.add(tokenRequest);
		
	}
	
	public interface TokenListener {
		
		public void onNewToken(String token);
		
	}
	
	
	


}
