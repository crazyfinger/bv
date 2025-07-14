package dev.aaa1115910.bv.util

import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.formatHourMinSec(): String {
    return if (this < 0L) {
        "00:00"
    } else {
        val hours = TimeUnit.MILLISECONDS.toHours(this)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(this)
        )

        if (hours > 0) {
            String.format(Locale.PRC, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.PRC, "%02d:%02d", minutes, seconds)
        }
    }
}

fun Long.formatMinSec(): String {
    return if (this < 0L) {
        "00:00"
    } else {
        String.format(
            Locale.PRC,
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}
