package com.sk.socketserver.request.requestmanager;

import java.net.Socket;

public interface RequestManager {

	public void handleRequest(Socket socket);
}
