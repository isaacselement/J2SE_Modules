package j2se.modules.Helper;

import java.util.Iterator;
import java.util.Map;

public class DLog {
	
	public static boolean isDebugingMode = true;		// Set it to no when in production
	
	public static void log(String message) {
		if (! isDebugingMode) return; 
		StackTraceElement[] elements = new Throwable().getStackTrace();
		String classNameString = elements[1].getClassName();
		println("\n ----- " + "[" + classNameString + "]" + "  " + message);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void printMapKeysValues(Map map) {
	    if (! isDebugingMode) return;
	    Iterator iterator = map.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry pairs = (Map.Entry)iterator.next();
	        String key = (String) pairs.getKey(); 
	        Object value = pairs.getValue();
	        println(key + " = " + value);
	        iterator.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	
	public static void println(String message) {
		if (! isDebugingMode) return; 
		System.out.println(message);
	}
	
	
}
