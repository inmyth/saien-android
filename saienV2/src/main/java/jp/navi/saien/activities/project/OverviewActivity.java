package jp.navi.saien.activities.project;

import jp.navi.saien.R;
import jp.navi.saien.activities.project.CropOverviewFragment.CropOverviewListener;
import jp.navi.saien.activities.splash.LoginActivity;
import jp.navi.saien.json.CropItem;
import jp.navi.saien.json.CropListResponse;
import jp.navi.saien.utils.GsonUtils;
import jp.navi.saien.utils.PreferenceUtils;
import jp.navi.saien.utils.TokenUtils;
import jp.navi.saien.utils.UrlUtils;
import jp.navi.saien.utils.VolleyUtils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.androidquery.AQuery;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class OverviewActivity extends SherlockFragmentActivity implements CropOverviewListener {
	public static final String LOAD_CROP_ITEM = "LOAD_CROP_ITEM";
	
	private AQuery a;
	private SherlockFragment fragment;
    private RequestQueue queue = VolleyUtils.getRequestQueue();

	
	@Override
	protected void onCreate(Bundle bundle) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(bundle);
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_overview);
		a = new AQuery(this);
		setSupportProgressBarIndeterminateVisibility(false);

		View customNav = LayoutInflater.from(this).inflate(R.layout.action_bar, null);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
	    getSupportActionBar().setCustomView(customNav);
	    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_lt)));

		
		
		 
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		fragment = CropOverviewFragment.newFragment(getIntent().getStringExtra(LoginActivity.LOAD_INITIAL_LOAD));
		
		getSupportFragmentManager()
		.beginTransaction()		
		.add(R.id.container, fragment)		
		.commit();
		
		String url = "http://saien-navi.jp/mod/profile/icondirect.php?guid=" + PreferenceUtils.getUserGuid(this);
		ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap bm) {
				Drawable d =new BitmapDrawable(getResources(), bm);
				getSupportActionBar().setIcon(d);				
			}
			 

		}, 100, 100, null, null);
		
		queue.add(ir);
		
		
		a.id(R.id.menu_saien).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.URL_SAIEN_OWNER));
				startActivity(browserIntent);
			}
		});
		
		a.id(R.id.menu_home).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.URL_DASHBOARD));
				startActivity(browserIntent);
			}
		});
			
		a.id(R.id.menu_album).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.URL_SAIEN_ALBUM));
				startActivity(browserIntent);
			}
		});
		
		a.id(R.id.menu_guide).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.URL_SAIEN_TRAINING));
				startActivity(browserIntent);
			}
		});
		
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
    	Crouton.clearCroutonsForActivity(this);
	}
	
	@Override
	public void onItemClick(CropItem cropItem) {
		Intent i = new Intent(this, BlogEditActivity.class);
		i.putExtra(LOAD_CROP_ITEM, GsonUtils.toJson(cropItem));			
		startActivity(i);			
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (intent.hasExtra(BlogEditActivity.LOAD_UPLOAD_COMPLETE)){
			Crouton.makeText(this, R.string.blog_posted, Style.CONFIRM).show();
		}
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		if (fragment instanceof CropOverviewFragment && !getIntent().hasExtra(LoginActivity.LOAD_INITIAL_LOAD)){
				 getCropItems(true);				
		}
		getIntent().removeExtra(LoginActivity.LOAD_INITIAL_LOAD);

	}
	
	private void getCropItems(final boolean retry){
		
		 String token = PreferenceUtils.getToken(this);
		 String urlCrops = UrlUtils.getCropListUrl(token);
		 StringRequest sr = new StringRequest(urlCrops, new Listener<String>() {

			@Override
			public void onResponse(String response) {																	
				CropListResponse cropListResponse = GsonUtils.toBean(response, CropListResponse.class);
				if (cropListResponse.getStatus() == -1 && retry){
					TokenUtils tokenUtils = new TokenUtils(OverviewActivity.this, new TokenUtils.TokenListener() {
						
						@Override
						public void onNewToken(String token) {
							if (token != null){
								PreferenceUtils.setToken(OverviewActivity.this, token);
								getCropItems(false);											
							}else{
								 setSupportProgressBarIndeterminateVisibility(false);
								 LoginActivity.crash(OverviewActivity.this);
							}
						
						}
					});			
					tokenUtils.requestToken();
				}else if (cropListResponse.getStatus() == 0){
					 setSupportProgressBarIndeterminateVisibility(false);
					 CropOverviewFragment cropOverviewFragment = (CropOverviewFragment) fragment;
					 cropOverviewFragment.buildList(response);										
				}else{
					 setSupportProgressBarIndeterminateVisibility(false);
					 LoginActivity.crash(OverviewActivity.this);
				}
			}
			 
		 }, null);
		 
		 setSupportProgressBarIndeterminateVisibility(true);
		 queue.add(sr);
		
	}
	
	
	
	


	

}
