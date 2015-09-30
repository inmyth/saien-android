package jp.navi.saien.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.Uri;

public class UrlUtils {
		
	public final static String URL = "http://saien-navi.jp";
//	public final static String URL = "http://192.168.1.102";
	
	public final static String URL_FORGOTTEN_PASSWORD = URL + "/account/forgotten_password.php";
	public final static String URL_REGISTER = URL + "/account/register.php";
	
	public final static String URL_DASHBOARD = URL + "/pg/dashboard";
	public final static String URL_SAIEN_OWNER = URL + "/pg/saiennavi/owner";
	public final static String URL_SAIEN_ALBUM = URL + "/pg/photoalbum";
	public final static String URL_SAIEN_TRAINING = URL + "/pg/expages/read/saienbook";
	
	
	
	public final static String PATH = "services/api/rest/json";

	public final static String VAL_GET_TOKEN = "auth.gettoken";
	public final static String VAL_GET_CROPS = "get.crops";
	public final static String VAL_ADD_BLOG = "add.blog";
	public final static String VAL_UPLOAD_MOBPIC = "upload.mobpic";
	
	public final static String PARAM_LIMIT = "limit";
	public final static String PARAM_METHOD = "method";
	public final static String PARAM_USERNAME ="username";
	public final static String PARAM_PASSWORD = "password";
	public final static String PARAM_SUBMIT = "submit";
	public final static String PARAM_AUTH_TOKEN = "auth_token";
	public final static String PARAM_ID_CROP = "crop_id";
	public final static String PARAM_DESCRIPTION = "description";
	public final static String PARAM_UPLOAD = "upload";
	public final static String PARAM_GUID_FILE = "file_guid";
	public final static String PARAM_COMMENTS_ON = "comments_on";
	public final static String PARAM_COMMENT_WANTED = "commentwanted";
	public final static String PARAM_CROP_STATUS = "cropstatus";
	
	public static String getUrlPage(int id){
		return String.format(URL + "/pg/saiennavi/read/%1$d", id);	
	}
	
	
	public static String getUrlOwner(String userName){
		return URL + "/pg/saiennavi/add/" + userName; 
	}
	
	public static Map<String,String> getPostParamsAuthenticate(String username, String password){		
		HashMap<String, String> res = new HashMap<String, String>();
		res.put(PARAM_USERNAME, username);
		res.put(PARAM_PASSWORD, password);
		res.put(PARAM_SUBMIT, "Submit");
		return res;	
		
	}
	
	public static Map<String, Object> getPostParamsUploadMobPhoto(Context ctx, String token, int cropId, String content, String commentsOn, String commentWanted, int cropStatus, Uri fileUri ) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(PARAM_DESCRIPTION, content);
		res.put(PARAM_UPLOAD, ImageUtils.getResizedImage(ctx, fileUri, 2));
		res.put(PARAM_ID_CROP, cropId);
		res.put(PARAM_AUTH_TOKEN, token);	
		
		res.put(PARAM_COMMENTS_ON, commentsOn);
		res.put(PARAM_COMMENT_WANTED, commentWanted);
		res.put(PARAM_CROP_STATUS, cropStatus);
		return res;
	}
	
	public static String getUploadMobPhotoUrl(){
		Uri.Builder b = Uri.parse(URL).buildUpon();
		b.path(PATH);
		b.appendQueryParameter(PARAM_METHOD, VAL_UPLOAD_MOBPIC);
		b.build();
		return b.toString();
		}
	
	
	public static String getAuthenticateUrl(){
		Uri.Builder b = Uri.parse(URL).buildUpon();
		b.path(PATH);
		b.appendQueryParameter(PARAM_METHOD, VAL_GET_TOKEN);
		b.build();
		return b.toString();
	}
	
	// http://saien-navi.jp/services/api/rest/json?method=auth.gettoken&username=tester7&password=tester7
	
//	http://192.168.1.102/services/api/rest/json?method=get.crops&limit=10&auth_token=5fff17670e16e7a3fa059fe179543100
//		
//		http://saien-navi.jp/services/api/rest/json?method=auth.gettoken&username=tester7&password=tester7&submit=Submit
//		bccd17268248a2ee888d8602eb57b43f
	
	public static String getCropListUrl(String token){
		Uri.Builder b = Uri.parse(URL).buildUpon();
		b.path(PATH);	
		b.appendQueryParameter(PARAM_METHOD, VAL_GET_CROPS);
		b.appendQueryParameter(PARAM_AUTH_TOKEN, token);
		b.build();
		return b.toString();
		
		
	}
	
}
