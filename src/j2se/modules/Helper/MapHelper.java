package j2se.modules.Helper;

import java.util.HashMap;
import java.util.Map;

public class MapHelper {

    public static Map<String, Object> extractToOneDimensionMap(Map<String, Map<String, Object>> map) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        for(Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
//            String key = entry.getKey();
            Map<String, Object> value = entry.getValue();
            for(Map.Entry<String, Object> element : value.entrySet()) {
                String elementKey = element.getKey();
                Object elementValue = element.getValue();
                result.put(elementKey, elementValue);
            }
        }
        
        return result;
    }
}
