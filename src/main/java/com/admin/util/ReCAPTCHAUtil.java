package com.admin.util;

import java.util.HashMap;

public class ReCAPTCHAUtil {
	public static boolean getSiteVerify (String url, String secretKey, String token) throws Exception {
		String stringBuilder = url + "?secret=" + secretKey + "&response=" + token;
		HashMap<String, Object> resultMap = HttpConnectUtil.getStringFromURL(stringBuilder,
				"UTF-8", true, "", "application/x-www-form-urlencoded");
		return (boolean) resultMap.get("success");
	}
}
