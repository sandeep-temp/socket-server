package com.sk.socketserver.requestprocessor;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.request.Request;
import com.sk.socketserver.request.RequestMap;
import com.sk.socketserver.util.Constants;

public class SleepingPutRequestProcessor implements RequestProcessor {

	private InputRequest inputRequest;
	private OutputStream outputStream;
	private Gson GSON;

	public SleepingPutRequestProcessor(InputRequest inputRequest,
			OutputStream outputStream) {
		super();
		this.inputRequest = inputRequest;
		this.outputStream = outputStream;
		GSON = new Gson();
	}

	@Override
	public void process() {

		try {
			switch (inputRequest.getUrlPath()) {
			case Constants.REQUEST_KILL:
				KillPostRequest killPostRequest = getKillRequest();
				System.out.println("Killing:" + killPostRequest.getConnId());
				killRequest(killPostRequest.getConnId());
				break;
			default:
				outputStream.write("Invalid Request".getBytes());
			}
		} catch (IOException e) {
			System.out.println("Caught IOException");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Caught Exception");
			e.printStackTrace();
		} finally {
		}
	}

	private KillPostRequest getKillRequest() {
		return GSON.fromJson(inputRequest.getPutBody(),
				KillPostRequest.class);
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
			System.out.println("Wrote OK to outputstream");
		} else {
			System.out.println("Invalid ID");
			String output = Constants.INVALID_CONNECTION_ID;
			output = output.replace("<connId>", connectinId);
			outputStream.write(output.getBytes());
			outputStream.close();
		}

	}

	class KillPostRequest {
		private int connId;

		public KillPostRequest(int connId) {
			super();
			this.connId = connId;
		}

		public int getConnId() {
			return connId;
		}

		public void setConnId(int connId) {
			this.connId = connId;
		}

	}
}
