package com.host.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class CmdExecuteTestClass {
	
	public static void main(String[] args) {
		
		HttpClient client2 = new DefaultHttpClient();		
		HttpPost httpPost = new HttpPost("http://127.0.0.1:9999/");

		try {
			
			StringEntity inputEntity = new StringEntity("cmd /c tasklist");
//			inputEntity.setContentType("application/json");
			httpPost.setEntity(inputEntity);
			
//			System.out.println("inputEntity: " + JSONObject.fromObject(status).toString());
			
			HttpResponse response = client2.execute(httpPost);
			String json = EntityUtils.toString(response.getEntity(), "utf-8");
			
//			JSONObject obj = new JSONObject(json);
//			
//			JSONObject.toBean(jsonObject, beanClass);
			
			System.out.println("JSON: " + json);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();     
		}
		
//		try {
//			Process pro = Runtime.getRuntime().exec("cmd /c tasklist"); 
//		    BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream())); 
//		    String msg = null; 
//		    while ((msg = br.readLine()) != null) { 
//		    	System.out.println(msg); 
//		    }
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
   
//		try {
//
//			   // 登网站
//			   Process p = Runtime.getRuntime().exec("cmd.exe  /c  start  http://www.hao123.net/");
//
//			   // 使用用Ping命令
////			   Process p = Runtime.getRuntime().exec("cmd.exe  /c  start  ping 10.5.2.19");
//			   
//			   
//			   
//			   InputStream is = p.getInputStream();
//			   int data;
//			   while((data = is.read()) != -1) {
//				   System.out.print((char)data);
//			   }
//
//			  } catch (Exception e) {
//				  e.printStackTrace();
//			  }
	}

}
