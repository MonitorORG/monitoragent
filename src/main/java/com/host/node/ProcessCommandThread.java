package com.host.node;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

import com.host.node.model.UserCommandDTO;
import com.host.node.request.MainPostRequest;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;


public class ProcessCommandThread extends Thread {
	
	private UserCommandDTO command;
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	public ProcessCommandThread (UserCommandDTO command) {
		this.command = command;
	}
	
	@Override
	public void run() {
		StringBuffer resultBuffer = new StringBuffer();
		boolean isSucess = true;
		
		String commandStr = command.getCommandStr();
		System.out.println("Process command string: " + commandStr);
		
		if (commandStr != null && !(commandStr.trim().isEmpty())) {
			
			try {
				
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		        ByteArrayOutputStream errorStream = new ByteArrayOutputStream(); 
		        CommandLine commandline = CommandLine.parse(commandStr);  
		        
		        DefaultExecutor exec = new DefaultExecutor();  
		        exec.setExitValues(null);  
		        
		        // Infinite timeout
		        ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
		        exec.setWatchdog(watchdog);
		        
		        // Using Std out for the output/error stream
		        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream,errorStream);
		        exec.setStreamHandler(streamHandler);  
		        
		        // This is used to end the process when the JVM exits
		        ShutdownHookProcessDestroyer processDestroyer = new ShutdownHookProcessDestroyer();
		        exec.setProcessDestroyer(processDestroyer);
		        
		        // Use of recursion along with the ls makes this a long running process
		        exec.setWorkingDirectory(new File("C:/"));
		        
		        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		        exec.execute(commandline, resultHandler);
//		        exec.execute(commandline);
		        resultHandler.waitFor();
		          
		        String out = outputStream.toString("gbk");
		        String error = errorStream.toString("gbk");
		          
		        System.out.println("Process command out: " + out);
		        System.out.println("Process command error: " + error);
		        
		        resultBuffer.append(out);
		        resultBuffer.append(error);
		        
		        // some time later the result handler callback was invoked so we
		    	// can safely request the exit value
				int exitValue = resultHandler.getExitValue();			
				System.out.println("Process command exitValue: " + exitValue);
				
				if (exitValue != 0) {
					isSucess = false;
					resultBuffer.append("Command execute failed");
				}
				
				outputStream.close();
				errorStream.close();
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			isSucess = false;
			resultBuffer.append("Command invalid");
		}
		
		// End command process
		command.setResultStr(resultBuffer.toString());
		command.setEndDate(new Date());
		if (isSucess) {
			command.setStatus("Sucess");
		} else {
			command.setStatus("Failed");
		}
			
		
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
