package cn.rong.combusis.common.utils

import android.text.InputFilter
import android.text.Spanned

/**
 * Created by hugo on 2021/11/5
 * @param charLength 英文字符的长度
 */
class ChineseLengthFilter(private val charLength: Int) : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var len = calculateLength(dest)

        val builder = StringBuilder()
        if (len >= charLength) {
            return ""
        } else {
            for (c in source) {
                if (isChineseChar(c)) {
                    len += 2
                } else {
                    len++
                }
                builder.append(c)
                if (len >= charLength) {
                    break
                }
            }
        }
        return builder.toString().trim()
    }

    private fun isChineseChar(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION)
    }

    private fun calculateLength(source: Spanned): Int {
        var len = 0
        for (c in source) {
            if (isChineseChar(c)) {
                len += 2
            } else {
                len++
            }
        }
        return len
    }
}