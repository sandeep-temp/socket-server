package com.sk.socketserver.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class InputRequest {

	private MethodType methodType;
	private String urlPath;
	private Map<String, String> urlParams;
	private String putBody;

	public MethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(MethodType methodType) {
		this.methodType = methodType;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public Map<String, String> getUrlParams() {
		return urlParams;
	}

	public void setUrlParams(Map<String, String> urlParams) {
		this.urlParams = urlParams;
	}

	public void parseUrl(String url) {

		try {
			URI uri = new URI(url);
			System.out.println("Host:" + uri.getHost());
			System.out.println("PAth:" + uri.getPath());
			System.out.println("Query:" + uri.getQuery());

			this.urlPath = uri.getPath();
			String query = uri.getQuery();
			if (query != null) {
				String[] params = query.split("&");
				for (String param : params) {
					if (param != null && param.length() != 0) {
						String[] keyValPair = param.split("=");
						if (keyValPair[0] != null
								&& keyValPair[0].length() != 0
								&& keyValPair[1] != null
								&& keyValPair[1].length() != 0) {
							urlParams.put(keyValPair[0], keyValPair[1]);
						}
					}
				}
			}
			System.out.println("urlParams:" + urlParams);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	public InputRequest() {
		urlParams = new HashMap<String, String>();
	}

	public String getPutBody() {
		return putBody;
	}

	public void setPutBody(String putBody) {
		this.putBody = putBody;
	}

}
