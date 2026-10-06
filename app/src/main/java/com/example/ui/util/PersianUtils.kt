package com.example.ui.util

import java.text.NumberFormat
import java.util.Locale

object PersianUtils {
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(input: String): String {
        val builder = java.lang.StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                builder.append(persianDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun toPersianDigits(number: Long): String {
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return toPersianDigits(formatted)
    }

    fun toPersianDigits(number: Int): String {
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return toPersianDigits(formatted)
    }

    fun formatPrice(amountToman: Long): String {
        return when {
            amountToman >= 1_000_000_000L -> {
                val billions = amountToman.toDouble() / 1_000_000_000.0
                if (billions == billions.toLong().toDouble()) {
                    "${toPersianDigits(billions.toLong())} میلیارد تومان"
                } else {
                    "${toPersianDigits(String.format(Locale.US, "%.1f", billions))} میلیارد تومان"
                }
            }
            amountToman >= 1_000_000L -> {
                val millions = amountToman / 1_000_000L
                "${toPersianDigits(millions)} میلیون تومان"
            }
            else -> {
                "${toPersianDigits(amountToman)} تومان"
            }
        }
    }
}
