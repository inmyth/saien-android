package jp.navi.saien.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.text.format.DateUtils;

public class DateUtil {

	
	public static String getDayCues(String a, String delimiter){
		String[] e = a.split("[/]");		
		Calendar cal = new GregorianCalendar(Integer.parseInt(e[0]), Integer.parseInt(e[1])-1, Integer.parseInt(e[2]));
		Calendar c = Calendar.getInstance(); 
		return DateUtils.getRelativeTimeSpanString(cal.getTimeInMillis(), c.getTimeInMillis(), DateUtils.DAY_IN_MILLIS).toString();
	}
	
}
