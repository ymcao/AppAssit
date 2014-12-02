/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appassit.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

/**
 * Subclass of the Apache {@link DefaultHttpClient} that is configured with reasonable default settings and registered schemes for Android, and also lets the user add {@link HttpRequestInterceptor} classes. Don't create this directly, use the {@link #newInstance} factory method.
 * 
 * <p>
 * This client processes cookies but does not retain them by default. To retain cookies, simply add a cookie store to the HttpContext:
 * </p>
 * 
 * <pre>
 * context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
 * </pre>
 */
public final class AndroidHttpClient implements HttpClient {

	// Gzip of data shorter than this probably won't be worthwhile
	public static long DEFAULT_SYNC_MIN_GZIP_BYTES = 256;

	private static final String TAG = "AndroidHttpClient";

	private boolean mIsLoadCookies = false;

	/** Interceptor throws an exception if the executing thread is blocked */
	private static final HttpRequestInterceptor sThreadCheckInterceptor = new HttpRequestInterceptor() {
		@Override
		public void process(HttpRequest request, HttpContext context) {
			// Prevent the HttpRequest from being sent on the main thread
			if (Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
				throw new RuntimeException("This thread forbids HTTP requests");
			}
		}
	};

	/**
	 * Create a new HttpClient with reasonable defaults (which you can update).
	 * 
	 * @param userAgent
	 *            to report in your HTTP requests
	 * @param context
	 *            to use for caching SSL sessions (may be null for no caching)
	 * @return AndroidHttpClient for you to use for all your requests.
	 */
	public static AndroidHttpClient newInstance(String userAgent, Context context) {
		HttpParams params = new BasicHttpParams();
		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 20 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Increase max total connection to 60
		ConnManagerParams.setMaxTotalConnections(params, 60);
		// Increase default max connection per route to 20
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
		// Increase max connections for localhost:80 to 20
		HttpHost localhost = new HttpHost("locahost", 80);
		connPerRoute.setMaxForRoute(new HttpRoute(localhost), 20);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

		// Don't handle redirects -- return them to the caller. Our code
		// often wants to re-POST after a redirect, which we must do ourselves.
		HttpClientParams.setRedirecting(params, false);

		// Use a session cache for SSL sockets
		// SSLSessionCache sessionCache = context == null ? null : new SSLSessionCache(context);
		// SSLCertificateSocketFactory.getDefault (30 * 1000);

		// Set the specified user agent and register standard protocols.
		HttpProtocolParams.setUserAgent(params, userAgent);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

		// We use a factory method to modify superclass initialization
		// parameters without the funny call-a-static-method dance.
		return new AndroidHttpClient(manager, params);
	}

	/**
	 * Create a new HttpClient with reasonable defaults (which you can update).
	 * 
	 * @param userAgent
	 *            to report in your HTTP requests.
	 * @return AndroidHttpClient for you to use for all your requests.
	 */
	public static AndroidHttpClient newInstance(String userAgent) {
		AndroidHttpClient client = newInstance(userAgent, null /* session cache */);
		return client;
	}

	public CookieStore getCookies() {
		if (delegate != null) {
			return delegate.getCookieStore();
		}
		return null;
	}

	private final DefaultHttpClient delegate;

	private RuntimeException mLeakedException = new IllegalStateException("AndroidHttpClient created and never closed");

	private AndroidHttpClient(ClientConnectionManager ccm, HttpParams params) {
		this.delegate = new DefaultHttpClient(ccm, params) {
			@Override
			protected BasicHttpProcessor createHttpProcessor() {
				// Add interceptor to prevent making requests from main thread.
				BasicHttpProcessor processor = super.createHttpProcessor();
				processor.addRequestInterceptor(sThreadCheckInterceptor);
				processor.addRequestInterceptor(new CurlLogger());

				return processor;
			}

			@Override
			protected HttpContext createHttpContext() {
				// Same as DefaultHttpClient.createHttpContext() minus the
				// cookie store.
				HttpContext context = new BasicHttpContext();
				context.setAttribute(ClientContext.AUTHSCHEME_REGISTRY, getAuthSchemes());
				context.setAttribute(ClientContext.COOKIESPEC_REGISTRY, getCookieSpecs());
				context.setAttribute(ClientContext.CREDS_PROVIDER, getCredentialsProvider());
				context.setAttribute(ClientContext.COOKIE_STORE, getCookieStore());
				return context;
			}
		};
	}

	/**
	 * Use customize cookies
	 * 
	 * @param cookies
	 *            the cookies you want to maintain
	 */
	public void loadCookies(CookieStore cookies) {
		this.mIsLoadCookies = true;
		this.delegate.setCookieStore(cookies);
	}

	/**
	 * Indicate if the HTTP client loaded user's own cookies(such as login session)
	 * 
	 * @return whether loaded the retained cookies
	 */
	public boolean isLoadOwnCookies() {
		return this.mIsLoadCookies;
	}

