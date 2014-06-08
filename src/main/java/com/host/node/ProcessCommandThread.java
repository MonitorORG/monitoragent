package com.host.node;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

import com.host.node.model.UserCommandDTO;
import com.host.node.request.MainPostRequest;
import com.host.node.util.CommandExecutor;


public class ProcessCommandThread extends Thread {
	
	private UserCommandDTO command;
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	public ProcessCommandThread (UserCommandDTO command) {
		this.command = command;
	}
	
	@Override
	public void run() {
		String commandStr = command.getSecureCommandStr();
		System.out.println("Process command string: " + commandStr);
		
		// 1. execute command
		CommandExecutor executor = new CommandExecutor(commandStr, true);
		executor.execute();
		
		// 2. end command process
		command.setResultStr(executor.getResult());
		command.setEndDate(new Date());
		if (executor.isSuccess()) {
			command.setStatus("Sucess");
		} else {
			command.setStatus("Failed");
		}			
		
		// 3. update command result to server
		  MainPostRequest request = new MainPostRequest();
		  request.setUrl("services/command/userCommandService/create");
		  
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		  try {
			objectMapper.writeValue(bos, command);
		  } catch (Exception e) {
				e.printStackTrace();
		  }
		  String newCommandJson = bos.toString();
		  System.out.println("Update command execute request: " + newCommandJson);
		  
		  request.setPostDataJsonStr(newCommandJson);
		  String resultDataJsonStr = request.execute();
		  
		  System.out.println("Update command execute response: " + resultDataJsonStr);
		
		
	}
	
}
