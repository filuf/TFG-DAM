package com.raj.slotify.models

import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.chip.Chip
import java.time.DayOfWeek

data class ChipComponent(
    val dayOfWeek: DayOfWeek,
    val chip: Chip,
    val textStart: TextView,
    val textEnd: TextView,
    val layout: LinearLayout
)
