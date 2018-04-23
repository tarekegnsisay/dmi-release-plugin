package com.dmi.plugin.util;

import java.util.Collection;

public class CollectionsUtil {
	public static boolean isEmptyOrNull(Collection<?> list){
		if(list==null || list.isEmpty())
			return true;
		return false;
	}
}
