package jp.navi.saien.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtils {
	
	public static final String KEY_USER_NAME = "KEY_USER_ID";
	public static final String KEY_PASSWORD = "KEY_PASSWORD";
	public static final String KEY_TOKEN = "KEY_TOKEN";
	public static final String KEY_USER_GUID = "KEY_USER_GUID";
	
	
	  public static String getUserGuid(Context context){
		    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		    return sharedPreferences.getString(KEY_USER_GUID, null);	
	  }
		
	  
	  public static void setUserGuid(Context context, String guid){
		  Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		  editor.putString(KEY_USER_GUID, guid);
		  editor.commit();		  
	  }
	
	
	  public static String getUserName(Context context){
		    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		    return sharedPreferences.getString(KEY_USER_NAME, null);	
	  }
	  
	  public static void setUserName(Context context, String userName){
		  Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		  editor.putString(KEY_USER_NAME, userName);
		  editor.commit();		  
	  }
	  
	  public static String getPassword(Context context){
		    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		    return sharedPreferences.getString(KEY_PASSWORD, null);	
	  }
	  
	  public static void setPassword(Context context, String password){
		  Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		  editor.putString(KEY_PASSWORD, password);
		  editor.commit();		  
	  }

	  public static String getToken(Context context){
		    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		    return sharedPreferences.getString(KEY_TOKEN, null);	
	  }
	  
	  public static void setToken(Context context, String token){
		  Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		  editor.putString(KEY_TOKEN, token);
		  editor.commit();		  
	  }
	  
}
