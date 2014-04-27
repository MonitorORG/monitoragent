package com.host.node.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
	
	public static boolean isEmpty(String str) {
		boolean result = true;
		
		if (str != null) {
			result = str.trim().isEmpty();
		}
		
		return result;
	}
	
	public static String[] getProcessArray(String processStr) {
		if (!isEmpty(processStr)) {
			return processStr.replaceAll(ProcessStatus.START_SYMBOL, "").split(ProcessStatus.END_SYMBOL_REGEX);
		}
		return new String[0];
	}
	
	public static List<String> getProcessList(String processStr) {
		 return Arrays.asList(getProcessArray(processStr));
	}

}
