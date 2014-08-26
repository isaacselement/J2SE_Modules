package j2se.modules.Introspector;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IntrospectHelper {
	
	private static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	// i.e. A.B.C -> return B
	public static String getParentPackageName(Object object) {
		String wholeClassName = object.getClass().getName();
		String packageParts[] = wholeClassName.split("\\.");
		
		int parentPackageIndex = packageParts.length - 2;
		return parentPackageIndex >= 0 ? packageParts[parentPackageIndex] : null;
	}
	
	public static String getWholePackageName(Object object) {
		String wholeClassName = object.getClass().getName();
		String packageName = wholeClassName.substring(0, wholeClassName.lastIndexOf("."));
		return packageName;
	}
	
	
	public static String getShortClassName(Object object) {
		return getShortClassName(object.getClass());
	}
	
	public static <T> String getShortClassName(Class<T> clazz) {
		String wholeClassName = clazz.getName();
		return getClassNameLastComponent(wholeClassName);
	}
	
	public static String getClassNameLastComponent(String wholeClassName) {
		return wholeClassName.substring(wholeClassName.lastIndexOf(".") + 1);
	}
	
	
	public static String getLongClassName(Object object) {
		return object.getClass().getName();
	}
	
	
	/**
	 * HumanResource.Employee{name,sex,birthday, ... }
	 * [com.xinyuan.model.Business.Client, com.xinyuan.model.Cards.CardsAlbums, ...] 
	 * -> 
	 * { Business={Client=[address, businessEmployee, ... ]}, Cards={CardsAlbums=[albumName, albumPassword, createDate, ..]}, ... }
	 */
	public static Map<String, Map<String, Map<String, String>>> translateToPropertiesMap(List<String> wholeClassNames) {
		Map<String, Map<String, Map<String, String>>> categoriesNamesTypesMap = new HashMap<String, Map<String, Map<String, String>>>();
		
		for (Iterator<String> iterator = wholeClassNames.iterator(); iterator.hasNext();) {
			String wholeClassName = iterator.next();
			String parts[] = wholeClassName.split("\\.");
			int length = parts.length;
			
			if (length < 2) continue;
			
			String className = parts[length - 1];			// Client
			String categoryName = parts[length - 2];		// Business
			
			// category map
			Map<String, Map<String, String>> categoryNameTypeMap =  categoriesNamesTypesMap.get(categoryName);  //  Business={Client=[address, businessEmployee, ... ]
			if (categoryNameTypeMap == null) {
			    categoryNameTypeMap = new HashMap<String, Map<String, String>>();
			    categoriesNamesTypesMap.put(categoryName, categoryNameTypeMap);
            }
			
			// class properties list
			Map<String, String> modelsMap = new HashMap<String, String>();   // {"address":"Street 1", "businessEmployee": "AE0001", ... }
			
			try {
				Class<?> classObj = Class.forName(wholeClassName);
				for (PropertyDescriptor pd : Introspector.getBeanInfo(classObj).getPropertyDescriptors()) {
					String propertyName = pd.getName() ;
					Class<?> propertyType = pd.getPropertyType();
					String typeString = propertyType.getName();
					String[] typeComponents = typeString.split("\\.");
					String type = typeComponents[typeComponents.length - 1];
					
					if (!isClassPropertyName(propertyName)) {
						modelsMap.put(propertyName, type);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			categoryNameTypeMap.put(className, modelsMap);
		}
		
		return categoriesNamesTypesMap;
	}
	
	public static Set<String> getAllProperties(Object object) throws Exception {
		Set<String> allProperties = new HashSet<String>();
		
		for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
			String propertyname = pd.getName() ;
			if (!isClassPropertyName(propertyname)) {
				allProperties.add(propertyname);
			}
		}
		return allProperties;
	}
	
	
	/**
	 * 
	 * @param object
	 * @return 	the object's properties and their types , map , key for properties name, type for value.
	 * @throws Exception
	 */
	public static Map<String, Class<?>> getPropertiesTypes(Object object) throws Exception {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		
		for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
			String propertyname = pd.getName() ;
			if (!isClassPropertyName(propertyname)) {
				map.put(propertyname, pd.getPropertyType());
			}
		}
		
		if (map.size() != 0) return map;
		else return null;
	}
	
	
	/**
	 *  
	 * @param fields
	 * @param key
	 * @return
	 */
	public static boolean isContains(Set<String> keys , String key) {
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equals(key)) return true; 
		}
		return false;
	}
	
	
	  /**
	   * 
	   * @param value
	   * @param classObj
	   * @return
	   * @throws ParseException
	   */
	public static Object convert(Object value, Class<?> classObj) throws Exception {
		String valueString = (String) value;

		if (classObj == java.util.Date.class) {
			
			return new SimpleDateFormat(DateTimeFormat).parse(valueString);
			
		} else if (classObj == Boolean.class) {

			return valueString.equals("true");
			
		} else if (classObj == float.class) {
			
			return Float.parseFloat(valueString);
			
		} else if (classObj == int.class) {
			
			return Integer.parseInt(valueString);
			
		}

		return value;
	}
	
	
	public static String objectToString(Object object) {
		String toString = "";
		try {
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
			
			for (PropertyDescriptor pd : propertyDescriptors) {
				String propertyname = pd.getName() ;
				if (isClassPropertyName(propertyname)) continue ;
				Method readMethod = pd.getReadMethod();
				Object value = readMethod.invoke(object);
				if (value != null) toString += propertyname + ": " + value.toString() + ", ";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toString;
	}
	
	
	public static boolean isClassPropertyName(String propertyname) {
//		"class".equals(propertyname)
		return "class".equalsIgnoreCase(propertyname);
	}

}
