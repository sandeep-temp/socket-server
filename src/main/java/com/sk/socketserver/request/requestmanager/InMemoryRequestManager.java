package com.sk.socketserver.request.requestmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import com.sk.socketserver.InMemoryRestServer;
import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.request.MethodType;
import com.sk.socketserver.util.Constants;

public class InMemoryRequestManager implements RequestManager {

	@Override
	public void handleRequest(final Socket socket) {
		try {
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			InputRequest inputRequest = findRequestParameters(inputStream,
					outputStream);

			InMemoryRestServer restServer = new InMemoryRestServer(socket, inputRequest);
			
			restServer.process();
			
			
		} catch (IOException e) {
			System.out.println("IOException in handleRequest");
		}
	}

	private InputRequest findRequestParameters(InputStream inputStream,
			OutputStream outputStream) throws IOException {

		InputRequest inputrequest = parseInputRequest(inputStream, outputStream);
		return inputrequest;
	}

	private InputRequest parseInputRequest(InputStream inputStream,
			OutputStream outputStream) throws IOException {

		InputRequest inputRequest = new InputRequest();

		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader inputReader = new BufferedReader(isr);
		String line = inputReader.readLine();
		System.out.println(line);
		if (line.startsWith("GET")) {
			inputRequest.setMethodType(MethodType.GET);
			String[] lineArr = line.split(" ");
			inputRequest.parseUrl(lineArr[1]);
		} else if (line.startsWith("PUT")) {
			StringBuffer postRequest = new StringBuffer();
			boolean postReqBegin = false;

			inputRequest.setMethodType(MethodType.PUT);
			String[] lineArr = line.split(" ");
			inputRequest.parseUrl(lineArr[1]);
			line = inputReader.readLine();

			int contentLinesRead = 0;
			int contentLength = 0;
			while (line != null) {

				System.out.println("Line:" + line);
				if (line.startsWith(Constants.CONTENT_LENGTH)) {
					String contentLen = line.replace(Constants.CONTENT_LENGTH,
							"");
					contentLength = Integer.valueOf(contentLen);
					System.out.println("contentLength:" + contentLength);
				}
				if (line.equals("")) {
					postReqBegin = true;

				}
				if (postReqBegin) {
					char[] cbuf = new char[contentLength];
					inputReader.read(cbuf, 0, contentLength);
					line = new String(cbuf);
					System.out.println("Actual Content:" + line);
					postRequest.append(line);
					contentLinesRead++;
					if (postRequest.length() >= contentLength) {
						break;
					}
					if (contentLinesRead > 0) {

						break;
					}
				} else {
					line = inputReader.readLine();
				}
				
			}
			inputRequest.setPutBody(postRequest.toString());
			System.out.println("PostReq:" + postRequest.toString());
		} else {
			outputStream.write("Invalid Method".getBytes());
			outputStream.close();
		}

		return inputRequest;

	}
}
