package com.host.node;

import java.io.ByteArrayOutputStream;

import org.apache.commons.httpclient.util.TimeoutController;
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;
import org.codehaus.jackson.map.ObjectMapper;

import com.host.node.model.UserCommandDTO;
import com.host.node.request.MainGetRequest;
import com.host.node.request.MainPostRequest;
import com.host.node.util.ConstantsUtil;

public class ScanCommandThread extends Thread {
	
	private int refreshRate = 3000;
	
	private boolean isContinue = true;
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	public ScanCommandThread() {
		super();
	}
	
	@Override
	public void run() {
		
		while(isContinue()) {
			try {
				// 1. Get Last Command mac address
				MainGetRequest cmdRequest = new MainGetRequest();
				cmdRequest.setUrl("services/command/userCommandService/lastcommand/" + MainController.macAddress);
				String cmdResponse = cmdRequest.execute();
				 
				System.out.println("Get last command response: " + cmdResponse);
				
				if (cmdResponse != null && !cmdResponse.isEmpty()) {
					// 2. Transfer response to Command DTO
					UserCommandDTO jsonObject = objectMapper.readValue(cmdResponse, UserCommandDTO.class);
					
					if (jsonObject.getId() != null) {
						
						// 3. Update command status to processing
						jsonObject.setStatus("Processing");
						MainPostRequest request = new MainPostRequest();
						request.setUrl("services/command/userCommandService/create");
						  
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						try {
							objectMapper.writeValue(bos, jsonObject);
						} catch (Exception e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
						String newCommandJson = bos.toString();						  
						System.out.println("Update command status to processing: " + newCommandJson);
						  
						request.setPostDataJsonStr(newCommandJson);
						String resultDataJsonStr = request.execute();
						  
						System.out.println("Update command status to processing returns: " + resultDataJsonStr);
						
						// 4. Process command
						ProcessCommandThread task = new ProcessCommandThread(jsonObject);
						TimeoutController.execute(task, ConstantsUtil.Default_Thread_Timeout);
					}
				}
			
				Thread.sleep(getRefreshRate());
				
			} catch (InterruptedException e) {
				
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (TimeoutException e) {
				
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	public boolean isContinue() {
		return isContinue;
	}

	public void setContinue(boolean isContinue) {
		this.isContinue = isContinue;
	}
}
