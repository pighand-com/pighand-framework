package com.pighand.framework.spring.util;

/**
 * 随机码类型
 *
 * @author wangshuli
 */
public enum CodeType {

    //  数字
    NUMBER,

    // 小写字母
    LOWERCASE,

    // 大写字母
    CAPITAL,

    // 大小写随机字母
    LETTER,

    // 数字和小写字母
    NUMBER_LOWERCASE_LETTER,

    // 数字和大写字母
    NUMBER_CAPITAL_LETTER,

    // 数字和字母
    NUMBER_LETTER;

    public static CodeType get(Integer value) {
        return switch (value) {
            default -> NUMBER;
            case 2 -> LOWERCASE;
            case 3 -> CAPITAL;
            case 4 -> LETTER;
            case 5 -> NUMBER_LOWERCASE_LETTER;
            case 6 -> NUMBER_CAPITAL_LETTER;
            case 7 -> NUMBER_LETTER;
        };
    }
}
