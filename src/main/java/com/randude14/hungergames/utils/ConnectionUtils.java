package com.randude14.hungergames.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownServiceException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConnectionUtils {

	public static void post(String url, Map<String, String> data) throws IOException, ParserConfigurationException, SAXException {
		URL siteUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
		try {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			
			Set<String> keys = data.keySet();
			Iterator<String> keyIter = keys.iterator();
			String content = "";
			for (int i = 0; keyIter.hasNext(); i++) {
				String key = keyIter.next();
				if (i != 0) {
					content += "&";
				}
				content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
			}
			out.writeBytes(content);
			out.flush();
			out.close();
		} catch (IOException iOException) {
			throw iOException;
		} finally {
			conn.getInputStream().close();
		}
	}

	public static Document postWithRequest(String url, Map<String, String> data) throws IOException, ParserConfigurationException, SAXException {
		URL siteUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
		try {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			
			Set<String> keys = data.keySet();
			Iterator<String> keyIter = keys.iterator();
			String content = "";
			for (int i = 0; keyIter.hasNext(); i++) {
				String key = keyIter.next();
				if (i != 0) {
					content += "&";
				}
				content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
			}
			out.writeBytes(content);
			out.flush();
			out.close();
			InputStream in;
			try {
				in = conn.getInputStream();
			} catch (UnknownServiceException unknownServiceException) {
				return null;
			}
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			return document;
		} catch (IOException iOException) {
			throw iOException;
		} catch (ParserConfigurationException parserConfigurationException) {
			throw parserConfigurationException;
		} catch (SAXException sAXException) {
			throw sAXException;
		} finally {
			conn.getInputStream().close();
		}
	}
	
}
