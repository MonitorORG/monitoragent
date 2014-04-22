package com.host.node;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;

import com.host.node.model.HostStatusInfoDTO;
import com.host.node.model.UserCommandDTO;
import com.host.node.request.MainGetRequest;
import com.host.node.request.MainPostRequest;

public class MainController {
	
	public static String serverUrl = "http://127.0.0.1:8090/MonitorServer-1.0/";
	public static String macAddress = "";
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		try {
			Sigar sigar = new Sigar();
			NetInterfaceConfig nic = sigar.getNetInterfaceConfig();	// get Default Mac Address
			macAddress = nic.getHwaddr();		
			
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
				e.printStackTrace();
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
			  
			  // 1. Save New Status
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
		}
		
	}

	public static void main(String[] args) {
		
		// 1. Refresh Host Status		
		Timer timer = new Timer();
		timer.schedule(new RefreshHostInfoTask(), 1000, 5000);//在1秒后执行此任务,每次间隔5秒,如果传递一个Data参数,就可以在某个固定的时间执行这个任务.
		
		// 2. Get Last Unfinished Command  (Scan)
		ScanCommandThread scanThread = new ScanCommandThread();
		scanThread.start();
				
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
