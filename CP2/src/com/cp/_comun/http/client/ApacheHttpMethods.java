package com.cp._comun.http.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.cp._comun.StExcepcion;

class ApacheHttpMethods implements IHttpMethods {
	
	private boolean isTraza = false; 
	
	private final String encodeCharset;
	
	public ApacheHttpMethods(String charset) {
		encodeCharset = charset;
	}
	
	public ApacheHttpMethods(String charset,boolean isTraza) {
		encodeCharset = charset;
		this.isTraza = isTraza;
	}

	public String doPut(String url, Map<String, String> params, Map<String, String> headers, String payload)
			throws StExcepcion {
		String result = null;
		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut httpPut = null;

		if (url==null || url.trim().length()<1) {
			return result;
		}
		
		//Agregar parametros a la url
		url = url + this.createQuery(params);


		try {
			httpPut = new HttpPut(url);
			//Agregar cabeceras
			if (headers!=null) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPut.addHeader(entry.getKey(), entry.getValue());
				}
			}
			
			if (payload!=null && payload.trim().length()>0) {
				StringEntity payloadEntity = new StringEntity(payload, this.encodeCharset);
				httpPut.setEntity(payloadEntity);
			}
		} catch (UnsupportedCharsetException e) {
			httpPut = null;
			close(httpClient);
			throw new StExcepcion(e.getMessage());
		} catch (IllegalArgumentException e) {
			close(httpClient);
			throw new StExcepcion(e.getMessage());
		}

		if (httpPut != null) {
			try {
				httpResponse = httpClient.execute(httpPut);
			} catch (ClientProtocolException e) {
				httpPut = null;
				close(httpClient);
				throw new StExcepcion(e.getMessage());
			} catch (IOException e) {
				httpPut = null;
				close(httpClient);
				throw new StExcepcion(e.getMessage());
			}
		}

		if (httpResponse != null) {
			result = "" + httpResponse.getStatusLine().getStatusCode();

			StringBuffer bodyContent = new StringBuffer();
			BufferedReader rd = null;
			InputStreamReader ir = null;
			if (httpResponse.getEntity() != null) {
				try {
					ir = new InputStreamReader(httpResponse.getEntity().getContent());
					rd = new BufferedReader(ir);

					String line = "";
					while ((line = rd.readLine()) != null) {
						bodyContent.append(line);
					}
				} catch (UnsupportedOperationException e) {
					throw new StExcepcion(e.getMessage());
				} catch (IOException e) {
					throw new StExcepcion(e.getMessage());
				} finally {
					close(rd);
					close(ir);
					close(httpResponse);
					close(httpClient);
					httpPut = null;
				}
			}

			result += "^" + bodyContent.toString();
		}

		return result;
	}

	public String doGet(String url, Map<String, String> params, Map<String, String> headers)
			throws StExcepcion {
		String result = "";
		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = null;
		
		if (url==null || url.trim().length()<1) {
			return result;
		}
		
		//Agregar parametros a la url
		url = url + this.createQuery(params);

		try {
			httpGet = new HttpGet(url);
			//Agregar cabeceras
			if (headers!=null) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
		} catch (IllegalArgumentException e) {
			close(httpClient);
			throw new StExcepcion(e.getMessage());
		}

		if (httpGet != null) {
			try {
				if (isTraza) { System.out.println( "\n>> httpClient.execute(httpGet) : " + httpGet.toString() ); }
				httpResponse = httpClient.execute(httpGet);
				if (isTraza) { System.out.println( "\n<< httpClient.execute(httpGet) : " + httpResponse ); }
			} catch (ClientProtocolException e) {
				httpGet = null;
				close(httpClient);
				throw new StExcepcion(e.getMessage());
			} catch (IOException e) {
				httpGet = null;
				close(httpClient);
				throw new StExcepcion(e.getMessage());
			}
		}

		if (httpResponse != null) {
			result = "" + httpResponse.getStatusLine().getStatusCode();

			StringBuffer bodyContent = new StringBuffer();
			BufferedReader rd = null;
			InputStreamReader ir = null;
			if (httpResponse.getEntity() != null) {
				try {
					ir = new InputStreamReader(httpResponse.getEntity()
							.getContent());
					rd = new BufferedReader(ir);

					String line = "";
					while ((line = rd.readLine()) != null) {
						bodyContent.append(line);
					}
				} catch (UnsupportedOperationException e) {
					throw new StExcepcion(e.getMessage());
				} catch (IOException e) {
					throw new StExcepcion(e.getMessage());
				} finally {
					close(rd);
					close(ir);
					close(httpResponse);
					close(httpClient);
					httpGet = null;
				}
			}

			result += "^" + bodyContent.toString();
		}

		return result;
	}

	protected String createQuery(Map<String, String> params) throws StExcepcion {
		if (params == null) {
			return "";
		}
		try {
			StringBuffer query = new StringBuffer();
			for (Entry<String, String> entry : params.entrySet()) {
				if (query.length() > 0) {
					query.append("&");
				}
				if (query.length() == 0) {
					query.append("?");
				}
				String encodedKey = URLEncoder.encode(entry.getKey(), this.encodeCharset);
				String encodedValue = URLEncoder.encode(entry.getValue(), this.encodeCharset);
				query.append(String.format("%s=%s", encodedKey, encodedValue));
			}
			return query.toString();
		} catch (UnsupportedEncodingException ex) {
			throw new StExcepcion("Error encoding params " + params.toString());
		}
	}

	private void close(Closeable object) {
		try {
			object.close();
		} catch (IOException e) {
		}
		object = null;

	}


	public String doPost(String url, List<NameValuePair> params, Map<String, String> headers, String payload) throws StExcepcion {
		return this.doPost(new DefaultHttpClient(), url, params, headers, payload);
	}


	public String doPost(IHttpClient httpClientBuilder, String url, List<NameValuePair> params, Map<String, String> headers, String payload) throws StExcepcion {
		String result = null;
		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = httpClientBuilder.getClient();
		HttpPost httpPost = null;

		if (url==null || url.trim().length()<1) {
			return result;
		}
		
		try {
			httpPost = new HttpPost(url);
			//Agregar cabeceras
			if (headers!=null) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			
			if (params!=null && params.size()>0) {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, this.encodeCharset);
				httpPost.setEntity(formEntity);
			}
			
			if (payload!=null && payload.trim().length()>0) {
				StringEntity payloadEntity = new StringEntity(payload, this.encodeCharset);
				httpPost.setEntity(payloadEntity);
			}
		} catch (UnsupportedCharsetException e) {
			httpPost = null;
			close(httpClient);
			throw new StExcepcion(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			httpPost = null;
			close(httpClient);
			throw new StExcepcion(e.getMessage());
		} catch (IllegalArgumentException e) {
			close(httpClient);
			throw new StExcepcion(e.getMessage());
		}

		if (httpPost != null) {
			try {
				if (isTraza) { System.out.println( "\n>> httpClient.execute(httpPost) : " + httpPost.toString() ); }
				httpResponse = httpClient.execute(httpPost);
				if (isTraza) { System.out.println( "\n<< httpClient.execute(httpPost) : " + httpResponse ); }
			} catch (ClientProtocolException e) {
				httpPost = null;
				close(httpClient);
				throw new StExcepcion(e.getMessage());
			} catch (IOException e) {
				httpPost = null;
				close(httpClient);
				throw new StExcepcion(e.getMessage());
			}
		}

		if (httpResponse != null) {
			result = "" + httpResponse.getStatusLine().getStatusCode();

			StringBuffer bodyContent = new StringBuffer();
			BufferedReader rd = null;
			InputStreamReader ir = null;
			if (httpResponse.getEntity() != null) {
				try {
					ir = new InputStreamReader(httpResponse.getEntity()
							.getContent());
					rd = new BufferedReader(ir);

					String line = "";
					while ((line = rd.readLine()) != null) {
						bodyContent.append(line);
					}
				} catch (UnsupportedOperationException e) {
					throw new StExcepcion(e.getMessage());
				} catch (IOException e) {
					throw new StExcepcion(e.getMessage());
				} finally {
					close(rd);
					close(ir);
					close(httpResponse);
					close(httpClient);
					httpPost = null;
				}
			}

			result += "^" + bodyContent.toString();
		}

		return result;
	}


}
