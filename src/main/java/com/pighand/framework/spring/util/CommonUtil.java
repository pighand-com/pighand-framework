package com.pighand.framework.spring.util;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 公共工具类
 *
 * @author wangshuli
 */
public class CommonUtil {

    /**
     * 通过 HttpServletRequest 返回IP地址
     *
     * @param request
     * @return IP
     */
    public static String getIp(HttpServletRequest request) {
        String unknown = "unknown";
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果是多级代理取第一个IP地址
        String multilevelFlag = ",";
        if (ip != null && ip.indexOf(multilevelFlag) != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        return ip;
    }

    /**
     * 设置class的默认值
     *
     * @param clz 要设置值得class
     * @return
     */
    public static Object setDefValue(Class clz) {
        return setDefValue(clz, null);
    }

    /**
     * 设置class的默认值
     *
     * @param clz 要设置值得class
     * @param genericityClz List类型的泛型
     * @return
     */
    public static Object setDefValue(Class clz, Class genericityClz) {
        String clzName = clz.getName();

        if (clzName.equals(Object.class.getName())) {
            return "Object";
        } else if (clzName.equals(String.class.getName())) {
            return "String";
        } else if (clzName.equals(Boolean.class.getName())
                || boolean.class.getName().equals(clzName)) {
            return true;
        } else if (clzName.equals(Byte.class.getName())
                || clzName.equals(Short.class.getName())
                || clzName.equals(Integer.class.getName())
                || clzName.equals(Long.class.getName())
                || byte.class.getName().equals(clzName)
                || short.class.getName().equals(clzName)
                || int.class.getName().equals(clzName)
                || long.class.getName().equals(clzName)) {
            return 0;
        } else if (clzName.equals(Float.class.getName())
                || clzName.equals(Double.class.getName())) {
            return 0.0;
        } else if (clzName.equals(List.class.getName())
                || clzName.equals(ArrayList.class.getName())
                || clzName.equals(Set.class.getName())) {
            List<Object> list = new ArrayList<>();
            if (null != genericityClz) {
                list.add(setDefValue(genericityClz, null));
                list.add(setDefValue(genericityClz, null));
            }
            return list;
        } else if (clzName.equals(Map.class.getName()) || clzName.equals(HashMap.class.getName())) {
            Map<String, String> map = new HashMap<>(2);
            map.put("key1", "value1");
            map.put("key2", "value2");
            return map;
        } else if (clzName.equals(Date.class.getName())) {
            return new Date();
        } else {
            Object reObj = null;
            try {
                reObj = clz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return "Object";
            }

            Method[] methods = clz.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length != 1) {
                    continue;
                }

                try {
                    method.invoke(reObj, setDefValue(method.getParameterTypes()[0], genericityClz));
                } catch (Exception e) {
                    continue;
                }
            }

            if (null == reObj) {
                return "Object";
            }

            return reObj;
        }
    }
}
