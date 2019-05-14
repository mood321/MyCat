package com.mood.framework.minicat.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//import oracle.sql.CLOB;

public class StringUtil {

	public static boolean isNullOrEmpty(Object obj) {
		try {
			if (obj == null)
				return true;
			if (obj instanceof CharSequence) {
				return ((CharSequence) obj).length() == 0;
			}
			if (obj instanceof Collection) {
				return ((Collection<?>) obj).isEmpty();
			}
			if (obj instanceof Map) {
				return ((Map<?, ?>) obj).isEmpty();
			}
			if (obj instanceof Object[]) {
				Object[] object = (Object[]) obj;
				if (object.length == 0) {
					return true;
				}
				boolean empty = true;
				for (int i = 0; i < object.length; i++) {
					if (!isNullOrEmpty(object[i])) {
						empty = false;
						break;
					}
				}
				return empty;
			}

			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	public static Integer findNull(Object... objs) {
		if (isNullOrEmpty(objs)) {
			return 0;
		}
		for (int i = 0; i < objs.length; i++) {
			if (isNullOrEmpty(objs[i])) {
				return i;
			}
		}
		return -1;
	}

	public static boolean hasNull(Object... objs) {
		return findNull(objs) > -1;
	}
	public static String textCutCenter(String allTxt, String firstTxt, String lastTxt) {
		try {
			String tmp = "";
			int n1 = allTxt.indexOf(firstTxt);
			if (n1 == -1) {
				return "";
			}
			tmp = allTxt.substring(n1 + firstTxt.length(), allTxt.length());
			int n2 = tmp.indexOf(lastTxt);
			if (n2 == -1) {
				return "";
			}
			tmp = tmp.substring(0, n2);
			return tmp;
		} catch (Exception e) {
			return "";
		}
	}

	public static List<String> textCutCenters(String allTxt, String firstTxt, String lastTxt) {
		try {
			List<String> results = new ArrayList<String>();
			while (allTxt.contains(firstTxt)) {
				int n = allTxt.indexOf(firstTxt);
				allTxt = allTxt.substring(n + firstTxt.length(), allTxt.length());
				n = allTxt.indexOf(lastTxt);
				if (n == -1) {
					return results;
				}
				String result = allTxt.substring(0, n);
				results.add(result);
				allTxt = allTxt.substring(n + firstTxt.length(), allTxt.length());
			}
			return results;
		} catch (Exception e) {
			return null;
		}
	}
}
