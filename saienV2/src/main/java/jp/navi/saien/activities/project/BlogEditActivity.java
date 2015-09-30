package jp.navi.saien.activities.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import jp.navi.saien.R;
import jp.navi.saien.activities.splash.LoginActivity;
import jp.navi.saien.json.BaseResponse;
import jp.navi.saien.json.CropItem;
import jp.navi.saien.utils.Config;
import jp.navi.saien.utils.GsonUtils;
import jp.navi.saien.utils.ImageUtils;
import jp.navi.saien.utils.PreferenceUtils;
import jp.navi.saien.utils.ProgressDialogUtils;
import jp.navi.saien.utils.TokenUtils;
import jp.navi.saien.utils.UrlUtils;
import jp.navi.saien.utils.VolleyUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.RequestQueue;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BlogEditActivity extends SherlockActivity{
	public static final String LOAD_UPLOAD_COMPLETE = "LOAD_UPLOAD_COMPLETE";
	
	private AQuery a;
	private final static int CODE_REQUEST_GALLERY = 78;
	private final static int CODE_REQUEST_CAMERA = 32;
    private RequestQueue queue = VolleyUtils.getRequestQueue();
    private Uri photoUri;
    private CropItem cropItem;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_edit);

		a = new AQuery(this);
		
		this.cropItem = GsonUtils.toBean(getIntent().getStringExtra(OverviewActivity.LOAD_CROP_ITEM), CropItem.class);
		
		a.id(R.id.browse).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);			 
				startActivityForResult(i, CODE_REQUEST_GALLERY);
				
			}
		});
		
		a.id(R.id.camera).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				camera();
			}
		});
		
		
			
		a.id(R.id.line1).text(getString(R.string.journal_composite, cropItem.getTitle()));
		
		ArrayAdapter<CharSequence> csAdapter = ArrayAdapter.createFromResource(this, R.array.options_cropstatus, android.R.layout.simple_spinner_item);
		csAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		a.id(R.id.spinner_cropstatus).adapter(csAdapter);
		
		a.id(R.id.submit).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (photoUri == null){
					Crouton.makeText(BlogEditActivity.this, R.string.error_no_photo, Style.ALERT).show();
					return;
				}
				
				upload(true);
			}
		});
		
		a.id(R.id.text).getEditText().setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(a.id(R.id.text).getEditText().getWindowToken(), 0);
                }

            }
        });
		
		camera();
	}
	
	private void upload(final boolean retry){
		final ProgressDialog pd = ProgressDialogUtils.showIndeterminate(this);
				
		String url = UrlUtils.getUploadMobPhotoUrl();
		String token = PreferenceUtils.getToken(BlogEditActivity.this);
		String content = a.id(R.id.text).getEditText().getText().toString();
		if (content.trim().length() ==  0 ){
			content = "e367633adbc6ee1ef0c66936ea21d912"; // default string = md5 of "default string"
		}
		String commentWanted = a.id(R.id.chk1).isChecked() ? "true" : "false";
		String commentsOn = a.id(R.id.chk2).isChecked() ? "Off" : "On";		
		int cropStatus = a.id(R.id.spinner_cropstatus).getSpinner().getSelectedItemPosition();
				
		Map<String, Object> params = UrlUtils.getPostParamsUploadMobPhoto(BlogEditActivity.this, token, cropItem.getGuid(), content, commentsOn, commentWanted, cropStatus, photoUri);
		
		a.ajax(url, params, String.class, new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String response, AjaxStatus status) {
				if (response == null){
					Crouton.makeText(BlogEditActivity.this, R.string.error_server, Style.ALERT).show();
					return;
				}
//					{"status":0,"result":{"success":0}}
		
				BaseResponse baseResponse = GsonUtils.toBean(response, BaseResponse.class);
			
				if (baseResponse.getStatus() == -1 && retry){
					TokenUtils tokenUtils = new TokenUtils(BlogEditActivity.this, new TokenUtils.TokenListener() {
						
						@Override
						public void onNewToken(String token) {
							if (token != null){
								PreferenceUtils.setToken(BlogEditActivity.this, token);
								upload(false);											
							}else{
								 pd.dismiss();
								 LoginActivity.crash(BlogEditActivity.this);
							}
						
						}
					});			
					tokenUtils.requestToken();
				}else if (baseResponse.getStatus() == 0){
					pd.dismiss();
					Intent i = new Intent(BlogEditActivity.this,OverviewActivity.class);
					i.putExtra(LOAD_UPLOAD_COMPLETE, "");
					startActivity(i);
					finish();								
				}else{
					 pd.dismiss();
					 LoginActivity.crash(BlogEditActivity.this);
				}
			}
			
		});
		 

	}
	
	private void camera(){
		 if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File dir = new File(Environment.getExternalStorageDirectory() + Config.DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		    String date = dateFormat.format(new Date());
			File photo = new File(dir, date + ".jpg");
			photoUri  = Uri.fromFile(photo);
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(i, CODE_REQUEST_CAMERA);

		 }else{
			 Crouton.showText(BlogEditActivity.this, R.string.error_sdcard, Style.ALERT);
		 }
		
	}


	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	       if (requestCode == CODE_REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
               this.photoUri = data.getData();
               
                String finalPath;
//              OI FILE Manager
                String filemanagerstring = photoUri.getPath();
//
//             //MEDIA GALLERY
             	String  selectedImagePath = getPath(photoUri);
	            if(selectedImagePath!=null)
	            	finalPath = selectedImagePath;
	            else
	            	finalPath = filemanagerstring;
	            

	            setImage();
	       }else if (requestCode == CODE_REQUEST_CAMERA){
	    	   if (resultCode == RESULT_OK){
	    		   setImage();
	    	   }else{
	    		   photoUri = null;
	    	   }
	    	   
	       }
	        
	}

	
	private void setImage(){

   			
   		    int targetW =  150;
   		    int targetH = 150;

   		    // Get the dimensions of the bitmap
   		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
   		    bmOptions.inJustDecodeBounds = true;
//   		    Bitmap bitmap = BitmapFactory.decodeFile(photoUri.getPath(), bmOptions);
   		    

   		    
   		    int photoW = bmOptions.outWidth;
   		    int photoH = bmOptions.outHeight;

   		    // Determine how much to scale down the image
   		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

   		    // Decode the image file into a Bitmap sized to fill the View
   		    bmOptions.inJustDecodeBounds = false;
   		    bmOptions.inSampleSize = scaleFactor;
   		    bmOptions.inPurgeable = true;

   			
//   			BitmapFactory.Options options = new BitmapFactory.Options();
//   			options.inSampleSize = ImageUtils.calculateInSampleSize(options, 150, 150);
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
		    Bitmap bmp = BitmapFactory.decodeFile(photoUri.getPath(), bmOptions);
			a.id(R.id.img1).image(bmp, AQuery.RATIO_PRESERVE);

		
	}
	
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Crouton.clearCroutonsForActivity(this);
    }

}
