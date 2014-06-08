package com.host.node.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;

public class CommandExecutor {
	
	private String commandStr;
	private StringBuffer resultBuffer = new StringBuffer();
	private boolean isSucess = true;
	private boolean isSecureCommandStr = false;
	
	public CommandExecutor(String commandStr, boolean isSecureCommandStr) {
		this.commandStr = commandStr;
		this.isSecureCommandStr = isSecureCommandStr;
	}
	
	public void execute() {
		if (isSecureCommandStr) {
			commandStr = KeystoreUtil.getCommandBySecuredStr(commandStr);
		}
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
	}
	
	public boolean isSuccess() {
		return isSucess;
	}
	
	public String getResult() {
		return resultBuffer.toString();
	}
}
