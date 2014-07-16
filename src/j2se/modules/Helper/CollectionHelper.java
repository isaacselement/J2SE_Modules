package j2se.modules.Helper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CollectionHelper {
	
	public static void removeStartWithElement(Set<String> set , String startWith) {
		Set<String> tempSet = new HashSet<String>();
		tempSet.addAll(set);
		for (String value : tempSet) {
			if (value.startsWith(startWith)) {
				set.remove(value);
			}
		}
	}
	
	
	public static void removeNotStartWithElement(Set<String> set , String notStartWith) {
		Set<String> tempSet = new HashSet<String>();
		tempSet.addAll(set);
		for (String value : tempSet) {
			if (!value.startsWith(notStartWith)) {
				set.remove(value);
			}
		}
	}
	
	
	public static boolean isContains(List<String> list, String element){
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (string.equals(element)) {
				return true;
			}
		}
		return false;
	}
	
	
	public static void removeElement(List<String> list, String element){
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (string.equals(element)) {
				iterator.remove();
			}
		}
	}

	
	@SuppressWarnings("unchecked")
    public static void combineTowMap(Map<String, Object> source, Map<String, Object> destination) {
		for(Map.Entry<String, Object> entry : source.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) continue;
			
			Object desValue = destination.get(key);
			
			if ( desValue != null && desValue instanceof Map && value instanceof Map) {
				CollectionHelper.combineTowMap((Map<String, Object>)value, (Map<String, Object>)desValue);
			} else {
				 destination.put(key, value);
				
			}
		}
	}
}
