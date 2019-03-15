package com.cp._comun.http.client;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TLSHttpClient implements IHttpClient {

	private final String tlsVersion;
	
	public TLSHttpClient(String tlsVersion) {
		this.tlsVersion = tlsVersion;
	}
	
	public CloseableHttpClient getClient() {
	    SSLContext context = null;
		try {
			context = SSLContext.getInstance(this.tlsVersion);
		    context.init(null,null,null);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return HttpClients.custom().setSslcontext(context).build();
	}

}
