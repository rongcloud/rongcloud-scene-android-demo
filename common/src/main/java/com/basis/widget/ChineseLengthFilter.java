package com.basis.widget;


import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by gyn on 2021/11/17
 */
public class ChineseLengthFilter implements InputFilter {

    private int charLength;

    public ChineseLengthFilter(int charLength) {
        this.charLength = charLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int len = calculateLength(dest);

        StringBuilder builder = new StringBuilder();
        if (len >= charLength) {
            return "";
        } else {
            for (char c : source.toString().toCharArray()) {
                if (isChineseChar(c)) {
                    len += 2;
                } else {
                    len++;
                }
                builder.append(c);
                if (len >= charLength) {
                    break;
                }
            }
        }
        return builder.toString().trim();
    }

    private boolean isChineseChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION);
    }

    private int calculateLength(Spanned source) {
        int len = 0;
        for (char c : source.toString().toCharArray()) {
            if (isChineseChar(c)) {
                len += 2;
            } else {
                len++;
            }
        }
        return len;
    }
}
