package com.cp._comun.http.client;


import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.cp._comun.StExcepcion;

/**
 * Clase proxy para la ejecucion de peticiones HTTP.
 * 
 * Ejemplo de uso:
 * {@code 
 		IHttpMethods httpMethods = new ProxyHttpMethods("ISO-8859-1");
		String respGet = httpMethods.doGet(urlGet);
		String respPost = httpMethods.doPost(urlPost);
		httpMethods = null;
	}
 *
 */
public class ProxyHttpMethods implements IHttpMethods {

	private IHttpMethods realObject = null;
	
	/**
	 * 
	 * @param charset Valor que determina que charset se utilizara en los encodes.
	 * 				Por ejemplo: "ISO-8859-1" , "UTF-8"
	 */
	public ProxyHttpMethods(String charset) {
		realObject = new ApacheHttpMethods(charset);
	}
	public ProxyHttpMethods(String charset, boolean isTraza) {
		realObject = new ApacheHttpMethods(charset,isTraza);
	}

	public String doPut(String url, Map<String, String> params, Map<String, String> headers, String payload) throws StExcepcion {
		return realObject.doPut(url, params, headers, payload);
	}

	public String doGet(String url, Map<String, String> params, Map<String, String> headers) throws StExcepcion {
		return realObject.doGet(url, params, headers);
	}

	@Override
	public String doPost(String url, List<NameValuePair> params, Map<String, String> headers, String payload) throws StExcepcion {
		return realObject.doPost(url, params, headers, payload);
	}
	@Override
	public String doPost(IHttpClient httpClientBuilder, String url, List<NameValuePair> params, Map<String, String> headers, String payload) throws StExcepcion {
		return realObject.doPost(httpClientBuilder, url, params, headers, payload);
	}

}
