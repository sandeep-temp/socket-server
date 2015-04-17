package com.sk.socketserver.request;

import java.net.Socket;

public class Request {

	private String connectionId;
	private Thread thread;
	private long expiryTime;
	private Socket socket;
	

	public Request(String connectionId, Thread thread, long expiryTime) {
		super();
		this.connectionId = connectionId;
		this.thread = thread;
		this.expiryTime = expiryTime;
	}

	
	public Request(String connectionId, Thread thread, long expiryTime, Socket socket) {
		super();
		this.connectionId = connectionId;
		this.thread = thread;
		this.expiryTime = expiryTime;
		this.setSocket(socket);
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}


	public Socket getSocket() {
		return socket;
	}


	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
