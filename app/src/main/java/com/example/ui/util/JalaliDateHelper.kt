package com.example.ui.util

import java.util.Calendar
import java.util.TimeZone

object JalaliDateHelper {

    data class JalaliDate(val year: Int, val month: Int, val day: Int, val hour: Int = 12, val minute: Int = 0)

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = if (gm > 2) gy + 1 else gy
        var gDayNo = 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 - 80
        for (i in 0 until (gm - 1)) {
            gDayNo += gDaysInMonth[i]
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..10) {
            if (jDayNo >= jDaysInMonth[i]) {
                jDayNo -= jDaysInMonth[i]
                jm++
            } else {
                break
            }
        }
        val jd = jDayNo + 1
        return JalaliDate(jy, jm + 1, jd)
    }

    fun formatJalaliDateTime(timestamp: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
        cal.timeInMillis = timestamp
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val jDate = gregorianToJalali(gy, gm, gd)
        val jYearStr = PersianUtils.toPersianDigits(jDate.year.toString())
        val jMonthStr = PersianUtils.toPersianDigits(String.format("%02d", jDate.month))
        val jDayStr = PersianUtils.toPersianDigits(String.format("%02d", jDate.day))
        val hourStr = PersianUtils.toPersianDigits(String.format("%02d", hour))
        val minuteStr = PersianUtils.toPersianDigits(String.format("%02d", minute))

        return "$jYearStr/$jMonthStr/$jDayStr — $hourStr:$minuteStr"
    }

    fun formatJalaliDateOnly(timestamp: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
        cal.timeInMillis = timestamp
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)

        val jDate = gregorianToJalali(gy, gm, gd)
        val jYearStr = PersianUtils.toPersianDigits(jDate.year.toString())
        val jMonthStr = PersianUtils.toPersianDigits(String.format("%02d", jDate.month))
        val jDayStr = PersianUtils.toPersianDigits(String.format("%02d", jDate.day))

        return "$jYearStr/$jMonthStr/$jDayStr"
    }

    fun formatTimeOnly(timestamp: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
        cal.timeInMillis = timestamp
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val hourStr = PersianUtils.toPersianDigits(String.format("%02d", hour))
        val minuteStr = PersianUtils.toPersianDigits(String.format("%02d", minute))
        return "$hourStr:$minuteStr"
    }

    fun formatRelativeTime(timestamp: Long): String {
        val diffMillis = System.currentTimeMillis() - timestamp
        val diffMinutes = diffMillis / (60 * 1000)
        val diffHours = diffMillis / (60 * 60 * 1000)
        val diffDays = diffMillis / (24 * 60 * 60 * 1000)

        return when {
            diffMinutes < 5 -> "دقایقی پیش"
            diffMinutes < 60 -> "${PersianUtils.toPersianDigits(diffMinutes.toInt())} دقیقه پیش"
            diffHours < 24 -> "${PersianUtils.toPersianDigits(diffHours.toInt())} ساعت پیش"
            diffDays == 1L -> "دیروز"
            diffDays < 7 -> "${PersianUtils.toPersianDigits(diffDays.toInt())} روز پیش"
            else -> formatJalaliDateOnly(timestamp)
        }
    }
}
