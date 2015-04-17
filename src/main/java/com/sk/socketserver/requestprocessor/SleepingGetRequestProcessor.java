package com.sk.socketserver.requestprocessor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.request.Request;
import com.sk.socketserver.request.RequestMap;
import com.sk.socketserver.util.Constants;

public class SleepingGetRequestProcessor implements RequestProcessor {

	private InputRequest inputRequest;
	private OutputStream outputStream;
	private Gson GSON;

	public SleepingGetRequestProcessor(InputRequest inputRequest,
			OutputStream outputStream) {
		super();
		this.inputRequest = inputRequest;
		this.outputStream = outputStream;
		GSON = new Gson();
	}

	@Override
	public void process() {

		try {
			boolean isValid = validateRequest(inputRequest);
			if (!isValid) {
				outputStream.write("Invalid Request".getBytes());
				outputStream.close();
				return;
			}
			switch (inputRequest.getUrlPath()) {
			case Constants.GET_SLEEP_REQUEST:
				executeAPIRequest(inputRequest, outputStream);
				break;
			case Constants.GET_SERVER_STATUS_REQUEST:
				executeServerStatusRequest(outputStream);
				break;
			case Constants.REQUEST_KILL:
				killRequest(Integer.valueOf(inputRequest.getUrlParams().get(
						"connid")));
				break;
			default:
				System.out.println("Default in GETProcessor.process. breaking");
				outputStream.write("Invalid Request".getBytes());
			}
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void killRequest(int connId) throws IOException {
		String connectinId = String.valueOf(connId);
		if (RequestMap.get(connectinId) != null) {
			System.out.println("Found the thread");
			Request request = RequestMap.get(String.valueOf(connId));
			request.getThread().interrupt();
			RequestMap.remove(connectinId);
			outputStream.write(Constants.STATUS_OK.getBytes());
			outputStream.close();
		} else {
			System.out.println("Invalid ID");
			String output = Constants.INVALID_CONNECTION_ID;
			output = output.replace("<connId>", connectinId);
			outputStream.write(output.getBytes());
			outputStream.close();
		}

	}

	private void executeServerStatusRequest(OutputStream outputStream) {
		long currentTime = System.currentTimeMillis();
		Set<Entry<String, Request>> entrySet = RequestMap.entrySet();
		Map<String, String> map = new HashMap<String, String>();
		for (Entry<String, Request> entry : entrySet) {
			String connId = entry.getKey();
			Request request = entry.getValue();
			long remainingTime = ((request.getExpiryTime() - currentTime) / 1000);
			map.put(connId, String.valueOf(remainingTime));
		}
		try {
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

	private void executeAPIRequest(InputRequest inputRequest,
			OutputStream outputStream) {

		long currentTime = System.currentTimeMillis();
		Map<String, String> params = inputRequest.getUrlParams();
		long expTime = Long.valueOf(params.get(Constants.TIMEOUT)) * 1000;
		long expiryTime = currentTime + expTime;

		String connectionId = params.get(Constants.CONNECTION_ID);
		Request request = new Request(connectionId, Thread.currentThread(),
				expiryTime);

		RequestMap.put(connectionId, request);
		try {
			Thread.sleep(expTime);
			try {
				outputStream.write(Constants.STATUS_OK.getBytes());

			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			// Thread interupted. moving ahead
			System.out.println("Interupted");
			try {
				outputStream.write(Constants.STATUS_KILLED.getBytes());

			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					outputStream.close();
				} catch (IOException e3) {
					e.printStackTrace();
				}
			}
		}
		RequestMap.remove(connectionId);
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
