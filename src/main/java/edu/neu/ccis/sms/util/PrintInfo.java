package edu.neu.ccis.sms.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.CompositeConfiguration;

public class PrintInfo {

	public static void printRequestParameters(HttpServletRequest request) {
		Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println(paramName + ":" + paramValue);   
        }
	}
	
	/**
	 * List all configuration properties
	 */
	public static void listAllConfigurationProperties(CompositeConfiguration config) {
		
		Iterator<String> keys = config.getKeys();
		while (keys.hasNext()) {
			String key = keys.next();
			System.out.print(key + ":");
			printKeyValue(key, config);
			System.out.println("");
		}
	}
	
	/**
	 * Print values for given key, even if it is a list
	 * @param key
	 */
	public static void printKeyValue(String key, CompositeConfiguration config) {
		Object property = config.getProperty(key);
		if(property instanceof Collection) {
			List<Object> properties = config.getList(key);
			for (Object propertyKey : properties) {
				System.out.print(propertyKey + ",");
			}
		} else {
			System.out.print(property);
		}
	}
	
	public void printHashSet(HashSet<String> setToPrint) {
		for (String category : setToPrint) {
		    System.out.println(category);
		}
	}
}
