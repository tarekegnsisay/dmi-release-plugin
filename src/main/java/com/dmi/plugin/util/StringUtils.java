package com.dmi.plugin.util;

public class StringUtils {
	public static boolean isEmptyOrNull(String str) {
		if(str==null)
			return true;
		if(str.isEmpty())
			return true;
		if(str.equals(" "))
			return true;
		/*
		 * not null and empty or not just a space		
		 */
		return false;
	}

}