	public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
		if (interceptor == null) {
			return;
		}
		this.delegate.addRequestInterceptor(interceptor, this.delegate.getRequestInterceptorCount());
	}

	public void removeRequestInterceptor(HttpRequestInterceptor interceptor) {
		if (interceptor == null) {
			return;
		}
		this.delegate.removeRequestInterceptorByClass(interceptor.getClass());
	}

	public void addResponseInterceptor(HttpResponseInterceptor interceptor) {
		if (interceptor == null) {
			return;
		}
		this.delegate.addResponseInterceptor(interceptor, this.delegate.getResponseInterceptorCount());
	}

	public void removeResponseInterceptor(HttpResponseInterceptor interceptor) {
		if (interceptor == null) {
			return;
		}
		this.delegate.removeResponseInterceptorByClass(interceptor.getClass());
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (mLeakedException != null) {
			Log.e(TAG, "Leak found", mLeakedException);
			mLeakedException = null;
		}
	}

	/**
	 * Modifies a request to indicate to the server that we would like a gzipped response. (Uses the "Accept-Encoding" HTTP header.)
	 * 
	 * @param request
	 *            the request to modify
	 * @see #getUngzippedContent
	 */
	public static void modifyRequestToAcceptGzipResponse(HttpRequest request) {
		request.addHeader("Accept-Encoding", "gzip");
	}

	/**
	 * Modifies a request to indicate to the server that we would like a kind of MIME type response.(Uses the "Content-Type" HTTP header.)
	 * 
	 * @param request
	 *            the request to modify
	 * @param contentType
	 *            MEME type
	 */
	public static void modifyRequestContentType(HttpRequest request, String contentType) {
		request.addHeader("Content-Type", contentType);
	}

	/**
	 * Gets the input stream from a response entity. If the entity is gzipped then this will get a stream over the uncompressed data.
	 * 
	 * @param entity
	 *            the entity whose content should be read
	 * @return the input stream to read from
	 * @throws IOException
	 */
	public static InputStream getUngzippedContent(HttpEntity entity) throws IOException {
		InputStream responseStream = entity.getContent();
		if (responseStream == null)
			return responseStream;
		Header header = entity.getContentEncoding();
		if (header == null)
			return responseStream;
		String contentEncoding = header.getValue();
		if (contentEncoding == null)
			return responseStream;
		if (contentEncoding.contains("gzip"))
			responseStream = new GZIPInputStream(responseStream);
		return responseStream;
	}

	/**
	 * Release resources associated with this client. You must call this, or significant resources (sockets and memory) may be leaked.
	 */
	public void close() {
		if (mLeakedException != null) {
			getConnectionManager().shutdown();
			mLeakedException = null;
		}
	}

	/**
	 * Use cmwap gateway(10.0.0.172:80) as proxy for the normal internet connection
	 */
	public void useProxyConnection(HttpHost proxy) {
		// HttpHost proxy = new HttpHost(host, port, "http");
		this.delegate.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	/**
	 * Use default connection (except cmwap sisuation)
	 */
	public void useDefaultConnection() {
		this.delegate.getParams().removeParameter(ConnRoutePNames.DEFAULT_PROXY);
	}

	@Override
	public HttpParams getParams() {
		return delegate.getParams();
	}

	@Override
	public ClientConnectionManager getConnectionManager() {
		return delegate.getConnectionManager();
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException {
		return delegate.execute(request);
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
		return delegate.execute(request, context);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
		return delegate.execute(target, request);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
		return delegate.execute(target, request, context);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		return delegate.execute(request, responseHandler);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
		return delegate.execute(request, responseHandler, context);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
		return delegate.execute(target, request, responseHandler);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException,
			ClientProtocolException {
		return delegate.execute(target, request, responseHandler, context);
	}

	/**
	 * Compress data to send to server. Creates a Http Entity holding the gzipped data. The data will not be compressed if it is too short.
	 * 
	 * @param data
	 *            The bytes to compress
	 * @return Entity holding the data
	 */
	public static AbstractHttpEntity getCompressedEntity(byte data[]) throws IOException {
		AbstractHttpEntity entity;
		if (data.length < getMinGzipSize()) {
			entity = new ByteArrayEntity(data);
		} else {
			ByteArrayOutputStream arr = new ByteArrayOutputStream();
			OutputStream zipper = new GZIPOutputStream(arr);
			zipper.write(data);
			zipper.close();
			entity = new ByteArrayEntity(arr.toByteArray());
			entity.setContentEncoding("gzip");
		}
		return entity;
	}

	/**
	 * Compress data to send to server. Creates a Http Entity holding the gzipped data. The data will not be compressed if it is too short.
	 * 
	 * @param data
	 *            The bytes to compress
	 * @return Entity holding the data
	 */
	public static AbstractHttpEntity getCompressedEntity(InputStream in) throws IOException {
		AbstractHttpEntity entity;
		byte[] buffer = new byte[4096];
		int bytesRead = in.read(buffer);

		if (bytesRead < getMinGzipSize()) {
			byte[] data = new byte[bytesRead];
			System.arraycopy(buffer, 0, data, 0, bytesRead);
			entity = new ByteArrayEntity(data);
			in.close();

		} else {
			ByteArrayOutputStream arr = new ByteArrayOutputStream();
			OutputStream zipper = new GZIPOutputStream(arr);

			do {
				zipper.write(buffer, 0, bytesRead);
			} while ((bytesRead = in.read(buffer)) != -1);

			in.close();
			zipper.close();
			entity = new ByteArrayEntity(arr.toByteArray());
			entity.setContentEncoding("gzip");
		}
		return entity;
	}

	/**
	 * Retrieves the minimum size for compressing data. Shorter data will not be compressed.
	 */
	public static long getMinGzipSize() {
		return DEFAULT_SYNC_MIN_GZIP_BYTES; // For now, this is just a constant.
	}

	/* cURL logging support. */

	/**
	 * Logging tag and level.
	 */
	private static class LoggingConfiguration {

		private final String tag;

		private final int level;

		private LoggingConfiguration(String tag, int level) {
			this.tag = tag;
			this.level = level;
		}

		/**
		 * Returns true if logging is turned on for this configuration.
		 */
		private boolean isLoggable() {
			return Log.isLoggable(tag, level);
		}

		/**
		 * Prints a message using this configuration.
		 */
		private void println(String message) {
			Log.println(level, tag, message);
		}
	}

	/** cURL logging configuration. */
	private volatile LoggingConfiguration curlConfiguration;

	/**
	 * Enables cURL request logging for this client.
	 * 
	 * @param name
	 *            to log messages with
	 * @param level
	 *            at which to log messages (see {@link android.util.Log})
	 */
	public void enableCurlLogging(String name, int level) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (level < Log.VERBOSE || level > Log.ASSERT) {
			throw new IllegalArgumentException("Level is out of range [" + Log.VERBOSE + ".." + Log.ASSERT + "]");
		}

		curlConfiguration = new LoggingConfiguration(name, level);
	}

	/**
	 * Disables cURL logging for this client.
	 */
	public void disableCurlLogging() {
		curlConfiguration = null;
	}

	/**
	 * Logs cURL commands equivalent to requests.
	 */
	private class CurlLogger implements HttpRequestInterceptor {
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			LoggingConfiguration configuration = curlConfiguration;
			if (configuration != null && configuration.isLoggable() && request instanceof HttpUriRequest) {
				// Never print auth token -- we used to check ro.secure=0 to
				// enable that, but can't do that in unbundled code.
				configuration.println(toCurl((HttpUriRequest) request, false));
			}
		}
	}

	/**
	 * Generates a cURL command equivalent to the given request.
	 */
	private static String toCurl(HttpUriRequest request, boolean logAuthToken) throws IOException {
		StringBuilder builder = new StringBuilder();

		builder.append("curl ");

		for (Header header : request.getAllHeaders()) {
			if (!logAuthToken && (header.getName().equals("Authorization") || header.getName().equals("Cookie"))) {
				continue;
			}
			builder.append("--header \"");
			builder.append(header.toString().trim());
			builder.append("\" ");
		}

		URI uri = request.getURI();

		// If this is a wrapped request, use the URI from the original
		// request instead. getURI() on the wrapper seems to return a
		// relative URI. We want an absolute URI.
		if (request instanceof RequestWrapper) {
			HttpRequest original = ((RequestWrapper) request).getOriginal();
			if (original instanceof HttpUriRequest) {
				uri = ((HttpUriRequest) original).getURI();
			}
		}

		builder.append("\"");
		builder.append(uri);
		builder.append("\"");

		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
			HttpEntity entity = entityRequest.getEntity();
			if (entity != null && entity.isRepeatable()) {
				if (entity.getContentLength() < 1024) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					entity.writeTo(stream);
					String entityString = stream.toString();

					// TODO: Check the content type, too.
					builder.append(" --data-ascii \"").append(entityString).append("\"");
				} else {
					builder.append(" [TOO MUCH DATA TO INCLUDE]");
				}
			}
		}

		return builder.toString();
	}

	/**
	 * Returns the date of the given HTTP date string. This method can identify and parse the date formats emitted by common HTTP servers, such as <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC 822</a>, <a href="http://www.ietf.org/rfc/rfc0850.txt">RFC 850</a>, <a href="http://www.ietf.org/rfc/rfc1036.txt">RFC 1036</a>, <a href="http://www.ietf.org/rfc/rfc1123.txt">RFC 1123</a> and <a href="http://www.opengroup.org/onlinepubs/007908799/xsh/asctime.html" >ANSI C's asctime()</a>.
	 * 
	 * @return the number of milliseconds since Jan. 1, 1970, midnight GMT.
	 * @throws IllegalArgumentException
	 *             if {@code dateString} is not a date or of an unsupported format.
	 */
	// public static long parseDate(String dateString) {
	// return HttpDateTime.parse(dateString);
	// }

	public void clearCookies() {
		if (delegate != null) {
			delegate.setCookieStore(null);
		}
	}

}
