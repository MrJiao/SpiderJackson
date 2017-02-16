package com.jackson.net;

import org.apache.http.Header;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jackson on 2016/10/26.
 */
public enum HttpClientCreater {
    instance;
    private static Logger logger = LogManager.getLogger(HttpClientCreater.class.getName());
    private  PoolingHttpClientConnectionManager cm ;
    private  CloseableHttpClient httpClient;
    private  CloseableHttpClient httpsClient;

    HttpClientCreater(){
         cm = new PoolingHttpClientConnectionManager();
         cm.setMaxTotal(200);
    }

    public CloseableHttpClient getHttpClient() {
        if(httpClient==null){
            synchronized (CloseableHttpClient.class){
                if(httpClient==null){
                    httpClient = HttpClients.custom()
                            .setDefaultRequestConfig(getDefaultRequestConfig())
                            .setDefaultHeaders(getDefaultHeaders())
                            .setConnectionManager(cm)
                            .build();
                }

            }
        }
        logger.debug("获取httpClient");
        return httpClient;
    }

    public CloseableHttpClient getHttpsClient(){
        if(httpsClient==null){
            synchronized (CloseableHttpClient.class){
                if(httpsClient==null){
                    httpsClient = HttpClients.custom()
                            .setDefaultRequestConfig(getDefaultRequestConfig())
                            .setDefaultHeaders(getDefaultHeaders())
                            .setConnectionManager(cm)
                            .setSSLSocketFactory(getSSLSocketFactory())
                            .build();
                }

            }
        }
        logger.debug("获取httpsClient");
        return httpsClient;
    }



    public static Collection<? extends Header> getDefaultHeaders() {
        ArrayList<Header> headers = new ArrayList();
        headers.add(new BasicHeader("Accept-Charset", "utf-8"));
        headers.add(new BasicHeader("Cache-Control", "no-cache"));
        headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.1952.400 QQBrowser/9.5.10023.400"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, sdch"));
        return headers;
    }

    private static RequestConfig getDefaultRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();
    }


    /**
     * 创建SSL安全连接，不验证安全性(爬虫下安全性要求不高）
     * @return
     */
    public static SSLConnectionSocketFactory getSSLSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            sslsf = new SSLConnectionSocketFactory(sslContext,new HostnameVerifier(){
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }
}
