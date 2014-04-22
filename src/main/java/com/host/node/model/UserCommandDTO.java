package com.host.node.model;

import java.util.Date;

public class UserCommandDTO {
	
    private Long id;
	private String hostMacAddress;
	
	private String commandStr;
	private Date creationDate;
	private Date endDate;
	private String status;
	
	private String resultStr;
	
	private String endCommandClass;
	private String endCommandMethod;
	private String endCommandParam;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCommandStr() {
		return commandStr;
	}
	public void setCommandStr(String commandStr) {
		this.commandStr = commandStr;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResultStr() {
		return resultStr;
	}
	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}
	public String getEndCommandClass() {
		return endCommandClass;
	}
	public void setEndCommandClass(String endCommandClass) {
		this.endCommandClass = endCommandClass;
	}
	public String getEndCommandMethod() {
		return endCommandMethod;
	}
	public void setEndCommandMethod(String endCommandMethod) {
		this.endCommandMethod = endCommandMethod;
	}
	public String getEndCommandParam() {
		return endCommandParam;
	}
	public void setEndCommandParam(String endCommandParam) {
		this.endCommandParam = endCommandParam;
	}
	public String getHostMacAddress() {
		return hostMacAddress;
	}
	public void setHostMacAddress(String hostMacAddress) {
		this.hostMacAddress = hostMacAddress;
	}

}
