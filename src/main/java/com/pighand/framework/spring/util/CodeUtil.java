package com.pighand.framework.spring.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 随机码
 *
 * @author wangshuli
 */
public class CodeUtil {

    private static final char[] NUMBER_LIST = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] CAPITAL_LIST = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U',
        'V', 'W', 'X', 'Y', 'Z'
    };
    private static final char[] LOWERCASE_LIST = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z'
    };
    private static final char[] LETTER = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private static final char[] ALL = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
        'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C',
        'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z'
    };

    /**
     * 验证码
     *
     * @param type 类型
     * @param number 个数
     * @return 随机验证码
     */
    public static String randomCode(CodeType type, int number) {
        char[] codeList = null;
        switch (type) {
            case NUMBER:
                codeList = NUMBER_LIST;
                break;
            case LETTER:
                codeList = LETTER;
                break;
            case LOWERCASE:
                codeList = LOWERCASE_LIST;
                break;
            case CAPITAL:
                codeList = CAPITAL_LIST;
                break;
            default:
                codeList = ALL;
                break;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < number; i++) {
            int idx = (int) (Math.random() * codeList.length);
            str.append(codeList[idx]);
        }

        String code = str.toString();

        if (type.equals(CodeType.NUMBER_LOWERCASE_LETTER)) {
            code = code.toLowerCase();
        } else if (type.equals(CodeType.NUMBER_CAPITAL_LETTER)) {
            code = code.toUpperCase();
        }

        return code;
    }

    /**
     * 验证码图片
     *
     * @return code:验证码字符串 image:BufferedImage图片对象
     */
    public static Map<String, Object> codeImg() {
        int width = 140;
        int height = 42;
        Map<String, Object> result = new HashMap<>(2);

        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();

        Random random = new Random();
        int red = 0, green = 0, blue = 0;

        // 色块
        int count = 1;
        int x = 0;
        int y = 0;
        // 色块大小
        int lump = 14;
        // 色块总数
        int lumpMax = 31;
        while (count < lumpMax) {

            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            gd.setColor(new Color(red, green, blue));
            gd.fillRect(x, y, lump, lump);

            if (count % 10 == 0) {
                x = 0;
                y += lump;
            } else {
                x += lump;
            }
            count++;
        }

        // 设置字体。
        ((Graphics2D) gd)
                .setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        Font font = new Font("", Font.BOLD, 34);
        gd.setFont(font);

        // 验证码
        int codeNum = 4;
        StringBuilder codeStr = new StringBuilder();
        for (int i = 0; i < codeNum; i++) {
            String code = String.valueOf(ALL[random.nextInt(ALL.length)]);
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * 23, 33);

            codeStr.append(code);
        }

        result.put("code", codeStr.toString());
        result.put("image", buffImg);

        return result;
    }
}
