package com.sk.socketserver;

import java.io.IOException;
import java.util.List;

import com.sk.socketserver.request.Request;
import com.sk.socketserver.request.RequestMap;
import com.sk.socketserver.util.Constants;

public class EvictionProcess implements Runnable {

	private Long lastRunSeconds;

	public EvictionProcess() {
		super();
		this.lastRunSeconds = System.currentTimeMillis() / 1000;
	}

	@Override
	public void run() {

		while (true) {
			cleanUp();
		}

	}

	private void cleanUp() {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long lastRunSec = lastRunSeconds.longValue();

		while (lastRunSec < currentSeconds) {
			List<String> connectionsToEnd = RequestMap
					.getConnectionIdsToKillByTime(lastRunSec);
			endConnections(connectionsToEnd);
			RequestMap.removeFromExpiryTimeMap(lastRunSec);
			lastRunSec++;
			currentSeconds = System.currentTimeMillis() / 1000;
		}
		lastRunSeconds = currentSeconds;
		long currentTime = System.currentTimeMillis();
		long sleepTime = currentTime % 1000;
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// Do Nothing. Proceed
		}
	}

	private void endConnections(List<String> connectionsToEnd) {
		if (connectionsToEnd != null && connectionsToEnd.size() != 0) {
			for (String connectionId : connectionsToEnd) {
				endConnection(connectionId);
			}
		}

	}

	private void endConnection(String connectionId) {
		Request request = RequestMap.get(connectionId);
		try {
			if (request == null) {
				return;
			}
			request.getSocket().getOutputStream()
					.write(Constants.STATUS_OK.getBytes());

			RequestMap.remove(connectionId);
			request.getSocket().getOutputStream().close();
		} catch (IOException e) {
			// Lets not do anything for now and assume things will work
		}

	}

}
