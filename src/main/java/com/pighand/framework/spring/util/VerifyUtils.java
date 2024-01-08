package com.pighand.framework.spring.util;

import com.pighand.framework.spring.exception.ThrowPrompt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具
 *
 * @author wangshuli
 */
public class VerifyUtils {

    /**
     * 判断对象是否为空 对象为null，字符序列长度为0，集合类、Map为empty
     *
     * @param obj
     * @return null或空返回true
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }

        // 字符串类型，并且值是null或空
        boolean isStringNull = obj instanceof String && "".equals(((String)obj).trim());

        if (isStringNull) {
            return true;
        } else if (obj instanceof Long) {
            return ((Long)obj) <= 0;
        } else if (obj instanceof Integer) {
            return ((Integer)obj) <= 0;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj instanceof Collection) {
            if (((Collection<?>)obj).isEmpty()) {
                return true;
            } else {
                boolean isNotColEmpty = false;

                for (Object colObj : (Collection<?>)obj) {
                    if (!isEmpty(colObj)) {
                        isNotColEmpty = true;
                        break;
                    }
                }

                return !isNotColEmpty;
            }
        } else if (obj instanceof Map) {
            return ((Map<?, ?>)obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * 判断对象是否非空 对象为null，字符序列长度为0，集合类、Map为empty
     *
     * @param obj
     * @return null或空返回false
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 验证IP4地址是否合法
     *
     * @param ipString
     * @return true 格式正确 false 格式错误
     */
    public static Boolean isIp4(String ipString) {
        String regex =
            "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipString);
        return matcher.matches();
    }

    /**
     * 验证邮箱格式
     *
     * @param mail 邮箱
     * @return true 格式正确 false 格式错误
     */
    public static boolean verifyMail(String mail) {
        String regex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mail);
        return m.matches();
    }

    /**
     * 分组校验参数
     *
     * @param voObject
     * @return key:参数名 value:错误信息
     */
    public static void validateParams(Object voObject, Class<?>... groupClass) {
        Set<ConstraintViolation<Object>> violations = getViolations(voObject, groupClass);

        Map<String, String> validation = violationToMap(violations);

        if (validation != null) {
            throw new ThrowPrompt("参数错误", validation);
        }
    }

    /**
     * 校验参数
     *
     * @param voObject
     * @param isErrorString 错误信息以字符串形式返回，否则已map形式返回
     */
    public static void validateParams(Object voObject, boolean isErrorString, Class<?>... groupClass) {
        Set<ConstraintViolation<Object>> violations = getViolations(voObject, groupClass);

        if (isErrorString) {
            String validation = violationToString(violations);
            if (validation != null) {
                throw new ThrowPrompt(validation);
            }
        } else {
            Map<String, String> validation = violationToMap(violations);
            if (validation != null) {
                throw new ThrowPrompt("参数错误", validation);
            }
        }
    }

    /**
     * 校验参数
     *
     * @param violations
     * @return key:参数名 value:错误信息
     */
    private static Map<String, String> violationToMap(Set<ConstraintViolation<Object>> violations) {
        Map<String, String> failMessages = new HashMap<>(violations.size());
        violations.forEach(violation -> {
            String propertyName = violation.getPropertyPath().toString();

            failMessages.put(propertyName, violation.getMessage());
        });

        if (failMessages.size() > 0) {
            return failMessages;
        }

        return null;
    }

    /**
     * 校验参数
     *
     * @param violations
     * @return key:参数名 value:错误信息
     */
    private static String violationToString(Set<ConstraintViolation<Object>> violations) {
        StringBuilder sb = new StringBuilder();
        violations.forEach(violation -> {
            String propertyName = violation.getPropertyPath().toString();

            sb.append(propertyName).append(violation.getMessage()).append("; ");
        });

        if (!sb.isEmpty()) {
            return sb.toString();
        }

        return null;
    }

    /**
     * 校验信息
     *
     * @param voObject
     * @param groupClass
     * @return
     */
    private static Set<ConstraintViolation<Object>> getViolations(Object voObject, Class<?>... groupClass) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(voObject, groupClass);
    }
}
