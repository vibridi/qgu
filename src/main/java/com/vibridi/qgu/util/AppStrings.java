package com.vibridi.qgu.util;

import java.io.InputStream;
import java.util.Properties;

import com.vibridi.qgu.Main;

public enum AppStrings {
	instance;
	
	private Properties props;
	
	private AppStrings() {
		props = new Properties();
		InputStream in = Main.class.getResourceAsStream("/loc/qgu_en.properties");
		try {
			props.load(in);
		} catch(Throwable e) {
			throw new RuntimeException("Cannot load properties", e);
		}
	}
	
	
	
}
