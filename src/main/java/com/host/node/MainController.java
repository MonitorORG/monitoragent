package com.host.node;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import org.codehaus.jackson.map.ObjectMapper;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;

import com.host.node.model.HostStatusInfoDTO;
import com.host.node.request.MainPostRequest;
import com.host.node.util.CommandExecutor;
import com.host.node.util.ProcessStatus;
import com.host.node.util.StringUtil;

public class MainController {
	
	public static String serverUrl = "http://127.0.0.1:8090/monitorserver/";
	public static String macAddress = "";
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String processListStr = new String();
	public static List<String> processList = new ArrayList<String>();
	public static String processStatusInfo = new String();
	
	static {
		try {
			initOnePhysicalMacAddress();
			System.out.println("MacAddress: " + macAddress);
			
			Properties p = new Properties();
			FileInputStream ferr = new FileInputStream("properties.properties");// 用subString(6)去掉：file:/
			try{
				p.load(ferr);
				ferr.close();
				Set s = p.keySet();
				Iterator it = s.iterator();
				while(it.hasNext()){
					String id = (String)it.next();
					String value = p.getProperty(id);
					System.out.println("Reading properties: " + id + " = " + value);
					
					if (id.equals("serverUrl")) {
						serverUrl = value;
					}
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	static void initOnePhysicalMacAddress() {
		
		try {
			Sigar sigar = new Sigar();
			String[] names = sigar.getNetInterfaceList();
		    
		    if (names != null && names.length > 0) {
			    
			    for (int i = 0; i < names.length; i++) {
			    	NetInterfaceConfig nic = sigar.getNetInterfaceConfig(names[i]);

			    	if (nic.getDescription().contains("PCI")) {
			    		macAddress = nic.getHwaddr();
			    		break;
			    	}
			    }
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	static class RefreshHostInfoTask extends java.util.TimerTask {
		
		@Override
		public void run() {
			System.out.println("FileEncoding: " + System.getProperty("file.encoding")); 
			System.out.println("SunJnuEncoding: " + System.getProperty("sun.jnu.encoding"));
			System.out.println("------------------------------------------");
			
			Sigar sigar = new Sigar();
			HostStatusInfoDTO status = new HostStatusInfoDTO();
			status.setIsAgentCommited(true);

			// 1. Get machine basic status info
			try { 
				CpuPerc firstCPU = sigar.getCpuPercList()[0];
			 
				status.setCpuCount(sigar.getCpuInfoList().length);
				status.setCpuTotalUsed(firstCPU.getCombined());		
				  
				Mem mem = sigar.getMem();
				mem = sigar.getMem(); 
				  
				status.setTotalMem(mem.getTotal());
				status.setFreeMem(mem.getFree());

				String hostname = InetAddress.getLocalHost().getHostName();
				hostname = sigar.getNetInfo().getHostName(); 
				  
				status.setHostname(hostname);
				status.setMacAddress(macAddress);
			  
			} catch (Exception e) { 
				  
				System.out.println(e.getMessage());
				e.printStackTrace();
			} finally {
				sigar.close();
			}
			  
			// 2. Get monitor process status info
			StringBuffer procStaInfoBuf = new StringBuffer();
			processStatusInfo = "";
			if (!processList.isEmpty()) {
				CommandExecutor executor = new CommandExecutor("tasklist", false);
				executor.execute();
				String tasklistResult = executor.getResult();
				
				for (String procName : processList) {
					if (executor.isSuccess() && tasklistResult.contains(procName)) {
						procStaInfoBuf.append(ProcessStatus.START_SYMBOL)
									  .append(ProcessStatus.RUNNING)
									  .append(ProcessStatus.SEP_SYMBOL)
									  .append(procName)
									  .append(ProcessStatus.END_SYMBOL);
					} else {
						procStaInfoBuf.append(ProcessStatus.START_SYMBOL)
									  .append(ProcessStatus.STOP)
									  .append(ProcessStatus.SEP_SYMBOL)
									  .append(procName)
									  .append(ProcessStatus.END_SYMBOL);
					}
				}
				
				processStatusInfo = procStaInfoBuf.toString();
			}
			status.setProcessList(processListStr);
			status.setProcessStatusResults(processStatusInfo);
			  
			// 3. Commit New Status
			MainPostRequest request = new MainPostRequest();
			request.setUrl("services/hostInfo/hostStatusInfoService/create");
			  
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				objectMapper.writeValue(bos, status);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String statusJson = bos.toString();
			System.out.println("Send new status: " + statusJson);
			  
			request.setPostDataJsonStr(statusJson);
			String resultDataJsonStr = request.execute();			  
			System.out.println("Send new status response: " + resultDataJsonStr);
			  
			// 4. Update processList by returns
			if (!StringUtil.isEmpty(resultDataJsonStr)) {
				try {
					  // 2. Transfer response to Command DTO
					HostStatusInfoDTO jsonObject = objectMapper.readValue(resultDataJsonStr, HostStatusInfoDTO.class);
					if (jsonObject.getId() != null) {
						  
						if (StringUtil.isEmpty(jsonObject.getProcessList())) {
							processListStr = "";
							processList = new ArrayList<String>();
						} else {
							if (!jsonObject.getProcessList().equals(processListStr)) {
								processListStr = jsonObject.getProcessList();
								processList = StringUtil.getProcessList(processListStr);
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
	}

	public static void main(String[] args) {
		
		// 1. Refresh Host Status		
		Timer timer = new Timer();
		timer.schedule(new RefreshHostInfoTask(), 1000, 5000);//在1秒后执行此任务,每次间隔5秒,如果传递一个Data参数,就可以在某个固定的时间执行这个任务.
		
		// 2. Get Last Unfinished Command  (Scan)
		ScanCommandThread scanThread = new ScanCommandThread();
		scanThread.start();
		
		// 3. Process monitor thread
		ProcessMonitorThread monitorThread = new ProcessMonitorThread();
				
		while(true){//这个是用来停止此任务的,否则就一直循环执行此任务了
			try {
				int ch = System.in.read();
				if(ch-'c'==0){
					timer.cancel();//使用这个方法退出任务
					scanThread.setContinue(false);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}	
	}
	
	

}
