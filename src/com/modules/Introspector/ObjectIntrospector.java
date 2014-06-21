package com.modules.Introspector;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class ObjectIntrospector {

    public static void setProperty(Object object, Map<String, ?> keyValues) throws Exception {
        for (Map.Entry<String, ?> entry : keyValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            setProperty(object, key, value);
        }
    }

    public static void setProperty(Object object, String propertyName, Object value) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals(propertyName)) {
                Method writeMethod = pd.getWriteMethod();

                // Class<?> type = writeMethod.getParameterTypes()[0];

                writeMethod.invoke(object, value);
                break;
            }
        }
    }

    public static Object getProperty(Object object, String propertyName) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        Object retValue = null;
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals(propertyName)) {
                Method methodGetX = pd.getReadMethod();
                retValue = methodGetX.invoke(object);
                break;
            }
        }
        return retValue;
    }

    public static Class<?> getPropertyType(Object object, String propertyName) throws Exception {
        return getPropertyType(object.getClass(), propertyName);
    }

    public static Class<?> getPropertyType(Class<?> clazz, String propertyName) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        Class<?> retValue = null;
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals(propertyName)) {
                retValue = pd.getPropertyType();
                break;
            }
        }
        return retValue;
    }

    /**
     * no recursive , just one deep depth, need to extend
     * 
     * @param vo
     *            the vo object
     * @param po
     *            the po object
     * @param keys
     *            specified the fields you want to copy
     * @throws Exception
     */
    public static void copyVoToPo(Object vo, Object po, Set<String> keys) throws Exception {

        for (PropertyDescriptor pd : Introspector.getBeanInfo(po.getClass()).getPropertyDescriptors()) {

            if (pd.getReadMethod() != null && !IntrospectHelper.isClassPropertyName(pd.getName())) {

                String propertyname = pd.getName();

                if (IntrospectHelper.isContains(keys, propertyname)) {
                    Method readMethod = pd.getReadMethod();
                    Object value = readMethod.invoke(vo);

                    Method writeMethod = pd.getWriteMethod();
                    writeMethod.invoke(po, value);
                }
            }
        }
    }
}
