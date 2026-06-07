package com.raj.slotify.tools

import android.content.Context
import com.raj.slotify.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.text.format


object TextUtils {
    fun formatDate(localDate: LocalDate): String {
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun formatTime(localTime: LocalTime): String {
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun formatPrice(priceCent: Int): String {
        return String.format("%.2f €", priceCent / 100.0)
    }

    fun calculateCompletedReserve(endTime: LocalDateTime): Boolean {
        val now: LocalDateTime = LocalDateTime.now()
        val reserveEndDuration = java.time.Duration.between(now, endTime)

        return reserveEndDuration.isNegative
    }

    fun formatRemainingText(startTime: LocalDateTime, endTime: LocalDateTime, context: Context): String {
        // SET REMAINING TEXT
        val now: LocalDateTime = LocalDateTime.now()

        val duration = java.time.Duration.between(now, startTime)
        val reserveEndDuration = java.time.Duration.between(now, endTime)

        if ((duration.isNegative || duration.isZero) && !(reserveEndDuration.isNegative || reserveEndDuration.isZero)) {
            return context.getString(R.string.already_started)
        } else if (reserveEndDuration.isNegative) {
            return context.getString(R.string.completed_word).replaceFirstChar{it.uppercase()}
        }

        val remainingDays = duration.toDays()
        val remainingHours = duration.toHours()
        val remainingMinutes = duration.toMinutes()

        return when {
            remainingDays > 0 -> " $remainingDays ${
                if (remainingDays > 1) context.getString(
                    R.string.days_word
                ) else context.getString(R.string.day_word)
            } ${context.getString(R.string.left_after)} "

            remainingHours > 0 -> " $remainingHours ${
                if (remainingHours > 1) context.getString(
                    R.string.hours_word
                ) else context.getString(R.string.hour_word)
            } ${context.getString(R.string.left_after)} "

            else -> " $remainingMinutes ${
                if (remainingMinutes > 1) context.getString(R.string.minutes_word) else context.getString(
                    R.string.minute_word
                )
            } ${context.getString(R.string.left_after)} "
        }
    }
}
