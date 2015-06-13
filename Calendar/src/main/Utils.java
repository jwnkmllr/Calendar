package main;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class Utils {
	private static final String ENC = "UTF-8";
	
	
	public static String stringToURI(String s) {
		try {
			return "data://" + URLEncoder.encode(s, ENC);
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Invalid encoding.");
		}
	}
	
	public static String uriToString(String uri) {
		if ( !uri.startsWith("data://") ) {
			throw new IllegalArgumentException("Invalid format.");
		}
		try {
			return URLDecoder.decode(uri.substring(7), ENC);
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Invalid encoding.");
		}
	}
}
