package com.sk.socketserver.requestprocessor;

import java.io.IOException;
import java.net.Socket;

import com.sk.socketserver.request.InputRequest;
import com.sk.socketserver.request.Request;
import com.sk.socketserver.request.RequestMap;
import com.sk.socketserver.requestprocessor.SleepingPutRequestProcessor.KillPostRequest;
import com.sk.socketserver.util.Constants;

public class InMemoryPutRequestProcessor implements RequestProcessor{

	private Socket socket;

	private InputRequest inputRequest;
	
	public InMemoryPutRequestProcessor(Socket socket, InputRequest inputRequest) {
		super();
		this.socket = socket;
		this.inputRequest = inputRequest;
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
				socket.getOutputStream().write("Invalid Request".getBytes());
				socket.getOutputStream().close();
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
	
	private void killRequest(int connId) throws IOException {
		String connectinId = String.valueOf(connId);
		if (RequestMap.get(connectinId) != null) {
			System.out.println("Found the thread");
			Request request = RequestMap.get(String.valueOf(connId));
			long expiryTime = request.getExpiryTime();
			RequestMap.removeConnectionIdFromExpiryTimeMap(connectinId, expiryTime);
			RequestMap.remove(connectinId);
			socket.getOutputStream().write(Constants.STATUS_OK.getBytes());
			socket.getOutputStream().flush();
			socket.getOutputStream().close();
			
			System.out.println("Wrote OK to outputstream");
			request.getSocket().getOutputStream().write(Constants.STATUS_KILLED.getBytes());
			request.getSocket().getOutputStream().close();
			System.out.println("Wrote Killed to outputstream");
			
		} else {
			System.out.println("Invalid ID");
			String output = Constants.INVALID_CONNECTION_ID;
			output = output.replace("<connId>", connectinId);
			socket.getOutputStream().write(output.getBytes());
			socket.getOutputStream().close();
		}

	}

	private KillPostRequest getKillRequest() {
		return Constants.GSON.fromJson(inputRequest.getPutBody(),
				KillPostRequest.class);
	}

}
