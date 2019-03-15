package com.cp._comun.http.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DefaultHttpClient implements IHttpClient {

	public CloseableHttpClient getClient() {
		return HttpClients.createDefault();
	}

}
