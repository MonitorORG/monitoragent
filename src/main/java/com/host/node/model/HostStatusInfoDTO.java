package com.host.node.model;

public class HostStatusInfoDTO {

	private Long id;
	private String hostname;
	private int cpuCount;
	private double cpuTotalUsed;
	private double totalMem;
	private double freeMem;
	
	private String macAddress;
	
	private String commandStr;
	public String getCommandStr() {
		return commandStr;
	}
	public void setCommandStr(String commandStr) {
		this.commandStr = commandStr;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getCpuCount() {
		return cpuCount;
	}
	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}
	public double getCpuTotalUsed() {
		return cpuTotalUsed;
	}
	public void setCpuTotalUsed(double cpuTotalUsed) {
		this.cpuTotalUsed = cpuTotalUsed;
	}
	public double getTotalMem() {
		return totalMem;
	}
	public void setTotalMem(double totalMem) {
		this.totalMem = totalMem;
	}
	public double getFreeMem() {
		return freeMem;
	}
	public void setFreeMem(double freeMem) {
		this.freeMem = freeMem;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}	
	
}
