package com.vibridi.qgu.storage;

import com.vibridi.qgu.storage.api.IQGUStorage;

public enum QGUStorageManager {
	instance;
	
	private IQGUStorage storage;
	
	private QGUStorageManager() {
	}
	
}
