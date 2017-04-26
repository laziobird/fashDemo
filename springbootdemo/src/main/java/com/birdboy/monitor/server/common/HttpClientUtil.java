package com.birdboy.monitor.server.common;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * http连接池控制
 * 
 */
public class HttpClientUtil {
	
    static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);	

	static final int TIMEOUT = 5 * 1000;

	private static CloseableHttpClient httpClient = null;

	private final static Object syncLock = new Object();

	
		/**
		 * 设置Header等 httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
		 * httpRequestBase.setHeader("Accept-Language",
		 * "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
		 * httpRequestBase.setHeader("Accept-Charset",
		 * "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");
		 **/

		
		// 配置请求的超时设置
		// connectionRequestTimeout  --  从池中获取连接超时时间 建议设置短
	static RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(1000)
				.setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build();

	

	/**
	 * 获取HttpClient对象
	 * maxTotal:总连接数
	 * maxPerRoute:每个目标服务器平均连接数
	 * maxRoute:目标服务器最大最大连接数
	 */
	static final int maxTotal = 1000;
	static final int maxPerRoute = 50;
	static final int maxRoute = 100;
	public static CloseableHttpClient getHttpClient(String url) {
		String hostname = url.split("/")[2];
		int port = 80;
		if (hostname.contains(":")) {
			String[] arr = hostname.split(":");
			hostname = arr[0];
			port = Integer.parseInt(arr[1]);
		}
		if (httpClient == null) {
			synchronized (syncLock) {
				if (httpClient == null) {
					httpClient = createHttpClient(maxTotal, maxPerRoute, maxRoute, hostname, port);
				}
			}
		}
		return httpClient;
	}

	/**
	 * 创建HttpClient对象
	 * 
	 * @return
	 * @author SHANHY
	 * @create 2015年12月18日
	 */
	public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String hostname,
			int port) {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", plainsf).register("https", sslsf).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加
		cm.setMaxTotal(maxTotal);
		// 将每个路由基础的连接增加
		cm.setDefaultMaxPerRoute(maxPerRoute);
		HttpHost httpHost = new HttpHost(hostname, port);
		// 将目标主机的最大连接数增加
		cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= 5) {// 如果已经重试了5次，就放弃
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					return false;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					return false;
				}
				if (exception instanceof SSLException) {// SSL握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};

		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
				.setRetryHandler(httpRequestRetryHandler).setDefaultRequestConfig(requestConfig).build();

		return httpClient;
	}

	private static void setPostParams(HttpPost httpost, Map<String, Object> params) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
		}
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String post(String url, Map<String, Object> params) throws IOException {
		HttpPost httppost = new HttpPost(url);
		setPostParams(httppost, params);
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			System.out.println("url:"+url+" | http code:"+response.getStatusLine().getStatusCode());
			logger.info("url:"+url+" | http code:"+response.getStatusLine().getStatusCode());
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			logger.error("url:"+url+" | error: "+e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 支持post 参数 json
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, String json) throws IOException {
		HttpPost httppost = new HttpPost(url);
		StringEntity s = new StringEntity(json.toString(),"UTF-8");
	    //s.setContentEncoding("UTF-8");
	    s.setContentType("application/json");
	    //发送json数据需要设置contentType
	    httppost.setEntity(s);
	   
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, "utf-8");
			System.out.println("url:"+url+" | http code:"+response.getStatusLine().getStatusCode());
			logger.info("url:"+url+" | http code:"+response.getStatusLine().getStatusCode());
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			logger.error("url:"+url+" | error: "+e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	
	
	
	

	public static String get(String url) {
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient(url).execute(httpget, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			System.out.println("url:"+url+" | http code:"+response.getStatusLine().getStatusCode());
			String result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String[] urisToGet = new String[500];
		for (int i = 2000; i < 2500; i++) {
			urisToGet[i-2000] = "http://product.auto.163.com/series/"+i+".html";
		}	

		long start = System.currentTimeMillis();
		try {
			int pagecount = urisToGet.length;
			ExecutorService executors = Executors.newFixedThreadPool(50);
			CountDownLatch countDownLatch = new CountDownLatch(pagecount);
			for (int i = 0; i < pagecount; i++) {
				
				// 启动线程抓取
				executors.execute(new GetRunnable(urisToGet[i], countDownLatch));
			}
			countDownLatch.await();
			executors.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(
					"线程" + Thread.currentThread().getName() + "," + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
		}

		long end = System.currentTimeMillis();
		System.out.println("consume -> " + (end - start));
	}

	static class GetRunnable implements Runnable {
		private CountDownLatch countDownLatch;
		private String url;

		public GetRunnable(String url, CountDownLatch countDownLatch) {
			this.url = url;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			try {
				System.out.println(countDownLatch+"------->"+HttpClientUtil.get(url).length());
			} finally {
				countDownLatch.countDown();
			}
		}
	}
}
