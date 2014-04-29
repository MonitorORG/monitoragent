package com.host.node.util;

public class ProcessStatus {
	public static final String STOP = "*2";
	public static final String RUNNING = "*1";
	public static final String WAITING = "*0";
	
	public static final String START_SYMBOL = "#";
	public static final String SEP_SYMBOL = "@";
	public static final String END_SYMBOL = "|";
	
	public static final String END_SYMBOL_REGEX = "\\|";
}
