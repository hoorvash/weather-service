package com.nik.weather.conn;

import com.nik.weather.util.JsonUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static int defaultRequestTimeout = 5000;
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private static SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
    private static SSLContext sslContext = null;
    private static IdleConnectionMonitorThread monitorThread = new IdleConnectionMonitorThread();

    private static final Logger LOGGER = LogManager.getLogger();

    static {
        // Increase max total connection
        cm.setMaxTotal(500);
        // Increase default max connection per route
        cm.setDefaultMaxPerRoute(100);

        try {
            sslContextBuilder.loadTrustMaterial((x509Certificates, s) -> true);
            sslContext = sslContextBuilder.build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        // this monitor is supposed to tell connection manager (cm) to check for stale connections and release their resources
        Thread t = new Thread(monitorThread);
        t.start();
    }

    private static SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);

    private static RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(defaultRequestTimeout)
            .setConnectionRequestTimeout(defaultRequestTimeout)
            .setSocketTimeout(defaultRequestTimeout).build();

    private static CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(sslFactory)
            .setConnectionManager(cm)
            .setDefaultRequestConfig(config)
            .build();

    @SuppressWarnings("unchecked")
    public static <T> T fetchJson(HttpRequestBase request, Class<T> cls) throws Exception {
        return fetchJson(request, cls, defaultRequestTimeout / 1000);
    }

    public static <T> T fetchJson(HttpRequestBase request, Class<T> cls, int timeout) throws Exception {
        String response = fetchRawResponse(request, timeout);
        LOGGER.info("RESPONSE: {}", response);

        if (response.trim().length() > 0)
            return JsonUtil.deserialize(response, cls);

        return null;
    }

    private static String fetchRawResponse(HttpRequestBase request) throws Exception {
        StringBuilder responseBuilder = new StringBuilder();

        try (CloseableHttpResponse response = HttpUtil.httpClient.execute(request)) {
            InputStream stream = response.getEntity().getContent();
            int content;
            int statusCode = response.getStatusLine().getStatusCode();

            while ((content = stream.read()) != -1)
                responseBuilder.append((char) content);

            if (statusCode == HttpStatus.SC_OK) {
                return responseBuilder.toString();
            } else {
                LOGGER.error(new String(responseBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                throw new Exception(String.format(
                        "Response status is [%s] for URL [%s] with response: %s",
                        statusCode, request.getURI().toString(), responseBuilder.toString()));
            }
        } finally {
            request.releaseConnection();
        }
    }

    private static void fireAndForget(HttpRequestBase request) throws Exception {
        try (CloseableHttpResponse response = HttpUtil.httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK)
                throw new Exception(String.format(
                        "Response status is [%s] for URL [%s]", statusCode, request.getURI().toString()));
        } finally {
            request.releaseConnection();
        }
    }

    /**
     * @param timeout In seconds
     */
    public static String fetchRawResponse(HttpRequestBase request, int timeout) throws Exception {
        timeout *= 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();

        request.setConfig(config);
        return fetchRawResponse(request);
    }

    public static void fireAndForget(HttpRequestBase request, int timeout) throws Exception {
        timeout *= 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();

        request.setConfig(config);
        fireAndForget(request);
    }

    public static HttpRequestBase buildHttpRequest(
            String method, String url, List<NameValuePair> params, Map<String, String> headers, String rawBody)
            throws MalformedURLException, UnsupportedEncodingException {

        new URL(url);

        if (method.toUpperCase().equals("POST")) {
            HttpPost post = new HttpPost(url);

            if (params != null && params.size() > 0)
                post.setEntity(new UrlEncodedFormEntity(params, Charset.forName("UTF8")));

            if (rawBody != null && !rawBody.trim().isEmpty())
                post.setEntity(new StringEntity(rawBody, "UTF8"));

            if (headers != null)
                for (Map.Entry<String, String> entry : headers.entrySet())
                    post.setHeader(entry.getKey(), entry.getValue());

            return post;
        }

        if (params != null && params.size() > 0) {
            StringBuilder urlBuilder = new StringBuilder(url).append("?");

            for (NameValuePair param : params)
                urlBuilder
                        .append(param.getName())
                        .append("=")
                        .append(URLEncoder.encode(param.getValue(), "UTF-8"))
                        .append("&");

            url = urlBuilder.substring(0, urlBuilder.length() - 1);
        }

        HttpGet get = new HttpGet(url);

        if (headers != null)
            for (Map.Entry<String, String> entry : headers.entrySet())
                get.setHeader(entry.getKey(), entry.getValue());

        return get;
    }

    private static class IdleConnectionMonitorThread extends Thread {
        @SuppressWarnings("InfiniteLoopStatement")
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        wait(1000);
                        cm.closeExpiredConnections();
                        cm.closeIdleConnections(20, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                LOGGER.error("ec-10027");
            }
        }
    }
}