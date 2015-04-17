package com.sk.socketserver.requestprocessor;

import static com.sk.socketserver.util.Constants.GSON;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.request.Request;
import com.sk.socketserver.request.RequestMap;
import com.sk.socketserver.util.Constants;

public class InMemoryGetRequestProcessor implements RequestProcessor {

	private Socket socket;

	private InputRequest inputRequest;

	public InMemoryGetRequestProcessor(Socket socket, InputRequest inputRequest) {
		super();
		this.socket = socket;
		this.inputRequest = inputRequest;
	}

	@Override
	public void process() {
		try {
			OutputStream outputStream = socket.getOutputStream();

			boolean isValid = validateRequest(inputRequest);
			if (!isValid) {
				outputStream.write("Invalid Request".getBytes());
				outputStream.close();
				return;
			}
			switch (inputRequest.getUrlPath()) {
			case Constants.GET_SLEEP_REQUEST:
				executeAPIRequest();
				break;
			case Constants.GET_SERVER_STATUS_REQUEST:
				executeServerStatusRequest();
				break;
			default:
				System.out.println("Default in GETProcessor.process. breaking");
				outputStream.write("Invalid Request".getBytes());
			}
		} catch (IOException e) {
			System.out
					.println("IOException in InMemoryGetRequestProcessor.process");
		}
	}

	private void executeServerStatusRequest() {

		OutputStream outputStream = null;
		try {
			outputStream = socket.getOutputStream();
			long currentTime = System.currentTimeMillis() / 1000;
			Set<Entry<String, Request>> entrySet = RequestMap.entrySet();
			Map<String, String> map = new HashMap<String, String>();
			for (Entry<String, Request> entry : entrySet) {
				String connId = entry.getKey();
				Request request = entry.getValue();
				long remainingTime = ((request.getExpiryTime() - currentTime));
				map.put(connId, String.valueOf(remainingTime));
			}

			if (map != null && GSON.toJson(map) != null
					&& GSON.toJson(map).length() > 0) {
				outputStream.write(GSON.toJson(map).getBytes());
			} else {
				outputStream.write("{}".getBytes());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void executeAPIRequest() {

		long currentTime = System.currentTimeMillis();
		long currentSeconds = currentTime / 1000;
		long expirySeconds = currentSeconds
				+ Integer.valueOf(inputRequest.getUrlParams().get(
						Constants.TIMEOUT));

		String connectionId = inputRequest.getUrlParams().get(
				Constants.CONNECTION_ID);
		Request request = new Request(connectionId, Thread.currentThread(),
				expirySeconds, socket);

		RequestMap.put(connectionId, request);
		RequestMap.addConnectionIdForExpiryTime(expirySeconds, connectionId);

	}

	private boolean validateRequest(InputRequest inputRequest) {
		switch (inputRequest.getUrlPath()) {
		case Constants.GET_SLEEP_REQUEST:
			Map<String, String> methodParams = inputRequest.getUrlParams();
			System.out.println("methodParams:" + methodParams.toString());
			if (!methodParams.containsKey(Constants.CONNECTION_ID)) {
				System.out.println("False due to ConnId Missing");
				return false;
			}
			if (!methodParams.containsKey(Constants.TIMEOUT)) {
				System.out.println("False due to Timeout Missing");
				return false;
			}
			return true;
		case Constants.GET_SERVER_STATUS_REQUEST:
			return true;
		case Constants.REQUEST_KILL:
			return true;
		default:
			System.out.println("Invalid get request URL");
			return false;
		}

	}

}
