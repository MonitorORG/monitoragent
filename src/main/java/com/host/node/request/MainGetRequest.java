package com.host.node.request;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.host.node.MainController;

public class MainGetRequest {
	
	private String url = "";
//	private String postDataJsonStr = "";
	private String resultDataJsonStr = "";
	
	public String execute() {
		HttpClient client = MainHttpUtil.getDefaultHttpClient();        
		HttpGet httpget = new HttpGet(MainController.serverUrl + url);
		
		try {			
//			StringEntity inputEntity = new StringEntity(postDataJsonStr);
//			inputEntity.setContentType("application/json");
//			httpPost.setEntity(inputEntity);
//			
//			System.out.println("inputEntity: " + postDataJsonStr);
			
			HttpResponse response = client.execute(httpget);
			resultDataJsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
			
			System.out.println("JSON: " + resultDataJsonStr);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();     
		}
		
		return resultDataJsonStr;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

//	public String getPostDataJsonStr() {
//		return postDataJsonStr;
//	}
//
//	public void setPostDataJsonStr(String postDataJsonStr) {
//		this.postDataJsonStr = postDataJsonStr;
//	}

	public String getResultDataJsonStr() {
		return resultDataJsonStr;
	}

	public void setResultDataJsonStr(String resultDataJsonStr) {
		this.resultDataJsonStr = resultDataJsonStr;
	}

}
