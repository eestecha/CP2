package com.cp._comun.http.client;


import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.cp._comun.StExcepcion;


/* //Ejemplo de Get
String charset = "UFT-8";
String url = "http://httpbin.org/ip";
String resp = "";

IHttpMethods httpMethods = new ProxyHttpMethods(charset);
try {
	resp = httpMethods.doGet(url,null,null);
} catch (StExcepcion e) {
	e.printStackTrace();
}
httpMethods = null;
System.out.println(resp);
*/

/*
* 
* //Ejemplo de Get con parametros
* 
		//https://httpbin.org/get?show_env=1
	String charset = "UTF-8";
	String url = "https://httpbin.org/get";
	Map<String,String> params = new HashMap<String, String>();
	String resp = "";
	
	params.put("show_env", "1");
	
	IHttpMethods httpMethods = new ProxyHttpMethods(charset);
	try {
		resp = httpMethods.doGet(url,params,null);
	} catch (StExcepcion e) {
		e.printStackTrace();
	}
	httpMethods = null;
	System.out.println(resp);

*/

/*
* Ejemplo de uso de POST con parametros
* 
		//https://httpbin.org/post?show_env=1
	String charset = "UTF-8";
	String url = "https://httpbin.org/post";
	Map<String,String> params = new HashMap<String, String>();
	String resp = "";
	
	params.put("show_env", "1");
	
	IHttpMethods httpMethods = new ProxyHttpMethods(charset);
	try {
		resp = httpMethods.doPost(url,params,null,null);
	} catch (StExcepcion e) {
		e.printStackTrace();
	}
	httpMethods = null;
	System.out.println(resp);

*/

/* 
* Ejemplo de uso de POST con payload
* 
	//https://httpbin.org/post
	String charset = "UTF-8";
	String url = "https://httpbin.org/post";
	String payload = "{ \"customer_bank_accounts\": { \"account_number\": \"55779911\" }";
	String resp = "";
	
	IHttpMethods httpMethods = new ProxyHttpMethods(charset);
	try {
		resp = httpMethods.doPost(url,null,null,payload);
	} catch (StExcepcion e) {
		e.printStackTrace();
	}
	httpMethods = null;
	System.out.println(resp);
* 
*/
/**
* Esta interface define las funciones basicas para realizar peticiones HTTP.
* 
* 
* @see ProxyHttpMethods
*
*/
public interface IHttpMethods {
	/**
	 * Para hacer una peticion HTTP POST
	 * @param url Contiene la url completa a donde realizar la peticion
	 * @param params Conjunto de pares<Clave,Valor> que agregaran a la url
	 * @param headers Conjunto de pares<Clave,Valor> que iran en la cabecera de la peticion
	 * @param payload El valor de este parametro sera el contenido de la peticion
	 * @return Devuelve '{CODIGO DE LA RESPUESTA}^{CONTENIDO DE LA RESPUESTA}'
	 * @throws StExcepcion 
	 * 			* Si el parametro url no es una direccion valida.
	 * 			* Si ocurre algun error en el protocolo.
	 * 			* Si ocurre algun error al recuperar el contenido de la respuesta.
	 */
	public String doPost(String url, List<NameValuePair> params, Map<String, String> headers, String payload) throws StExcepcion;

	/**
	 * Para hacer una peticion HTTP POST
	 * @param httpClientBuilder instancia de una clase que implemente la interfaz {@link IHttpClient}
	 * @param url Contiene la url completa a donde realizar la peticion
	 * @param params Conjunto de pares<Clave,Valor> que agregaran a la url
	 * @param headers Conjunto de pares<Clave,Valor> que iran en la cabecera de la peticion
	 * @param payload El valor de este parametro sera el contenido de la peticion
	 * @return Devuelve '{CODIGO DE LA RESPUESTA}^{CONTENIDO DE LA RESPUESTA}'
	 * @throws StExcepcion 
	 * 			* Si el parametro url no es una direccion valida.
	 * 			* Si ocurre algun error en el protocolo.
	 * 			* Si ocurre algun error al recuperar el contenido de la respuesta.
	 */
	public String doPost(IHttpClient httpClientBuilder, String url, List<NameValuePair> params, Map<String, String> headers, String payload) throws StExcepcion;
	/**
	 * Para hacer una peticion HTTP PUT
	 * @param url Contiene la url completa a donde realizar la peticion
	 * @param params Conjunto de pares<Clave,Valor> que agregaran a la url
	 * @param headers Conjunto de pares<Clave,Valor> que iran en la cabecera de la peticion
	 * @param payload El valor de este parametro sera el contenido de la peticion
	 * @return Devuelve '{CODIGO DE LA RESPUESTA}^{CONTENIDO DE LA RESPUESTA}'
	 * @throws StExcepcion 
	 * 			* Si el parametro url no es una direccion valida.
	 * 			* Si ocurre algun error en el protocolo.
	 * 			* Si ocurre algun error al recuperar el contenido de la respuesta.
	 */
	public String doPut(String url, Map<String, String> params, Map<String, String> headers, String payload) throws StExcepcion;
	/**
	 * Para hacer una peticion HTTP GET
	 * @param url Contiene la url completa a donde realizar la peticion
	 * @param params Conjunto de pares<Clave,Valor> que agregaran a la url
	 * @param headers Conjunto de pares<Clave,Valor> que iran en la cabecera de la peticion
	 * @return Devuelve '{CODIGO DE LA RESPUESTA}^{CONTENIDO DE LA RESPUESTA}'
	 * @throws StExcepcion 
	 * 			* Si el parametro url no es una direccion valida.
	 * 			* Si ocurre algun error en el protocolo.
	 * 			* Si ocurre algun error al recuperar el contenido de la respuesta.
	 */
	public String doGet(String url, Map<String, String> params, Map<String, String> headers) throws StExcepcion;
}
