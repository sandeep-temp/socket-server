package com.sk.socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.sk.socketserver.request.requestmanager.InMemoryRequestManager;
import com.sk.socketserver.request.requestmanager.RequestManager;

public class SocketServer {

	static ServerSocket serverSocket;

	static RequestManager requestManager;

	public static void main(String[] args) {

		new Thread(new EvictionProcess()).start();
		
		requestManager = new InMemoryRequestManager();
		
		try {
			String port;
			if (args == null || args.length == 0) {
				// defaulting to 8123 port
				port = "8123";
			} else {
				port = args[0];
			}
			final int serverPort = Integer.valueOf(port).intValue();
			serverSocket = new ServerSocket(serverPort);
			
			while (true) {

				Socket socket = serverSocket.accept();
				requestManager.handleRequest(socket);

			}

		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
				// Do Nothing.
			}
		}

	}
}
