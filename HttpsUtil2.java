package com.nio.ROKID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;

/*
 * https请求的工具类
 */
public class HttpsUtil2 {
	
	private static final String METHOD_POST = "POST";  
	private static final String DEFAULT_CHARSET = "utf-8";
	private static final int CONNECTIONTIMEOUT = 5000;
	private static final int READTIMEOUT = 5000;
	
	public static String doPost(String url,Map<String, Object> paramsMap, Map<String, String> headersMap) throws Exception{
		//组装url
		String requestUrl = getUrl(url,paramsMap);
		
		HttpsURLConnection conn = null;  
        OutputStream out = null;  
        String rsp = null; 
        
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");  
			ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());  
			SSLContext.setDefault(ctx);  
			
			URL postUrl = new URL(requestUrl);
			conn = (HttpsURLConnection) postUrl.openConnection();  
			
			conn.setRequestMethod(METHOD_POST);   
			conn.setRequestProperty("Content-Type", "application/octet-stream"); 
			for(Entry<String, String> each : headersMap.entrySet()) {
				conn.setRequestProperty(each.getKey(),each.getValue());
			}
			conn.setConnectTimeout(CONNECTIONTIMEOUT);  
			conn.setReadTimeout(READTIMEOUT);
			conn.setDoOutput(true);
			conn.setDoInput(true); 
			conn.setHostnameVerifier(new HostnameVerifier() {  
			    @Override  
			    public boolean verify(String hostname, SSLSession session) {  
			        return true;  
			    }
			});  
			
			out = conn.getOutputStream();  
			conn.connect();
			rsp = getResponseAsString(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {  
            if (out != null) {  
                out.close();  
            }  
            if (conn != null) {  
                conn.disconnect(); 
            }  
        }    
        
		return rsp;
	}
	
	private static class DefaultTrustManager implements X509TrustManager {  
		  
        @Override  
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}  
  
        @Override  
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}  
  
        @Override  
        public X509Certificate[] getAcceptedIssuers() {  
            return null;  
        } 
    }  
	//组装地址
	public static String getUrl(String url,Map<String, Object> paramsMap){
		if (paramsMap == null || paramsMap.size() == 0 ) {
			return url;
		}
		StringBuffer params = new StringBuffer();
        for (Entry<String, Object>entry : paramsMap.entrySet()) {
        	params.append(entry.getKey()+"="+entry.getValue());
        	params.append("&");
		}
        String newUrl = url + params.toString();
        return newUrl;
	}
	
	protected static String getResponseAsString(HttpURLConnection conn) throws IOException {  
        String charset = getResponseCharset(conn.getContentType());  
        InputStream es = conn.getErrorStream();  
        String msg = null;
        if (es == null) {  
            return getStreamAsString(conn.getInputStream(), charset);  
        } else {  
            msg = getStreamAsString(es, charset);  
            if (StringUtils.isEmpty(msg)) {  
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());  
            } else {  
            	//System.out.println("msg:" + msg);
                //throw new IOException(msg);  
            }  
        }
        return msg;
    }  
	private static String getStreamAsString(InputStream stream, String charset) throws IOException {  
        try {  
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));  
            StringWriter writer = new StringWriter();  
  
            char[] chars = new char[256];  
            int count = 0;  
            while ((count = reader.read(chars)) > 0) {  
                writer.write(chars, 0, count);  
            }  
  
            return writer.toString();  
        } finally {  
            if (stream != null) {  
                stream.close();  
            }  
        }  
    }  
  
    private static String getResponseCharset(String ctype) {  
        String charset = DEFAULT_CHARSET;  
        if (!StringUtils.isEmpty(ctype)) {  
            String[] params = ctype.split(";");  
            for (String param : params) {  
                param = param.trim();  
                if (param.startsWith("charset")) {  
                    String[] pair = param.split("=", 2);  
                    if (pair.length == 2) {  
                        if (!StringUtils.isEmpty(pair[1])) {  
                            charset = pair[1].trim();  
                        }  
                    }  
                    break;  
                }  
            }  
        }  
        return charset;  
    }  
}
