package jp.navi.saien.activities.splash;

import java.util.HashMap;
import java.util.Map;

import jp.navi.saien.R;
import jp.navi.saien.activities.project.OverviewActivity;
import jp.navi.saien.json.CropListResponse;
import jp.navi.saien.json.StringResponse;
import jp.navi.saien.utils.GsonUtils;
import jp.navi.saien.utils.PreferenceUtils;
import jp.navi.saien.utils.ProgressDialogUtils;
import jp.navi.saien.utils.UrlUtils;
import jp.navi.saien.utils.VolleyUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidquery.AQuery;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginActivity extends SherlockActivity{
	public static final String LOAD_INITIAL_LOAD = "LOAD_INITIAL_LOAD";
	public static final String LOAD_RETURN_ERROR = "LOAD_RETURN_ERROR";
	
	private AQuery a;
    private RequestQueue queue = VolleyUtils.getRequestQueue();

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_NoActionBar);
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_login);
		
		a = new AQuery(this);
		
		a.id(R.id.forgot).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.URL_FORGOTTEN_PASSWORD));
				startActivity(browserIntent);				
			}
		});
		
		a.id(R.id.register).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.URL_REGISTER));
				startActivity(browserIntent);			
			}
		});
		

		a.id(R.id.signin).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String username = a.id(R.id.username).getText().toString();
				final String password = a.id(R.id.password).getText().toString();
				boolean isOk = true;
				if (username.trim().length() == 0){
					a.id(R.id.username).getEditText().setError(getString(R.string.required));
					isOk = false;
				}
				if (password.trim().length() == 0){
					a.id(R.id.password).getEditText().setError(getString(R.string.required));
					isOk = false;
				}
				
				if (!isOk){
					return;
				}
				
								
				final ProgressDialog pd = ProgressDialogUtils.showIndeterminate(LoginActivity.this);


				
				 StringRequest sr = new StringRequest(Request.Method.POST, UrlUtils.getAuthenticateUrl(), new Response.Listener<String>() {
					 
					 @Override
					 public void onResponse(String response) {

						 StringResponse stringResponse = GsonUtils.toBean(response, StringResponse.class);
						 if (stringResponse.getStatus() == -1){
							    pd.dismiss();
					    		Crouton.makeText(LoginActivity.this, R.string.error_password, Style.ALERT).show();
					    		return;
						 }
						 
						 PreferenceUtils.setUserName(getBaseContext(), username);
						 PreferenceUtils.setPassword(getBaseContext(), password);
						 PreferenceUtils.setToken(getBaseContext(), stringResponse.getResult());
						 
						 
						 String urlCrops = UrlUtils.getCropListUrl(stringResponse.getResult());
						 StringRequest initLoad = new StringRequest(urlCrops, new Listener<String>() {

							@Override
							public void onResponse(String response2) {
								pd.dismiss();

								CropListResponse cropListResponse = GsonUtils.toBean(response2, CropListResponse.class);
								PreferenceUtils.setUserGuid(getBaseContext(), String.valueOf(cropListResponse.getResult().getUserGuid()));
								Intent i = new Intent(LoginActivity.this, OverviewActivity.class);
								i.putExtra(LOAD_INITIAL_LOAD, response2);
								startActivity(i);
								finish();
							}
							 
						}, null);
						 
						 queue.add(initLoad);
						 
					 }}, new Response.ErrorListener() {
						 
						 @Override
						 public void onErrorResponse(VolleyError error) {
								pd.dismiss();
								Crouton.makeText(LoginActivity.this, R.string.error_server, Style.ALERT).show();
						 }
					 }){
					 
						 @Override
						 protected Map<String,String> getParams(){
							 return UrlUtils.getPostParamsAuthenticate(username, password);
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
		});
	}

	public static void crash(Activity activity){
		Intent i = new Intent(activity, LoginActivity.class);
		i.putExtra(LoginActivity.LOAD_RETURN_ERROR, activity.getString(R.string.error_server));
		activity.startActivity(i);
		activity.finish();		
		
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	if (getIntent().hasExtra(SplashActivity.LOAD_SPLASH_ERROR)){
    		Crouton.makeText(this, getIntent().getStringExtra(SplashActivity.LOAD_SPLASH_ERROR), Style.ALERT).show();
    	}
    	
    	if (getIntent().hasExtra(LOAD_RETURN_ERROR)){
    		Crouton.makeText(this, getIntent().getStringExtra(LOAD_RETURN_ERROR), Style.ALERT).show();

    	}
    	
    }
	
    @Override
    protected void onDestroy() {
    	Crouton.clearCroutonsForActivity(this);
    	super.onDestroy();
    }
}
