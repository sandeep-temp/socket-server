package com.sk.socketserver.util;

import com.google.gson.Gson;

public class Constants {

	public static final String CONNECTION_ID = "connId";

	public static final String TIMEOUT = "timeout";

	public static final String STATUS_OK = "{\"status\":\"ok\"}";

	public static final String STATUS_KILLED = "{\"status\":\"killed\"}";

	public static final String INVALID_CONNECTION_ID = "{\"status\":\"invalid connection Id : <connId>\"}";

	public static final String GET_SLEEP_REQUEST = "/api/request";

	public static final String GET_SERVER_STATUS_REQUEST = "/api/serverStatus";
	
	public static final String REQUEST_KILL = "/api/kill";
	
	public static final String CONTENT_LENGTH = "Content-Length: ";
	
	public static final String KILL_BODY_BEGIN = "connid=";
	
	public static final Gson GSON = new Gson();
}
