package com.sk.socketserver.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMap {

	private static Map<String, Request> connectionIdToRequestMap = new ConcurrentHashMap<String, Request>();
	private static Map<Long, List<String>> expiryTimeToRequestIdsMap = new ConcurrentHashMap<Long, List<String>>();

	private RequestMap() {
		connectionIdToRequestMap = new ConcurrentHashMap<String, Request>();
	}

	public static Request get(String i) {
		return connectionIdToRequestMap.get(i);
	}

	public static Request put(String i, Request request) {
		return connectionIdToRequestMap.put(i, request);
	}

	public static Set<Entry<String, Request>> entrySet() {
		return connectionIdToRequestMap.entrySet();
	}

	public static Request remove(String i) {
		return connectionIdToRequestMap.remove(i);
	}

	public static void removeFromExpiryTimeMap(Long expiryTime) {
		 expiryTimeToRequestIdsMap.remove(expiryTime);
	}

	public static List<String> getConnectionIdsToKillByTime(Long time) {

		if (expiryTimeToRequestIdsMap.containsKey(time)) {
			return expiryTimeToRequestIdsMap.get(time);
		}
		return Collections.emptyList();

	}

	public static void addConnectionIdForExpiryTime(Long time,
			String connectionId) {
		if (expiryTimeToRequestIdsMap.containsKey(time)) {
			expiryTimeToRequestIdsMap.get(time).add(connectionId);
		} else {
			expiryTimeToRequestIdsMap.put(time,
					new ArrayList<String>(Arrays.asList(connectionId)));
		}
	}

	public static void removeConnectionIdFromExpiryTimeMap(String connectinId,
			Long expiryTime) {
		if (expiryTimeToRequestIdsMap.containsKey(expiryTime)) {
			expiryTimeToRequestIdsMap.get(expiryTime).remove(connectinId);
		}
	}
}
