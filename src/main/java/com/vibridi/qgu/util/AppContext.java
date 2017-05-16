package com.vibridi.qgu.util;

import java.util.prefs.Preferences;

import com.vibridi.qgu.Main;

public class AppContext {

	public static final String VERSION_NUMBER = Main.class.getPackage().getImplementationVersion();
	
	public static final Preferences preferences = Preferences.userRoot().node("qgu");
	

}
