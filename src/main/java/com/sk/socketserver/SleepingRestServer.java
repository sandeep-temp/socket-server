package com.sk.socketserver;

import java.io.IOException;
import java.io.OutputStream;

import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.requestprocessor.SleepingGetRequestProcessor;
import com.sk.socketserver.requestprocessor.SleepingPutRequestProcessor;
import com.sk.socketserver.requestprocessor.RequestProcessor;

public class SleepingRestServer {

	private RequestProcessor requestProcessor;

	public SleepingRestServer(InputRequest inputRequest, OutputStream outputStream) {
		switch (inputRequest.getMethodType()) {
		case GET:
			requestProcessor = new SleepingGetRequestProcessor(inputRequest,
					outputStream);
			break;
		case PUT:
			requestProcessor = new SleepingPutRequestProcessor(inputRequest,
					outputStream);
			break;
		default:
			try {
				outputStream.write("Invalid method(Get/Put)".getBytes());
				outputStream.close();
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
