package com.raj.slotify.tools

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.text.format


object FormatUtils {
    fun formatDate(localDate: LocalDate): String {
        return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun formatTime(localTime: LocalTime): String {
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun formatPrice(priceCent: Int): String {
        return String.format("%.2f €", priceCent / 100.0)
    }
}
