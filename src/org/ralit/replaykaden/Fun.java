package org.ralit.replaykaden;

import android.util.Log;

class Fun {

	public static void log(String log) {
		if (log != null) {
			Log.i("ralit", log);
		} else {
			Log.i("ralit", "☆null☆");
		}
	}

	public static void log(Object object) {
		if (object != null) {
			Log.i("ralit", String.valueOf(object));
		} else {
			Log.i("ralit", "☆null☆");
		}
	} 

}
