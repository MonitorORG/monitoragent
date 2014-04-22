package com.host.node.request;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.host.node.util.ConstantsUtil;

public class MainHttpUtil {
	
	
	public static HttpClient getDefaultHttpClient() {
		HttpClient client = new DefaultHttpClient();
		// 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConstantsUtil.Default_Http_Conn_Timeout);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ConstantsUtil.Default_Http_ReadWrite_Timeout);
        
        return client;
	}

}
