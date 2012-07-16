package com.randude14.hungergames.stats;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectionUtils {

	public static Map<String, String> post(String url, Map<String, String> data) throws IllegalStateException, IOException {
		URL siteUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		
		Set<String> keys = data.keySet();
		Iterator<String> keyIter = keys.iterator();
		String content = "";
		for(int i=0; keyIter.hasNext(); i++) {
			Object key = keyIter.next();
			if(i!=0) {
				content += "&";
			}
			content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
		}
		System.out.println(content);
		out.writeBytes(content);
		out.flush();
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		Map<String, String> map = new HashMap<String, String>();
		String line = "";
		while((line = in.readLine()) != null) {
			String[] split = line.split("=");
			if (split.length < 2) throw new IllegalStateException("Bad return information!");
			map.put(split[0], split[1]);
		}
		in.close();
		return map;
	}
	
}
