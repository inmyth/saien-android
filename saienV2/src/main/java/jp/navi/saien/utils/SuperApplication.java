package jp.navi.saien.utils;


import org.acra.ACRA;

import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;


@ReportsCrashes(formKey = "dHhFUE5JbmtsNXA1RG4zSjRMYlRqdVE6MQ") // The form Google Key
public class SuperApplication extends Application {
	private static Context context;

	
	
	@Override
	public void onCreate() {
        ACRA.init(this);
        context = getApplicationContext();
        VolleyUtils.init(this);
		super.onCreate();
	}

    public static Context getAppContext() {
        return SuperApplication.context;
    }
    
}
