package com.ts.test.util;

import com.ts.test.bean.Video;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Common {
    public static final String EMPTY_STRING = "";

    public static final String TITLE_DISK = "SD卡";

    public static final String MUSIC_LIST_DESC_SEPARATOR = " - ";

    public static final String[] TAB_TITLES = {
            "音乐",
            "视频"
    };

    public static final List<String> MUSIC_TYPES = List.of(new String[]{
            "mp3", "wav", "ae", "flac", "m4a", "ac3"
    });

    public static final List<String> VIDEO_TYPES = List.of(new String[]{
            "avi", "mov", "3gp", "mp4", "mpeg2", "m4v", "mkv", "mpg"
    });
    public static final String DEFAULT_TITLE = "未知";

    public static boolean isEmpty(String str) {
        return null == str || EMPTY_STRING.equals(str);
    }

    public static boolean isDefault(String str) {
        return isEmpty(str) || DEFAULT_TITLE.equals(str);
    }

    public static final List<Character> SYMBOL_SEQUENCE = List.of('_', '-', '!', '\'', '(', ')',
            '[', ']', '{', '}', '【', '】', '@', '&', '#', '%', '`', '^', '+', '=', '$', '￥');
    public static final List<Character> NUMBER_SEQUENCE = List.of(new Character[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    });
    public static final List<Character> LETTER_SEQUENCE = List.of('a', 'A', 'b', 'B', 'c', 'C', 'd',
            'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l', 'L',
            'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S', 't', 'T', 'u',
            'U', 'v', 'V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z');
    public static final int ORDER_SYMBOL = 0;
    public static final int ORDER_NUMBER = 1;
    public static final int ORDER_CHINESE = 2;
    public static final int ORDER_LETTER = 3;
    public static final int ORDER_OTHER = 4;

    /**
     * 是否是汉字
     *
     * @param c 字符
     * @return
     */
    public static boolean isChineseChar(char c) {
        return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }

    /**
     * 获取字符类型
     *
     * @param c 字符
     * @return
     */
    public static int getCharType(char c) {
        if (SYMBOL_SEQUENCE.contains(c)) {
            return ORDER_SYMBOL;
        } else if (NUMBER_SEQUENCE.contains(c)) {
            return ORDER_NUMBER;
        } else if (isChineseChar(c)) {
            return ORDER_CHINESE;
        } else if (LETTER_SEQUENCE.contains(c)) {
            return ORDER_LETTER;
        }
        return ORDER_OTHER;
    }

    /**
     * 同类型字符比较顺序
     *
     * @param a 字符 a
     * @param b 字符 b
     * @param type 类型
     * @return
     */
    public static int compareType(char a, char b, int type) {
        switch (type) {
            case ORDER_SYMBOL:
                return SYMBOL_SEQUENCE.indexOf(a) - SYMBOL_SEQUENCE.indexOf(b);
            case ORDER_NUMBER:
                return NUMBER_SEQUENCE.indexOf(a) - NUMBER_SEQUENCE.indexOf(b);
            case ORDER_CHINESE:
                Collator collator = Collator.getInstance(Locale.CHINA);
                CollationKey key1 = collator.getCollationKey(String.valueOf(a));
                CollationKey key2 = collator.getCollationKey(String.valueOf(b));
                return key1.compareTo(key2);
            case ORDER_LETTER:
                return LETTER_SEQUENCE.indexOf(a) - LETTER_SEQUENCE.indexOf(b);
            default:
                return 0;
        }
    }

    /**
     * 排序
     *
     * @param list 数据
     */
    public static void sort(List<Video> list) {
        Collections.sort(list, (Comparator<Video>) (o1, o2) -> {
            int o1Length = o1.getFileName().length();
            int o2Length = o2.getFileName().length();
            for (int i = 0; i < Math.min(o1Length, o2Length); i++) {
                if (getCharType(o1.getFileName().charAt(i)) == getCharType(o2.getFileName().charAt(i))) {
                    if (compareType(o1.getFileName().charAt(i), o2.getFileName().charAt(i), getCharType(o1.getFileName().charAt(i))) != 0) {
                        return compareType(o1.getFileName().charAt(i), o2.getFileName().charAt(i), getCharType(o1.getFileName().charAt(i)));
                    }
                } else {
                    return getCharType(o1.getFileName().charAt(i)) - getCharType(o2.getFileName().charAt(i));
                }
            }
            return o1Length - o2Length;
        });
    }
}
