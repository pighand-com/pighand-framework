package com.pighand.spring.util;

/**
 * 字符串工具
 *
 * @author wangshuli
 */
public class StringUtil {

    /**
     * 转换成Unicode码
     *
     * @param str
     * @return
     */
    public static String toUnicode(String str) {
        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isChinese(c)) {
                unicode.append("\\u" + Integer.toHexString(c));
            } else {
                unicode.append(String.valueOf(c));
            }
        }

        return unicode.toString();
    }

    /**
     * 是否汉字或中文符号
     *
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
}
