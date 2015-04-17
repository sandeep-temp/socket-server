package com.sk.socketserver;

import java.io.IOException;
import java.net.Socket;

import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.requestprocessor.InMemoryGetRequestProcessor;
import com.sk.socketserver.requestprocessor.InMemoryPutRequestProcessor;
import com.sk.socketserver.requestprocessor.RequestProcessor;

public class InMemoryRestServer {

	private RequestProcessor requestProcessor;

	public InMemoryRestServer(Socket socket, InputRequest inputRequest) {
		super();

		switch (inputRequest.getMethodType()) {
		case GET:
			requestProcessor = new InMemoryGetRequestProcessor(socket, inputRequest);
			break;
		case PUT:
			requestProcessor = new InMemoryPutRequestProcessor(socket, inputRequest);
			break;
		default:
			try {
				socket.getOutputStream().write(
						"Invalid method(Get/Put)".getBytes());
				socket.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			throw new IllegalArgumentException();
		}
	}
	
	public void process() {

		requestProcessor.process();
	}

}
