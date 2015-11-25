package com.example.jorgegil.closegamealert;

public interface Config {

	// used to share GCM regId with application server - using php app server
	static final String APP_SERVER_URL = "http://phpstack-4722-10615-67130.cloudwaysapps.com/gcm.php?shareRegId=1";

	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "532852092546";
	static final String MESSAGE_KEY = "message";

}
