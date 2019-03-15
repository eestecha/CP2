package com.cp._comun.http.client.test;

import java.util.HashMap;
import java.util.Map;

import com.cp._comun.StExcepcion;
import com.cp._comun.Subrutinas;
import com.cp._comun.http.client.IHttpMethods;
import com.cp._comun.http.client.ProxyHttpMethods;

import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		String stringToEncrypt = Test.testGetAuthenticationToken();
		
		System.out.println(stringToEncrypt);
	
		JSONObject jObject = JSONObject.fromObject(stringToEncrypt);
		
		String token = (String) jObject.get("token");
		
		int count = 0;
		String hashcash = "1:16:" + token + ":";
		
		while ( !(Subrutinas.getHashMD5FromString(hashcash + count).substring(0, 4).equalsIgnoreCase("0000"))) {
			count++;
		}
		
		String authToken = Subrutinas.getHashMD5FromString(hashcash + count);
		System.out.println(authToken);
		String respSignup = testDoSignup(authToken);
		System.out.println(respSignup);
		String respSignin = testDoSignin(authToken);
		System.out.println(respSignin);
		
	}
	
	/*
	 * 
	 * 200^{"code":200,"token":"d079f374a55cda73e94c484188db60b3","message":"Success"}
	 */
	private static String testGetAuthenticationToken() {
		final String secretKey = "55bc85c634212f790178c507d7d22a89bf6d8dacc7f2f6454c9d642b";
		final String urlDomain = "https://www.spotcap.es";
		final String endpoint = "/signup/partner-token";
		
		String charset = "UTF-8";
		String url = urlDomain + endpoint;
		Map<String,String> headers = new HashMap<String, String>();
		String resp = "";
		
		headers.put("secretkey", secretKey);
		
		IHttpMethods httpMethods = new ProxyHttpMethods(charset);
		try {
			resp = httpMethods.doGet(url,null,headers);
		} catch (StExcepcion e) {
			e.printStackTrace();
		}
		httpMethods = null;
		System.out.println(resp);
		
		String[] resParts = resp.split("\\^");
		
		if (resParts!=null && resParts.length>1) {
			return resParts[1];
		} 
		return "";
		
	}
	
	private static String testDoSignup(String authToken) {
		final String secretKey = "55bc85c634212f790178c507d7d22a89bf6d8dacc7f2f6454c9d642b";
		final String urlDomain = "https://www.spotcap.es";
		final String endpoint = "/signup/create";
		
		String charset = "UTF-8";
		String url = urlDomain + endpoint;
		Map<String,String> params = new HashMap<String, String>();
		Map<String,String> headers = new HashMap<String, String>();
		String resp = "";
		
		headers.put("secretkey", secretKey);
		headers.put("authtoken", authToken);
		
		params.put("email", "pepito@spotcap.com");
		params.put("password", "billin");
		params.put("newsletter", "1");
		params.put("termsAndConditionsAndPrivacyPolicy", "1");
		params.put("isPartner", "1");
		params.put("countrycode", "es");

		IHttpMethods httpMethods = new ProxyHttpMethods(charset);
		try {
			resp = httpMethods.doPost(url,null,headers,null);
		} catch (StExcepcion e) {
			e.printStackTrace();
		}
		httpMethods = null;
		System.out.println(resp);
		
		String[] resParts = resp.split("\\^");
		
		if (resParts!=null && resParts.length>1) {
			return resParts[1];
		} 
		return "";
		
	}


	private static String testDoSignin(String authToken) {
		final String secretKey = "55bc85c634212f790178c507d7d22a89bf6d8dacc7f2f6454c9d642b";
		final String urlDomain = "https://www.spotcap.es";
		final String endpoint = "/authenticate/authorize";
		
		String charset = "UTF-8";
		String url = urlDomain + endpoint;
		Map<String,String> params = new HashMap<String, String>();
		Map<String,String> headers = new HashMap<String, String>();
		String resp = "";
		
		headers.put("secretkey", secretKey);
		headers.put("authtoken", authToken);
		
		params.put("email", "pepito@spotcap.com");
		params.put("password", "billin");
		params.put("countrycode", "es");

		IHttpMethods httpMethods = new ProxyHttpMethods(charset);
		try {
			resp = httpMethods.doPost(url,null,headers,null);
		} catch (StExcepcion e) {
			e.printStackTrace();
		}
		httpMethods = null;
		System.out.println(resp);
		
		String[] resParts = resp.split("\\^");
		
		if (resParts!=null && resParts.length>1) {
			return resParts[1];
		} 
		return "";
		
	}
}
